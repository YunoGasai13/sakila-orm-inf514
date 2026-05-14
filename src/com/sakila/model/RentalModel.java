package com.sakila.model;

import com.sakila.data.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class RentalModel extends DataContext implements iDatapost {

    private ArrayList<Rental> allData;
    /** Auxiliares para resolver FK customer_id, inventory_id y staff_id por agregacion. */
    private CustomerModel  customerModel;
    private InventoryModel inventoryModel;
    private StaffModel     staffModel;

    /** tabla=rental | PK=rental_id | FK=customer_id | orden por fecha de renta DESC. */
    public RentalModel() {
        super("rental","rental_id","rental_id","customer_id","rental_date DESC","rental_date");
        customerModel  = new CustomerModel();
        inventoryModel = new InventoryModel();
        staffModel     = new StaffModel();
    }

    /** ResultSet -> ArrayList<Rental>. Inyecta Customer y Staff completos; Inventory solo con su ID. */
    @Override
    public void Mapping(ResultSet rSet) {
        // Cada renta arma sus relaciones: Customer y Staff completos, Inventory liviano
        allData = new ArrayList<>();
        if (rSet == null) return;
        // Precarga: clientes y staff de una sola vez para resolver FKs en memoria
        if (customerModel.getData() == null || customerModel.getData().isEmpty())
            customerModel.Get(true);
        if (staffModel.getData() == null || staffModel.getData().isEmpty())
            staffModel.Get();
        try {
            while (rSet.next()) {
                int custId  = rSet.getInt("customer_id");
                int invId   = rSet.getInt("inventory_id");
                int staffId = rSet.getInt("staff_id");
                Customer  objCust  = (Customer)  customerModel.inMemSearch(custId);
                Staff     objStaff = (Staff)     staffModel.inMemSearch(staffId);
                // Inventory liviano: solo el ID para evitar precargar 4500+ registros
                Inventory objInv   = new Inventory();
                objInv.inventoryId = invId;
                if (objStaff == null) { objStaff = new Staff(); objStaff.staffId = staffId; }

                allData.add(new Rental(
                    rSet.getInt("rental_id"),
                    rSet.getDate("rental_date"),
                    objInv,
                    objCust != null ? objCust : new Customer(),
                    rSet.getDate("return_date"),
                    objStaff,
                    rSet.getDate("last_update")));
            }
            rSet.close();
        } catch (SQLException e) { actionMessage = "Mapping Rental: " + e.getMessage(); }
    }

    @Override public ArrayList<Rental> Get()                  { Mapping(super.Find());       return allData; }
    @Override public ArrayList<Rental> Get(Object id)         { Mapping(super.Find(id));     return allData; }
    @Override public ArrayList<Rental> Get(String s)          { Mapping(super.Find(s));      return allData; }
    @Override public ArrayList<Rental> Get(String s, Object f){ Mapping(super.Find(s,f));    return allData; }
    @Override public ArrayList<Rental> Get(Date d1, Date d2)  { Mapping(super.Find(d1,d2));  return allData; }
    @Override public ArrayList<Rental> getData()               { return allData; }

    public ArrayList<Rental> Get(boolean full) { Mapping(super.Find(full)); return allData; }

    @Override
    public boolean Post(Entity o) {
        Rental r=(Rental)o;
        r.rentalId=(int)(getMaxID()+1);
        r.lastUpdate=new Date();
        return super.dbPost(SerializerMap(r));
    }

    @Override public boolean Put(Entity o) { return super.dbPut(SerializerMap(o)); }

    /** Marca la renta como devuelta poniendo return_date = hoy. */
    @Override
    public boolean Delete(Entity o) {
        // No borro la renta: la cierro fijando la fecha de devolucion = hoy
        Rental r=(Rental)o;
        HashMap<String,String> m=SerializerMap(r);
        m.put("return_date", Entity.getCurrentDate().toString());
        return super.dbPut(m);
    }

    @Override
    public HashMap<String,String> SerializerMap(Entity o) {
        Rental r=(Rental)o;
        int custId  = (r.objCustomer  != null) ? r.objCustomer.customerId   : 0;
        int invId   = (r.objInventory != null) ? r.objInventory.inventoryId : 0;
        int staffId = (r.objStaff     != null) ? r.objStaff.staffId         : 1;
        HashMap<String,String> m=new HashMap<>();
        m.put("rental_id",    String.valueOf(r.rentalId));
        m.put("rental_date",  Entity.getCurrentDate().toString());
        m.put("inventory_id", String.valueOf(invId));
        m.put("customer_id",  String.valueOf(custId));
        m.put("staff_id",     String.valueOf(staffId > 0 ? staffId : 1));
        m.put("last_update",  Entity.getCurrentDate().toString());
        return m;
    }

    @Override
    public String Serializer() {
        if(allData==null||allData.isEmpty()) return "[]";
        StringBuilder sb=new StringBuilder("["); char sep=' ';
        for(Rental r:allData){
            String cust=(r.objCustomer!=null)?r.objCustomer.firstName+" "+r.objCustomer.lastName:"N/A";
            sb.append(sep).append("{rental_id:").append(r.rentalId)
              .append(",customer:\"").append(cust)
              .append("\",return_date:\"").append(r.returnDate!=null?r.returnDate:"PENDIENTE").append("\"}");
            sep=',';
        }
        return sb.append("]").toString();
    }

    @Override
    public String SerializerCSV() {
        if(allData==null||allData.isEmpty()) return "rental_id,customer,rental_date,return_date\n(sin datos)";
        StringBuilder sb=new StringBuilder("rental_id,customer_id,rental_date,return_date,staff_id\n");
        for(Rental r:allData){
            int cid     = (r.objCustomer!=null)?r.objCustomer.customerId:0;
            int staffId = (r.objStaff   !=null)?r.objStaff.staffId:0;
            sb.append(r.rentalId).append(",").append(cid).append(",")
              .append(r.rentalDate).append(",").append(r.returnDate!=null?r.returnDate:"PENDIENTE").append(",").append(staffId).append("\n");
        }
        return sb.toString();
    }

    @Override
    public Entity inMemSearch(Object pid) {
        if(allData==null) return null;
        int id=Integer.parseInt(pid.toString());
        List<Rental> f=allData.stream().filter(r->r.rentalId==id).collect(Collectors.toList());
        return f.isEmpty()?null:f.get(0);
    }

    @Override public void close() { super.close(); customerModel.close(); inventoryModel.close(); staffModel.close(); }
}
