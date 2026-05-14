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
public final class InventoryModel extends DataContext implements iDatapost {

    private ArrayList<Inventory> allData;
    /** Auxiliares para resolver FK film_id y store_id por agregacion. */
    private FilmModel  filmModel;
    private StoreModel storeModel;

    /** tabla=inventory | PK=inventory_id | FK=store_id. */
    public InventoryModel() {
        super("inventory","inventory_id","inventory_id","store_id","inventory_id","last_update");
        filmModel  = new FilmModel();
        storeModel = new StoreModel();
    }

    /** ResultSet -> ArrayList<Inventory> con Film y Store inyectados. */
    @Override
    public void Mapping(ResultSet rSet) {
        // Cada copia fisica de pelicula trae su Film y Store completos por agregacion
        allData = new ArrayList<>();
        if (rSet == null) return;
        // Precarga films y tiendas para resolver las FKs sin queries extra
        if (filmModel.getData()==null || filmModel.getData().isEmpty())
            filmModel.Get(true);
        if (storeModel.getData()==null || storeModel.getData().isEmpty())
            storeModel.Get(true);
        try {
            while (rSet.next()) {
                int fid = rSet.getInt("film_id");
                int sid = rSet.getInt("store_id");
                Film  objFilm  = (Film)  filmModel.inMemSearch(fid);
                Store objStore = (Store) storeModel.inMemSearch(sid);
                if (objFilm == null)  { objFilm = new Film(); objFilm.filmId = fid; objFilm.title = "Film#"+fid; }
                if (objStore == null) { objStore = new Store(); objStore.storeId = sid; }
                allData.add(new Inventory(
                    rSet.getInt("inventory_id"), objFilm,
                    objStore, rSet.getDate("last_update")));
            }
            rSet.close();
        } catch (SQLException e) { actionMessage = "Mapping Inventory: " + e.getMessage(); }
    }

    @Override public ArrayList<Inventory> Get()                  { Mapping(super.Find());       return allData; }
    @Override public ArrayList<Inventory> Get(Object id)         { Mapping(super.Find(id));     return allData; }
    @Override public ArrayList<Inventory> Get(String s)          { Mapping(super.Find(s));      return allData; }
    @Override public ArrayList<Inventory> Get(String s, Object f){ Mapping(super.Find(s,f));    return allData; }
    @Override public ArrayList<Inventory> Get(Date d1, Date d2)  { Mapping(super.Find(d1,d2));  return allData; }
    @Override public ArrayList<Inventory> getData()               { return allData; }

    @Override
    public boolean Post(Entity o) {
        Inventory i=(Inventory)o;
        i.inventoryId=(int)(getMaxID()+1); i.lastUpdate=new Date();
        return super.dbPost(SerializerMap(i));
    }
    @Override public boolean Put(Entity o)    { return super.dbPut(SerializerMap(o)); }

    /** Inventory no tiene campo active; no se borra para mantener integridad con rental. */
    @Override public boolean Delete(Entity o) { return false; }

    @Override
    public HashMap<String,String> SerializerMap(Entity o) {
        Inventory i=(Inventory)o;
        int fid = (i.objFilm  != null) ? i.objFilm.filmId   : 1;
        int sid = (i.objStore != null) ? i.objStore.storeId : 1;
        HashMap<String,String> m=new HashMap<>();
        m.put("inventory_id",String.valueOf(i.inventoryId));
        m.put("film_id",     String.valueOf(fid));
        m.put("store_id",    String.valueOf(sid));
        m.put("last_update", Entity.getCurrentDate().toString());
        return m;
    }

    @Override
    public String Serializer() {
        if(allData==null||allData.isEmpty()) return "[]";
        StringBuilder sb=new StringBuilder("["); char sep=' ';
        for(Inventory i:allData){
            String ft=(i.objFilm!=null)?i.objFilm.title:"N/A";
            int sid = (i.objStore != null) ? i.objStore.storeId : 0;
            sb.append(sep).append("{inventory_id:").append(i.inventoryId)
              .append(",film:\"").append(ft).append("\",store_id:").append(sid).append("}");
            sep=',';
        }
        return sb.append("]").toString();
    }

    @Override
    public String SerializerCSV() {
        if(allData==null||allData.isEmpty()) return "inventory_id,film_id,film_title,store_id\n(sin datos)";
        StringBuilder sb=new StringBuilder("inventory_id,film_id,film_title,store_id\n");
        for(Inventory i:allData){
            int fid=(i.objFilm!=null)?i.objFilm.filmId:0;
            String ft=(i.objFilm!=null)?i.objFilm.title:"N/A";
            int sid = (i.objStore != null) ? i.objStore.storeId : 0;
            sb.append(i.inventoryId).append(",").append(fid).append(",\"").append(ft).append("\",").append(sid).append("\n");
        }
        return sb.toString();
    }

    @Override
    public Entity inMemSearch(Object pid) {
        if(allData==null) return null;
        int id=Integer.parseInt(pid.toString());
        List<Inventory> f=allData.stream().filter(i->i.inventoryId==id).collect(Collectors.toList());
        return f.isEmpty()?null:f.get(0);
    }

    @Override public void close() { super.close(); filmModel.close(); storeModel.close(); }
}
