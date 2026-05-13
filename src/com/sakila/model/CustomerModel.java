package com.sakila.model;

import com.sakila.data.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * INF514 Z06 | CustomerModel - hijo FINAL de DataContext | tabla customer
 * Delete() = soft delete via campo active (active=0 = inactivo)
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 */
public final class CustomerModel extends DataContext implements iDatapost {

    private ArrayList<Customer> allData;

    public CustomerModel() {
        super("customer", "customer_id",
              "CONCAT(first_name,' ',last_name,' ',email)",
              "store_id", "last_name, first_name", "create_date");
    }

    @Override
    public void Mapping(ResultSet rSet) {
        allData = new ArrayList<>();
        if (rSet == null) return;
        try {
            while (rSet.next())
                allData.add(new Customer(
                    rSet.getInt("customer_id"), rSet.getInt("store_id"),
                    rSet.getString("first_name"), rSet.getString("last_name"),
                    rSet.getString("email"), rSet.getInt("address_id"),
                    rSet.getBoolean("active"),
                    rSet.getDate("create_date"), rSet.getDate("last_update")));
            rSet.close();
        } catch (SQLException e) { actionMessage = "Mapping Customer: " + e.getMessage(); }
    }

    @Override public ArrayList<Customer> Get()                  { Mapping(super.Find());       return allData; }
    @Override public ArrayList<Customer> Get(Object id)         { Mapping(super.Find(id));     return allData; }
    @Override public ArrayList<Customer> Get(String s)          { Mapping(super.Find(s));      return allData; }
    @Override public ArrayList<Customer> Get(String s, Object f){ Mapping(super.Find(s,f));    return allData; }
    @Override public ArrayList<Customer> Get(Date d1, Date d2)  { Mapping(super.Find(d1,d2));  return allData; }
    @Override public ArrayList<Customer> getData()               { return allData; }

    /** Obtiene hasta 5000 clientes para precarga (usado por RentalModel/PaymentModel). */
    public ArrayList<Customer> Get(boolean full) { Mapping(super.Find(full)); return allData; }

    @Override
    public boolean Post(Entity o) {
        Customer c=(Customer)o;
        c.customerId=(int)(getMaxID()+1);
        c.createDate=new Date(); c.lastUpdate=new Date();
        return super.dbPost(SerializerMap(c));
    }

    @Override public boolean Put(Entity o) { return super.dbPut(SerializerMap(o)); }

    /**
     * Delete: SOFT DELETE usando el campo active de la tabla customer.
     * Cambia active=0 en lugar de borrar fisicamente el registro.
     * Preserva la integridad referencial con rentas y pagos.
     */
    @Override
    public boolean Delete(Entity o) {
        Customer c=(Customer)o;
        return super.dbDelete(c.customerId, "active"); // active=0
    }

    @Override
    public HashMap<String,String> SerializerMap(Entity o) {
        Customer c=(Customer)o;
        HashMap<String,String> m=new HashMap<>();
        m.put("customer_id",String.valueOf(c.customerId));
        m.put("store_id",   String.valueOf(c.storeId));
        m.put("first_name", c.firstName!=null?c.firstName:"");
        m.put("last_name",  c.lastName!=null?c.lastName:"");
        m.put("email",      c.email!=null?c.email:"");
        m.put("address_id", String.valueOf(c.addressId>0?c.addressId:1));
        m.put("active",     c.active?"1":"0");
        m.put("create_date",Entity.getCurrentDate().toString());
        m.put("last_update",Entity.getCurrentDate().toString());
        return m;
    }

    @Override
    public String Serializer() {
        if(allData==null||allData.isEmpty()) return "[]";
        StringBuilder sb=new StringBuilder("["); char sep=' ';
        for(Customer c:allData){
            sb.append(sep).append("{customer_id:").append(c.customerId)
              .append(",name:\"").append(c.firstName).append(" ").append(c.lastName)
              .append("\",email:\"").append(c.email)
              .append("\",active:").append(c.active).append("}");
            sep=',';
        }
        return sb.append("]").toString();
    }

    @Override
    public String SerializerCSV() {
        if(allData==null||allData.isEmpty()) return "customer_id,first_name,last_name,email,active\n(sin datos)";
        StringBuilder sb=new StringBuilder("customer_id,first_name,last_name,email,store_id,active\n");
        for(Customer c:allData)
            sb.append(c.customerId).append(",").append(c.firstName).append(",")
              .append(c.lastName).append(",").append(c.email).append(",")
              .append(c.storeId).append(",").append(c.active?1:0).append("\n");
        return sb.toString();
    }

    @Override
    public Entity inMemSearch(Object pid) {
        if(allData==null) return null;
        int id=Integer.parseInt(pid.toString());
        List<Customer> f=allData.stream().filter(c->c.customerId==id).collect(Collectors.toList());
        return f.isEmpty()?null:f.get(0);
    }
}
