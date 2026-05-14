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
public final class LanguageModel extends DataContext implements iDatapost {

    private ArrayList<Language> allData;

    /** tabla=language | PK=language_id | busqueda por name. */
    public LanguageModel() {
        super("language", "language_id", "name", "", "name", "last_update");
    }

    @Override
    public void Mapping(ResultSet rSet) {
        allData = new ArrayList<>();
        if (rSet == null) return;
        try {
            while (rSet.next())
                allData.add(new Language(rSet.getInt("language_id"),
                        rSet.getString("name"), rSet.getDate("last_update")));
            rSet.close();
        } catch (SQLException e) { actionMessage = "Mapping Language: " + e.getMessage(); }
    }

    @Override public ArrayList<Language> Get()                  { Mapping(super.Find());       return allData; }
    @Override public ArrayList<Language> Get(Object id)         { Mapping(super.Find(id));     return allData; }
    @Override public ArrayList<Language> Get(String s)          { Mapping(super.Find(s));      return allData; }
    @Override public ArrayList<Language> Get(String s, Object f){ return null; }
    @Override public ArrayList<Language> Get(Date d1, Date d2)  { Mapping(super.Find(d1,d2));  return allData; }
    @Override public ArrayList<Language> getData()               { return allData; }

    public ArrayList<Language> Get(boolean full) { Mapping(super.Find(full)); return allData; }

    @Override public boolean Post(Entity o)  { Language l=(Language)o; l.languageId=(int)(getMaxID()+1); return super.dbPost(SerializerMap(l)); }
    @Override public boolean Put(Entity o)   { return super.dbPut(SerializerMap(o)); }
    @Override public boolean Delete(Entity o){ return false; }

    @Override
    public HashMap<String,String> SerializerMap(Entity o) {
        Language l = (Language)o;
        HashMap<String,String> m = new HashMap<>();
        m.put("language_id", String.valueOf(l.languageId));
        m.put("name",        l.name);
        m.put("last_update", Entity.getCurrentDate().toString());
        return m;
    }

    @Override
    public String Serializer() {
        if(allData==null||allData.isEmpty()) return "[]";
        StringBuilder sb=new StringBuilder("["); char sep=' ';
        for(Language l:allData){ sb.append(sep).append("{language_id:").append(l.languageId).append(",name:\"").append(l.name).append("\"}"); sep=','; }
        return sb.append("]").toString();
    }

    @Override
    public String SerializerCSV() {
        if(allData==null||allData.isEmpty()) return "language_id,name\n(sin datos)";
        StringBuilder sb=new StringBuilder("language_id,name\n");
        for(Language l:allData) sb.append(l.languageId).append(",").append(l.name).append("\n");
        return sb.toString();
    }

    @Override
    public Entity inMemSearch(Object pid) {
        if(allData==null) return null;
        int id=Integer.parseInt(pid.toString());
        List<Language> f=allData.stream().filter(l->l.languageId==id).collect(Collectors.toList());
        return f.isEmpty()?null:f.get(0);
    }
}
