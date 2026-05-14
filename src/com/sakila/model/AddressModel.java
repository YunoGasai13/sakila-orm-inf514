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
public final class AddressModel extends DataContext implements iDatapost {

    private ArrayList<Address> allData;

    /** tabla=address | PK=address_id | busqueda por direccion | FK=city_id. */
    public AddressModel() {
        super("address","address_id","address","city_id","address","last_update");
    }

    /** ResultSet -> ArrayList<Address>. City se inyecta solo con su ID. */
    @Override
    public void Mapping(ResultSet rSet) {
        // Convierto cada fila a Address; la City va liviana (solo el ID, sin precargar)
        allData = new ArrayList<>();
        if (rSet == null) return;
        try {
            while (rSet.next()) {
                City c = new City();
                c.cityId = rSet.getInt("city_id");
                allData.add(new Address(
                    rSet.getInt("address_id"),
                    rSet.getString("address"),
                    rSet.getString("address2"),
                    rSet.getString("district"),
                    c,
                    rSet.getString("postal_code"),
                    rSet.getString("phone"),
                    rSet.getDate("last_update")));
            }
            rSet.close();
        } catch (SQLException e) { actionMessage = "Mapping Address: " + e.getMessage(); }
    }

    @Override public ArrayList<Address> Get()                  { Mapping(super.Find());       return allData; }
    @Override public ArrayList<Address> Get(Object id)         { Mapping(super.Find(id));     return allData; }
    @Override public ArrayList<Address> Get(String s)          { Mapping(super.Find(s));      return allData; }
    @Override public ArrayList<Address> Get(String s, Object f){ Mapping(super.Find(s,f));    return allData; }
    @Override public ArrayList<Address> Get(Date d1, Date d2)  { Mapping(super.Find(d1,d2));  return allData; }
    @Override public ArrayList<Address> getData()               { return allData; }

    /** Modo full: hasta 5000 (para resolver FK desde Customer/Staff/Store). */
    public ArrayList<Address> Get(boolean full) { Mapping(super.Find(full)); return allData; }

    @Override
    public boolean Post(Entity o) {
        Address a = (Address)o;
        a.addressId = (int)(getMaxID()+1);
        a.lastUpdate = new Date();
        return super.dbPost(SerializerMap(a));
    }

    @Override public boolean Put(Entity o)    { return super.dbPut(SerializerMap(o)); }

    /** Address no tiene campo active; no se borra para preservar integridad. */
    @Override public boolean Delete(Entity o) { return false; }

    @Override
    public HashMap<String,String> SerializerMap(Entity o) {
        Address a = (Address)o;
        int cityId = (a.objCity != null) ? a.objCity.cityId : 1;
        HashMap<String,String> m = new HashMap<>();
        m.put("address_id", String.valueOf(a.addressId));
        m.put("address",    a.address != null ? a.address : "");
        m.put("address2",   a.address2 != null ? a.address2 : "");
        m.put("district",   a.district != null ? a.district : "");
        m.put("city_id",    String.valueOf(cityId));
        m.put("postal_code",a.postalCode != null ? a.postalCode : "");
        m.put("phone",      a.phone != null ? a.phone : "");
        m.put("last_update",Entity.getCurrentDate().toString());
        return m;
    }

    @Override
    public String Serializer() {
        if(allData==null||allData.isEmpty()) return "[]";
        StringBuilder sb=new StringBuilder("["); char sep=' ';
        for(Address a:allData){
            sb.append(sep).append("{address_id:").append(a.addressId)
              .append(",address:\"").append(a.address)
              .append("\",phone:\"").append(a.phone).append("\"}");
            sep=',';
        }
        return sb.append("]").toString();
    }

    @Override
    public String SerializerCSV() {
        if(allData==null||allData.isEmpty()) return "address_id,address,district,phone\n(sin datos)";
        StringBuilder sb=new StringBuilder("address_id,address,district,postal_code,phone\n");
        for(Address a:allData)
            sb.append(a.addressId).append(",\"").append(a.address).append("\",")
              .append(a.district).append(",").append(a.postalCode).append(",").append(a.phone).append("\n");
        return sb.toString();
    }

    @Override
    public Entity inMemSearch(Object pid) {
        if(allData==null) return null;
        int id=Integer.parseInt(pid.toString());
        List<Address> f=allData.stream().filter(a->a.addressId==id).collect(Collectors.toList());
        return f.isEmpty()?null:f.get(0);
    }
}
