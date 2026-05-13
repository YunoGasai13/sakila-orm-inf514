package com.sakila.model;

import com.sakila.data.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Modelo PaymentModel - hijo CONCRETO y FINAL de DataContext.
 * Gestiona el ArrayList de objetos Payment con CRUD completo.
 * Incluye metodos estadisticos: getTotalAmount(), getAverageAmount().
 *
 * FK customer_id resuelta por agregacion de objeto Customer.
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class PaymentModel extends DataContext implements iDatapost {

    /** ArrayList con los pagos cargados en memoria */
    private ArrayList<Payment> allData;
    /** Modelo auxiliar para resolver FK customer por agregacion */
    private CustomerModel customerModel;

    /**
     * Constructor: configura DataContext para la tabla payment.
     */
    public PaymentModel() {
        super("payment","payment_id","payment_id","customer_id","payment_date DESC","payment_date");
        customerModel = new CustomerModel();
    }

    /**
     * Mapea ResultSet a objetos Payment.
     * Resuelve FK customer_id por agregacion de objeto Customer.
     *
     * @param rSet ResultSet de la tabla payment
     */
    @Override
    public void Mapping(ResultSet rSet) {
        allData = new ArrayList<>();
        if (rSet == null) return;
        // Precarga clientes en memoria para resolver FK sin N+1 queries
        if (customerModel.getData()==null||customerModel.getData().isEmpty())
            customerModel.Get(true);
        try {
            while (rSet.next()) {
                int custId = rSet.getInt("customer_id");
                Customer objCust = (Customer) customerModel.inMemSearch(custId);
                allData.add(new Payment(
                    rSet.getInt("payment_id"),
                    objCust != null ? objCust : new Customer(),
                    rSet.getInt("staff_id"),
                    rSet.getInt("rental_id"),
                    rSet.getBigDecimal("amount"),
                    rSet.getDate("payment_date"),
                    rSet.getDate("last_update")));
            }
            rSet.close();
        } catch (SQLException e) { actionMessage = "Mapping Payment: " + e.getMessage(); }
    }

    @Override public ArrayList<Payment> Get()                  { Mapping(super.Find());       return allData; }
    @Override public ArrayList<Payment> Get(Object id)         { Mapping(super.Find(id));     return allData; }
    @Override public ArrayList<Payment> Get(String s)          { Mapping(super.Find(s));      return allData; }
    @Override public ArrayList<Payment> Get(String s, Object f){ Mapping(super.Find(s,f));    return allData; }
    @Override public ArrayList<Payment> Get(Date d1, Date d2)  { Mapping(super.Find(d1,d2));  return allData; }
    @Override public ArrayList<Payment> getData()               { return allData; }

    /**
     * Obtiene todos los pagos (hasta 5000) para reportes globales.
     * @param full true para modo completo
     */
    public ArrayList<Payment> Get(boolean full) { Mapping(super.Find(full)); return allData; }

    @Override
    public boolean Post(Entity o) {
        Payment p = (Payment)o;
        p.paymentId   = (int)(getMaxID()+1);
        p.paymentDate = new Date();
        p.lastUpdate  = new Date();
        return super.dbPost(SerializerMap(p));
    }

    @Override public boolean Put(Entity o) { return super.dbPut(SerializerMap(o)); }

    /** Payments no se eliminan; retorna false indicando no soportado. */
    @Override public boolean Delete(Entity o) { return false; }

    @Override
    public HashMap<String,String> SerializerMap(Entity o) {
        Payment p = (Payment)o;
        int custId = (p.objCustomer != null) ? p.objCustomer.customerId : 0;
        HashMap<String,String> m = new HashMap<>();
        m.put("payment_id",  String.valueOf(p.paymentId));
        m.put("customer_id", String.valueOf(custId));
        m.put("staff_id",    String.valueOf(p.staffId > 0 ? p.staffId : 1));
        m.put("rental_id",   String.valueOf(p.rentalId));
        m.put("amount",      p.amount != null ? p.amount.toString() : "0.00");
        m.put("payment_date",Entity.getCurrentDate().toString());
        m.put("last_update", Entity.getCurrentDate().toString());
        return m;
    }

    @Override
    public String Serializer() {
        if(allData==null||allData.isEmpty()) return "[]";
        StringBuilder sb=new StringBuilder("["); char sep=' ';
        for(Payment p:allData){
            String cust=(p.objCustomer!=null)?p.objCustomer.firstName+" "+p.objCustomer.lastName:"N/A";
            sb.append(sep).append("{payment_id:").append(p.paymentId)
              .append(",customer:\"").append(cust)
              .append("\",amount:").append(p.amount)
              .append(",date:\"").append(p.paymentDate).append("\"}");
            sep=',';
        }
        return sb.append("]").toString();
    }

    @Override
    public String SerializerCSV() {
        if(allData==null||allData.isEmpty()) return "payment_id,customer_id,amount,payment_date\n(sin datos)";
        StringBuilder sb=new StringBuilder("payment_id,customer_id,amount,payment_date\n");
        for(Payment p:allData){
            int cid=(p.objCustomer!=null)?p.objCustomer.customerId:0;
            sb.append(p.paymentId).append(",").append(cid).append(",")
              .append(p.amount).append(",").append(p.paymentDate).append("\n");
        }
        return sb.toString();
    }

    @Override
    public Entity inMemSearch(Object pid) {
        if(allData==null) return null;
        int id=Integer.parseInt(pid.toString());
        List<Payment> f=allData.stream().filter(p->p.paymentId==id).collect(Collectors.toList());
        return f.isEmpty()?null:f.get(0);
    }

    // ─── Metodos estadisticos para reportes ───────────────────────────────────

    /**
     * Calcula el total de pagos de la lista actual en memoria.
     * Usar despues de Get() o Get(fecha, fecha).
     *
     * @return BigDecimal con la suma total de todos los pagos
     */
    public BigDecimal getTotalAmount() {
        if(allData==null) return BigDecimal.ZERO;
        return allData.stream()
               .map(p->p.amount!=null?p.amount:BigDecimal.ZERO)
               .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el promedio de pagos de la lista actual.
     *
     * @return BigDecimal con el promedio, ZERO si no hay datos
     */
    public BigDecimal getAverageAmount() {
        if(allData==null||allData.isEmpty()) return BigDecimal.ZERO;
        return getTotalAmount().divide(
               BigDecimal.valueOf(allData.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    @Override public void close() { super.close(); customerModel.close(); }
}
