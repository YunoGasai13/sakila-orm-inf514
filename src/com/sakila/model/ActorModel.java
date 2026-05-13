package com.sakila.model;

import com.sakila.data.Actor;
import com.sakila.data.Entity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Modelo ActorModel - hijo CONCRETO y FINAL de DataContext.
 * FINAL: no puede ser extendido por otras clases.
 * Implementa iDatapost: cumple el contrato CRUD.
 *
 * Gestiona el ArrayList de objetos Actor con:
 *   Post()   = INSERT nuevo actor
 *   Get()    = SELECT (varios overloads)
 *   Put()    = UPDATE actor existente
 *   Delete() = Soft delete (last_update, no campo active en actor)
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class ActorModel extends DataContext implements iDatapost {

    /** ArrayList con los actores cargados en memoria */
    private ArrayList<Actor> allData;

    /**
     * Constructor: configura DataContext para la tabla actor.
     * Parametros:
     *   tabla:    actor
     *   PK:       actor_id
     *   busqueda: CONCAT(first_name,' ',last_name)
     *   FK:       (ninguno)
     *   orden:    last_name, first_name
     *   fecha:    last_update
     */
    public ActorModel() {
        super("actor", "actor_id",
              "CONCAT(first_name,' ',last_name)",
              "", "last_name, first_name", "last_update");
    }

    /**
     * Mapea el ResultSet a objetos Actor y los guarda en allData.
     * Lee columna por columna segun la estructura de la tabla actor.
     *
     * @param rSet ResultSet proveniente de los metodos Find() del padre
     */
    @Override
    public void Mapping(ResultSet rSet) {
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

    // ─── GET overloads ────────────────────────────────────────────────────────

    /** @return primeros 10 actores ordenados por apellido */
    @Override public ArrayList<Actor> Get()                      { Mapping(super.Find());        return allData; }
    /** @param id actor_id exacto */
    @Override public ArrayList<Actor> Get(Object id)             { Mapping(super.Find(id));      return allData; }
    /** @param search texto parcial en nombre o apellido */
    @Override public ArrayList<Actor> Get(String search)         { Mapping(super.Find(search));  return allData; }
    /** Actor no tiene FK principal, retorna null */
    @Override public ArrayList<Actor> Get(String s, Object fk)   { return null; }
    /** @param d1 fecha inicio | @param d2 fecha fin */
    @Override public ArrayList<Actor> Get(Date d1, Date d2)      { Mapping(super.Find(d1,d2));   return allData; }
    /** @return lista en memoria sin consultar DB */
    @Override public ArrayList<Actor> getData()                   { return allData; }

    // ─── POST (Create) ────────────────────────────────────────────────────────

    /**
     * Post: inserta un nuevo actor en la DB.
     * Calcula el siguiente actor_id con getMaxID() + 1.
     *
     * @param odata Actor con firstName y lastName (actorId se genera aqui)
     * @return true si el INSERT fue exitoso
     */
    @Override
    public boolean Post(Entity odata) {
        Actor a = (Actor) odata;
        a.actorId    = (int)(super.getMaxID() + 1);
        a.lastUpdate = new Date();
        return super.dbPost(SerializerMap(a));
    }

    // ─── PUT (Update) ─────────────────────────────────────────────────────────

    /**
     * Put: actualiza un actor existente en la DB.
     * @param odata Actor con actorId + datos actualizados
     * @return true si el UPDATE fue exitoso
     */
    @Override
    public boolean Put(Entity odata) {
        return super.dbPut(SerializerMap(odata));
    }

    // ─── DELETE (Soft delete) ─────────────────────────────────────────────────

    /**
     * Delete: la tabla actor no tiene campo active.
     * En este caso actualizamos last_update para registrar la operacion.
     * En produccion real se agregaria un campo active a la tabla.
     *
     * @param odata Actor con actorId del registro a desactivar
     * @return true si la operacion fue exitosa
     */
    @Override
    public boolean Delete(Entity odata) {
        Actor a = (Actor) odata;
        // Actor no tiene campo active; marcamos via update de last_update
        // En un sistema real se agregaria: ALTER TABLE actor ADD active TINYINT DEFAULT 1
        HashMap<String, String> map = SerializerMap(a);
        map.put("last_update", "1900-01-01"); // fecha antigua = inactivo logico
        return super.dbPut(map);
    }

    // ─── Serializadores ───────────────────────────────────────────────────────

    /**
     * Convierte un Actor a HashMap columna->valor para operaciones CUD.
     * @param odata Actor a convertir (cast seguro)
     * @return HashMap con los pares columna_db -> valor_string
     */
    @Override
    public HashMap<String, String> SerializerMap(Entity odata) {
        if (!(odata instanceof Actor)) return null;
        Actor a = (Actor) odata;
        HashMap<String, String> map = new HashMap<>();
        map.put("actor_id",   String.valueOf(a.actorId));
        map.put("first_name", a.firstName != null ? a.firstName : "");
        map.put("last_name",  a.lastName  != null ? a.lastName  : "");
        map.put("last_update",Entity.getCurrentDate().toString());
        return map;
    }

    /**
     * Serializa la lista actual a JSON.
     * Formato: [{actor_id:1, first_name:"PENELOPE", last_name:"GUINESS"},...}]
     *
     * @return String con el JSON de la lista de actores
     */
    @Override
    public String Serializer() {
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

    /**
     * Serializa la lista actual a CSV.
     * Primera linea = encabezados de columna.
     *
     * @return String con el CSV de la lista de actores
     */
    @Override
    public String SerializerCSV() {
        if (allData == null || allData.isEmpty()) return "actor_id,first_name,last_name\n(sin datos)";
        StringBuilder sb = new StringBuilder("actor_id,first_name,last_name,last_update\n");
        for (Actor a : allData)
            sb.append(a.actorId).append(",").append(a.firstName).append(",")
              .append(a.lastName).append(",").append(a.lastUpdate).append("\n");
        return sb.toString();
    }

    /**
     * Busca un actor en el ArrayList actual (en memoria, sin DB).
     * Usa Java Streams y lambda para filtrar por actor_id.
     *
     * @param pid actor_id a buscar
     * @return Actor encontrado o null si no esta en la lista actual
     */
    @Override
    public Entity inMemSearch(Object pid) {
        if (allData == null) return null;
        int id = Integer.parseInt(pid.toString());
        List<Actor> found = allData.stream()
                .filter(a -> a.actorId == id)
                .collect(Collectors.toList());
        return found.isEmpty() ? null : found.get(0);
    }
}
