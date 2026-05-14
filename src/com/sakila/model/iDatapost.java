package com.sakila.model;

import com.sakila.data.Entity;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * Interface CRUD estandar implementada por todos los modelos.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public interface iDatapost {

    /** Convierte un ResultSet a ArrayList de entidades. */
    void Mapping(ResultSet rSet);

    /** Convierte una entidad a HashMap col->valor para INSERT/UPDATE. */
    HashMap<String, String> SerializerMap(Entity odata);

    /** Lista actual en memoria en formato JSON. */
    String Serializer();

    /** Lista actual en memoria en formato CSV. */
    String SerializerCSV();

    // ── Get: overloads de busqueda. Wildcard <?> generaliza el retorno.
    ArrayList<? extends Entity> Get();
    ArrayList<? extends Entity> Get(Object id);
    ArrayList<? extends Entity> Get(String search);
    ArrayList<? extends Entity> Get(String search, Object fklink);
    ArrayList<? extends Entity> Get(Date dtein, Date dteout);

    /** Ultima lista cargada sin volver a consultar la DB. */
    ArrayList<? extends Entity> getData();

    /** INSERT. */
    boolean Post(Entity odata);

    /** UPDATE. */
    boolean Put(Entity odata);

    /** Soft delete: marca el registro como inactivo (no DELETE fisico). */
    boolean Delete(Entity odata);

    /** Busqueda por PK sobre el ArrayList actual usando Streams. */
    Entity inMemSearch(Object pid);
}
