package com.sakila.model;

import com.sakila.data.Actor;
import com.sakila.data.Entity;
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
public final class ActorModel extends DataContext implements iDatapost {

    /** Lista de actores en memoria. */
    private ArrayList<Actor> allData;

    /** tabla=actor | PK=actor_id | busqueda por nombre+apellido | orden por apellido. */
    public ActorModel() {
        super("actor", "actor_id",
              "CONCAT(first_name,' ',last_name)",
              "", "last_name, first_name", "last_update");
    }

    /** ResultSet -> ArrayList<Actor>. */
    @Override
    public void Mapping(ResultSet rSet) {
        // Recorro cada fila del ResultSet y la convierto en un objeto Actor
        allData = new ArrayList<>();
        if (rSet == null) return;
        try {
            while (rSet.next()) {
                allData.add(new Actor(
                    rSet.getInt("actor_id"),
                    rSet.getString("first_name"),
                    rSet.getString("last_name"),
                    rSet.getDate("last_update")
                ));
            }
            rSet.close();
        } catch (SQLException e) {
            actionMessage = "Mapping Actor: " + e.getMessage();
        }
    }

    // ── Get: overloads que delegan en Find() del padre y mapean a Actor.
    @Override public ArrayList<Actor> Get()                      { Mapping(super.Find());        return allData; }
    @Override public ArrayList<Actor> Get(Object id)             { Mapping(super.Find(id));      return allData; }
    @Override public ArrayList<Actor> Get(String search)         { Mapping(super.Find(search));  return allData; }
    @Override public ArrayList<Actor> Get(String s, Object fk)   { return null; }
    @Override public ArrayList<Actor> Get(Date d1, Date d2)      { Mapping(super.Find(d1,d2));   return allData; }
    @Override public ArrayList<Actor> getData()                   { return allData; }

    /** Post: asigna actor_id = MAX(actor_id) + 1 e inserta. */
    @Override
    public boolean Post(Entity odata) {
        // Calculo el siguiente ID, seteo la fecha y delego el INSERT al padre
        Actor a = (Actor) odata;
        a.actorId    = (int)(super.getMaxID() + 1);
        a.lastUpdate = new Date();
        return super.dbPost(SerializerMap(a));
    }

    @Override
    public boolean Put(Entity odata) {
        return super.dbPut(SerializerMap(odata));
    }

    /** Tabla actor no tiene campo active; se marca con fecha antigua. */
    @Override
    public boolean Delete(Entity odata) {
        // Como sakila.actor no tiene active, marco last_update muy antigua para indicar "inactivo"
        Actor a = (Actor) odata;
        HashMap<String, String> map = SerializerMap(a);
        map.put("last_update", "1900-01-01");
        return super.dbPut(map);
    }

    /** Actor -> HashMap con cada columna de la tabla. */
    @Override
    public HashMap<String, String> SerializerMap(Entity odata) {
        // Convierto el objeto en un map columna->valor para que dbPost/dbPut arme el SQL
        if (!(odata instanceof Actor)) return null;
        Actor a = (Actor) odata;
        HashMap<String, String> map = new HashMap<>();
        map.put("actor_id",   String.valueOf(a.actorId));
        map.put("first_name", a.firstName != null ? a.firstName : "");
        map.put("last_name",  a.lastName  != null ? a.lastName  : "");
        map.put("last_update",Entity.getCurrentDate().toString());
        return map;
    }

    @Override
    public String Serializer() {
        // Construyo el JSON a mano con StringBuilder (sin libreria externa)
        if (allData == null || allData.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        char sep = ' ';
        for (Actor a : allData) {
            sb.append(sep).append("{actor_id:").append(a.actorId)
              .append(",first_name:\"").append(a.firstName)
              .append("\",last_name:\"").append(a.lastName).append("\"}");
            sep = ',';
        }
        return sb.append("]").toString();
    }

    @Override
    public String SerializerCSV() {
        // Primera linea = headers, luego una fila por actor separada por comas
        if (allData == null || allData.isEmpty()) return "actor_id,first_name,last_name\n(sin datos)";
        StringBuilder sb = new StringBuilder("actor_id,first_name,last_name,last_update\n");
        for (Actor a : allData)
            sb.append(a.actorId).append(",").append(a.firstName).append(",")
              .append(a.lastName).append(",").append(a.lastUpdate).append("\n");
        return sb.toString();
    }

    /** Busqueda por actor_id sobre la lista actual (no consulta DB). */
    @Override
    public Entity inMemSearch(Object pid) {
        // Filtro el ArrayList con Streams para encontrar el actor por su ID
        if (allData == null) return null;
        int id = Integer.parseInt(pid.toString());
        List<Actor> found = allData.stream()
                .filter(a -> a.actorId == id)
                .collect(Collectors.toList());
        return found.isEmpty() ? null : found.get(0);
    }
}
