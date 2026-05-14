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
public final class StoreModel extends DataContext implements iDatapost {

    private ArrayList<Store> allData;
    /** Auxiliar para resolver FK address_id por agregacion. */
    private AddressModel addressModel;

    /** tabla=store | PK=store_id | busqueda por store_id. */
    public StoreModel() {
        super("store","store_id","store_id","manager_staff_id","store_id","last_update");
        addressModel = new AddressModel();
    }

    @Override
    public void Mapping(ResultSet rSet) {
        // Convierto cada tienda y le adjunto su Address por agregacion
        allData = new ArrayList<>();
        if (rSet == null) return;
        // Precarga direcciones para resolver la FK en memoria
        if (addressModel.getData()==null || addressModel.getData().isEmpty())
            addressModel.Get(true);
        try {
            while (rSet.next()) {
                int addrId = rSet.getInt("address_id");
                Address objAddr = (Address) addressModel.inMemSearch(addrId);
                if (objAddr == null) { objAddr = new Address(); objAddr.addressId = addrId; }
                allData.add(new Store(
                    rSet.getInt("store_id"),
                    rSet.getInt("manager_staff_id"),
                    objAddr,
                    rSet.getDate("last_update")));
            }
            rSet.close();
        } catch (SQLException e) { actionMessage = "Mapping Store: " + e.getMessage(); }
    }

    @Override public ArrayList<Store> Get()                  { Mapping(super.Find());       return allData; }
    @Override public ArrayList<Store> Get(Object id)         { Mapping(super.Find(id));     return allData; }
    @Override public ArrayList<Store> Get(String s)          { Mapping(super.Find(s));      return allData; }
    @Override public ArrayList<Store> Get(String s, Object f){ Mapping(super.Find(s,f));    return allData; }
    @Override public ArrayList<Store> Get(Date d1, Date d2)  { Mapping(super.Find(d1,d2));  return allData; }
    @Override public ArrayList<Store> getData()               { return allData; }

    /** Modo full: precarga para resolver FK desde Customer/Inventory. */
    public ArrayList<Store> Get(boolean full) { Mapping(super.Find(full)); return allData; }

    @Override
    public boolean Post(Entity o) {
        Store s = (Store)o;
        s.storeId = (int)(getMaxID()+1);
        s.lastUpdate = new Date();
        return super.dbPost(SerializerMap(s));
    }

    @Override public boolean Put(Entity o)    { return super.dbPut(SerializerMap(o)); }

    /** Store no tiene campo active; no se borra para preservar integridad. */
    @Override public boolean Delete(Entity o) { return false; }

    @Override
    public HashMap<String,String> SerializerMap(Entity o) {
        Store s = (Store)o;
        int addrId = (s.objAddress != null) ? s.objAddress.addressId : 1;
        HashMap<String,String> m = new HashMap<>();
        m.put("store_id",         String.valueOf(s.storeId));
        m.put("manager_staff_id", String.valueOf(s.managerStaffId > 0 ? s.managerStaffId : 1));
        m.put("address_id",       String.valueOf(addrId));
        m.put("last_update",      Entity.getCurrentDate().toString());
        return m;
    }

    @Override
    public String Serializer() {
        if(allData==null||allData.isEmpty()) return "[]";
        StringBuilder sb=new StringBuilder("["); char sep=' ';
        for(Store s:allData){
            String addr = (s.objAddress!=null) ? s.objAddress.address : "N/A";
            sb.append(sep).append("{store_id:").append(s.storeId)
              .append(",manager:").append(s.managerStaffId)
              .append(",address:\"").append(addr).append("\"}");
            sep=',';
        }
        return sb.append("]").toString();
    }

    @Override
    public String SerializerCSV() {
        if(allData==null||allData.isEmpty()) return "store_id,manager_staff_id,address_id\n(sin datos)";
        StringBuilder sb=new StringBuilder("store_id,manager_staff_id,address_id,address\n");
        for(Store s:allData){
            int aid = (s.objAddress!=null) ? s.objAddress.addressId : 0;
            String addr = (s.objAddress!=null) ? s.objAddress.address : "";
            sb.append(s.storeId).append(",").append(s.managerStaffId).append(",")
              .append(aid).append(",\"").append(addr).append("\"\n");
        }
        return sb.toString();
    }

    @Override
    public Entity inMemSearch(Object pid) {
        if(allData==null) return null;
        int id=Integer.parseInt(pid.toString());
        List<Store> f=allData.stream().filter(s->s.storeId==id).collect(Collectors.toList());
        return f.isEmpty()?null:f.get(0);
    }

    @Override public void close() { super.close(); addressModel.close(); }
}
