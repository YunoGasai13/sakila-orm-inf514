package com.sakila.model;

import com.sakila.data.Entity;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Interface iDatapost - contrato CRUD estandar del sistema ORM.
 * NOMBRE REQUERIDO POR EL PROFESOR: iDatapost (no iORMObject).
 *
 * Todos los modelos (ActorModel, FilmModel, etc.) implementan esta interface.
 * Garantiza que cada modelo tenga exactamente las mismas operaciones basicas.
 *
 * CRUD = Create (Post), Read (Get), Update (Put), Delete (marcar inactivo)
 *
 * Usa wildcards <?> para generalizar metodos genericos con cualquier Entity hija.
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public interface iDatapost {

    /**
     * Mapea un ResultSet de la DB a objetos de la entidad correspondiente.
     * Cada modelo implementa este metodo segun las columnas de su tabla.
     *
     * @param rSet ResultSet con la data de la consulta SQL
     */
    void Mapping(ResultSet rSet);

    /**
     * Serializa una entidad a HashMap para operaciones de escritura.
     * El HashMap contiene pares columna_db -> valor_string.
     * Usado internamente por Post() y Put().
     *
     * @param odata entidad a serializar (cualquier hijo de Entity usando wildcard)
     * @return HashMap con nombre_columna -> valor
     */
    HashMap<String, String> SerializerMap(Entity odata);

    /**
     * Serializa la lista actual en memoria a formato JSON.
     * Formato: [{campo:valor,...},{...}]
     *
     * @return String con el JSON de todos los registros actuales
     */
    String Serializer();

    /**
     * Exporta la lista actual en memoria a formato CSV.
     * Primera linea son los encabezados de columna.
     *
     * @return String con el CSV de todos los registros actuales
     */
    String SerializerCSV();

    // ─── GET (Read) ───────────────────────────────────────────────────────────

    /**
     * Get base: obtiene los primeros 10 registros por defecto.
     * Wildcard <?> permite retornar cualquier tipo de Entity.
     *
     * @return ArrayList con las entidades encontradas
     */
    ArrayList<? extends Entity> Get();

    /**
     * Get por PK: busca un registro exacto por su llave primaria.
     *
     * @param id valor del PK
     * @return ArrayList con la entidad encontrada (vacio si no existe)
     */
    ArrayList<? extends Entity> Get(Object id);

    /**
     * Get por texto: busqueda LIKE en columnas de tipo string.
     * Ejemplo: Get("PEN") en actores encuentra PENELOPE, SPENCER, etc.
     *
     * @param search texto a buscar
     * @return ArrayList con entidades que contienen el texto
     */
    ArrayList<? extends Entity> Get(String search);

    /**
     * Get por texto + FK: busqueda LIKE filtrada por llave foranea.
     * Ejemplo: Get("AL", 2) busca ciudades con "AL" en el pais 2.
     *
     * @param search texto a buscar
     * @param fklink valor del FK para filtrar
     * @return ArrayList con entidades que coinciden
     */
    ArrayList<? extends Entity> Get(String search, Object fklink);

    /**
     * Get por rango de fechas: busqueda BETWEEN en columna de fecha.
     * Ejemplo: pagos entre el 01/01/2006 y el 31/01/2006.
     *
     * @param dtein  fecha inicio del rango
     * @param dteout fecha fin del rango
     * @return ArrayList con entidades en ese rango
     */
    ArrayList<? extends Entity> Get(Date dtein, Date dteout);

    /**
     * Retorna la ultima lista cargada en memoria sin ir a la DB.
     *
     * @return ArrayList con los datos del ultimo Get() ejecutado
     */
    ArrayList<? extends Entity> getData();

    // ─── POST (Create) ────────────────────────────────────────────────────────

    /**
     * Post: agrega un nuevo registro a la base de datos (INSERT).
     * El ID se asigna automaticamente (MAX(PK) + 1).
     *
     * @param odata entidad con los datos del nuevo registro
     * @return true si el INSERT fue exitoso
     */
    boolean Post(Entity odata);

    // ─── PUT (Update) ─────────────────────────────────────────────────────────

    /**
     * Put: actualiza un registro existente en la DB (UPDATE).
     * La entidad debe tener el PK del registro a actualizar.
     *
     * @param odata entidad con los datos actualizados (incluye PK)
     * @return true si el UPDATE fue exitoso
     */
    boolean Put(Entity odata);

    // ─── DELETE (Soft Delete = marcar inactivo) ───────────────────────────────

    /**
     * Delete: marca un registro como INACTIVO (soft delete).
     * NO borra el registro fisicamente de la DB.
     * Cambia el campo `active` a 0 para preservar integridad referencial.
     * Si la tabla no tiene campo active, puede usar un UPDATE de otro campo.
     *
     * @param odata entidad a desactivar (debe tener el PK)
     * @return true si la operacion fue exitosa
     */
    boolean Delete(Entity odata);

    /**
     * Busqueda en memoria sobre el ArrayList actual (sin consultar DB).
     * Usa Java Streams para filtrar por PK.
     * Util para evitar consultas repetitivas cuando la data ya esta cargada.
     *
     * @param pid valor del PK a buscar en memoria
     * @return Entity encontrada o null si no esta en la lista actual
     */
    Entity inMemSearch(Object pid);
}
