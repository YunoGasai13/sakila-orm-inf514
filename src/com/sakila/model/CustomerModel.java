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
 * Delete() = soft delete via campo active.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class CustomerModel extends DataContext implements iDatapost {

    private ArrayList<Customer> allData;
    /** Auxiliares para resolver FK store_id y address_id por agregacion. */
    private StoreModel   storeModel;
    private AddressModel addressModel;

    /** tabla=customer | PK=customer_id | busqueda por nombre/email | FK=store_id. */
    public CustomerModel() {
        super("customer", "customer_id",
              "CONCAT(first_name,' ',last_name,' ',email)",
              "store_id", "last_name, first_name", "create_date");
        storeModel   = new StoreModel();
        addressModel = new AddressModel();
    }

    @Override
    public void Mapping(ResultSet rSet) {
        // Convierto cada fila en Customer y resuelvo sus FKs (Store, Address) por agregacion
        allData = new ArrayList<>();
        if (rSet == null) return;
        // Precarga de auxiliares: una sola query por tabla en vez de una por cliente
        if (storeModel.getData()==null || storeModel.getData().isEmpty())
            storeModel.Get(true);
        if (addressModel.getData()==null || addressModel.getData().isEmpty())
            addressModel.Get(true);
        try {
            while (rSet.next()) {
                // Resuelvo las FK en memoria con los modelos auxiliares ya cargados
                int sid = rSet.getInt("store_id");
                int aid = rSet.getInt("address_id");
                Store   objStore   = (Store)   storeModel.inMemSearch(sid);
                Address objAddress = (Address) addressModel.inMemSearch(aid);
                if (objStore == null)   { objStore = new Store(); objStore.storeId = sid; }
                if (objAddress == null) { objAddress = new Address(); objAddress.addressId = aid; }
                allData.add(new Customer(
                    rSet.getInt("customer_id"), objStore,
                    rSet.getString("first_name"), rSet.getString("last_name"),
                    rSet.getString("email"), objAddress,
                    rSet.getBoolean("active"),
                    rSet.getDate("create_date"), rSet.getDate("last_update")));
            }
            rSet.close();
        } catch (SQLException e) { actionMessage = "Mapping Customer: " + e.getMessage(); }
    }

    @Override public ArrayList<Customer> Get()                  { Mapping(super.Find());       return allData; }
    @Override public ArrayList<Customer> Get(Object id)         { Mapping(super.Find(id));     return allData; }
    @Override public ArrayList<Customer> Get(String s)          { Mapping(super.Find(s));      return allData; }
    @Override public ArrayList<Customer> Get(String s, Object f){ Mapping(super.Find(s,f));    return allData; }
    @Override public ArrayList<Customer> Get(Date d1, Date d2)  { Mapping(super.Find(d1,d2));  return allData; }
    @Override public ArrayList<Customer> getData()               { return allData; }

    /** Modo full: hasta 5000 (usado por Rental/PaymentModel para resolver FK). */
    public ArrayList<Customer> Get(boolean full) { Mapping(super.Find(full)); return allData; }

    @Override
    public boolean Post(Entity o) {
        // Nuevo cliente: calculo su ID (MAX+1) y seteo fechas de alta
        Customer c=(Customer)o;
        c.customerId=(int)(getMaxID()+1);
        c.createDate=new Date(); c.lastUpdate=new Date();
        return super.dbPost(SerializerMap(c));
    }

    @Override public boolean Put(Entity o) { return super.dbPut(SerializerMap(o)); }

    /** Soft delete: pone active=0 (preserva rentas y pagos asociados). */
    @Override
    public boolean Delete(Entity o) {
        // Aprovecho el campo active que ya trae sakila.customer
        Customer c=(Customer)o;
        return super.dbDelete(c.customerId, "active");
    }

    @Override
    public HashMap<String,String> SerializerMap(Entity o) {
        // Extraigo los IDs desde los objetos agregados y armo el map columna->valor
        Customer c=(Customer)o;
        int sid = (c.objStore   != null) ? c.objStore.storeId     : 1;
        int aid = (c.objAddress != null) ? c.objAddress.addressId : 1;
        HashMap<String,String> m=new HashMap<>();
        m.put("customer_id",String.valueOf(c.customerId));
        m.put("store_id",   String.valueOf(sid));
        m.put("first_name", c.firstName!=null?c.firstName:"");
        m.put("last_name",  c.lastName!=null?c.lastName:"");
        m.put("email",      c.email!=null?c.email:"");
        m.put("address_id", String.valueOf(aid > 0 ? aid : 1));
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
        for(Customer c:allData){
            int sid = (c.objStore != null) ? c.objStore.storeId : 0;
            sb.append(c.customerId).append(",").append(c.firstName).append(",")
              .append(c.lastName).append(",").append(c.email).append(",")
              .append(sid).append(",").append(c.active?1:0).append("\n");
        }
        return sb.toString();
    }

    @Override
    public Entity inMemSearch(Object pid) {
        if(allData==null) return null;
        int id=Integer.parseInt(pid.toString());
        List<Customer> f=allData.stream().filter(c->c.customerId==id).collect(Collectors.toList());
        return f.isEmpty()?null:f.get(0);
    }

    @Override public void close() { super.close(); storeModel.close(); addressModel.close(); }
}
