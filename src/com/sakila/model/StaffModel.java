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
public final class StaffModel extends DataContext implements iDatapost {

    private ArrayList<Staff> allData;
    /** Auxiliar para resolver FK address_id por agregacion. */
    private AddressModel addressModel;

    /** tabla=staff | PK=staff_id | busqueda por nombre | FK=store_id. */
    public StaffModel() {
        super("staff","staff_id",
              "CONCAT(first_name,' ',last_name)",
              "store_id","last_name, first_name","last_update");
        addressModel = new AddressModel();
    }

    @Override
    public void Mapping(ResultSet rSet) {
        // Convierto cada fila en Staff y le adjunto su Address por agregacion
        allData = new ArrayList<>();
        if (rSet == null) return;
        // Precarga direcciones una sola vez
        if (addressModel.getData()==null || addressModel.getData().isEmpty())
            addressModel.Get(true);
        try {
            while (rSet.next()) {
                int addrId = rSet.getInt("address_id");
                Address objAddr = (Address) addressModel.inMemSearch(addrId);
                if (objAddr == null) { objAddr = new Address(); objAddr.addressId = addrId; }
                allData.add(new Staff(
                    rSet.getInt("staff_id"),
                    rSet.getString("first_name"),
                    rSet.getString("last_name"),
                    objAddr,
                    rSet.getString("email"),
                    rSet.getInt("store_id"),
                    rSet.getBoolean("active"),
                    rSet.getString("username"),
                    rSet.getDate("last_update")));
            }
            rSet.close();
        } catch (SQLException e) { actionMessage = "Mapping Staff: " + e.getMessage(); }
    }

    @Override public ArrayList<Staff> Get()                  { Mapping(super.Find());       return allData; }
    @Override public ArrayList<Staff> Get(Object id)         { Mapping(super.Find(id));     return allData; }
    @Override public ArrayList<Staff> Get(String s)          { Mapping(super.Find(s));      return allData; }
    @Override public ArrayList<Staff> Get(String s, Object f){ Mapping(super.Find(s,f));    return allData; }
    @Override public ArrayList<Staff> Get(Date d1, Date d2)  { Mapping(super.Find(d1,d2));  return allData; }
    @Override public ArrayList<Staff> getData()               { return allData; }

    /** Modo full: precarga para resolver FK desde Rental/Payment. */
    public ArrayList<Staff> Get(boolean full) { Mapping(super.Find(full)); return allData; }

    @Override
    public boolean Post(Entity o) {
        Staff s = (Staff)o;
        s.staffId = (int)(getMaxID()+1);
        s.lastUpdate = new Date();
        return super.dbPost(SerializerMap(s));
    }

    @Override public boolean Put(Entity o) { return super.dbPut(SerializerMap(o)); }

    /** Soft delete via campo active. */
    @Override
    public boolean Delete(Entity o) {
        Staff s = (Staff)o;
        return super.dbDelete(s.staffId, "active");
    }

    @Override
    public HashMap<String,String> SerializerMap(Entity o) {
        Staff s = (Staff)o;
        int addrId = (s.objAddress != null) ? s.objAddress.addressId : 1;
        HashMap<String,String> m = new HashMap<>();
        m.put("staff_id",   String.valueOf(s.staffId));
        m.put("first_name", s.firstName != null ? s.firstName : "");
        m.put("last_name",  s.lastName != null ? s.lastName : "");
        m.put("address_id", String.valueOf(addrId));
        m.put("email",      s.email != null ? s.email : "");
        m.put("store_id",   String.valueOf(s.storeId > 0 ? s.storeId : 1));
        m.put("active",     s.active ? "1" : "0");
        m.put("username",   s.username != null ? s.username : "");
        m.put("last_update",Entity.getCurrentDate().toString());
        return m;
    }

    @Override
    public String Serializer() {
        if(allData==null||allData.isEmpty()) return "[]";
        StringBuilder sb=new StringBuilder("["); char sep=' ';
        for(Staff s:allData){
            sb.append(sep).append("{staff_id:").append(s.staffId)
              .append(",name:\"").append(s.firstName).append(" ").append(s.lastName)
              .append("\",store:").append(s.storeId)
              .append(",active:").append(s.active).append("}");
            sep=',';
        }
        return sb.append("]").toString();
    }

    @Override
    public String SerializerCSV() {
        if(allData==null||allData.isEmpty()) return "staff_id,first_name,last_name,store_id,active\n(sin datos)";
        StringBuilder sb=new StringBuilder("staff_id,first_name,last_name,email,store_id,active\n");
        for(Staff s:allData)
            sb.append(s.staffId).append(",").append(s.firstName).append(",")
              .append(s.lastName).append(",").append(s.email).append(",")
              .append(s.storeId).append(",").append(s.active?1:0).append("\n");
        return sb.toString();
    }

    @Override
    public Entity inMemSearch(Object pid) {
        if(allData==null) return null;
        int id=Integer.parseInt(pid.toString());
        List<Staff> f=allData.stream().filter(s->s.staffId==id).collect(Collectors.toList());
        return f.isEmpty()?null:f.get(0);
    }

    @Override public void close() { super.close(); addressModel.close(); }
}
