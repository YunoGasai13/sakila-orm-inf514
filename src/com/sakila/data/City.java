package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Entidad City - mapea la tabla `city` de sakila.
 *
 * PATRON REQUERIDO POR EL PROFESOR:
 * La FK country_id se gestiona mediante AGREGACION de objeto Country.
 * En lugar de: int countryId;
 * Se usa:       Country objCountry;   <--- composicion/agregacion
 *
 * Esto permite acceder directamente a: city.objCountry.country
 * sin hacer un JOIN adicional una vez que el objeto esta cargado.
 *
 * Tabla: city(city_id, city, country_id, last_update)
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class City extends Entity {

    /** PK autoincrement */
    public int cityId;
    /** Nombre de la ciudad */
    public String city;
    /**
     * FK a country gestionada por COMPOSICION/AGREGACION.
     * El objeto Country esta embebido en City.
     * Acceso: objCity.objCountry.country
     */
    public Country objCountry;
    /** Fecha ultima actualizacion */
    public Date lastUpdate;

    /** Constructor vacio */
    public City() {
        this.objCountry = new Country(); // inicializar para evitar NullPointerException
    }

    /**
     * Constructor completo con objeto Country embebido.
     * @param cityId     ID de la ciudad
     * @param city       nombre de la ciudad
     * @param objCountry objeto Country (FK por agregacion)
     * @param lastUpdate ultima actualizacion
     */
    public City(int cityId, String city, Country objCountry, Date lastUpdate) {
        this.cityId      = cityId;
        this.city        = city;
        this.objCountry  = objCountry;
        this.lastUpdate  = lastUpdate;
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────
    /** @return ID de la ciudad */
    public int getCityId()                  { return cityId; }
    /** @param id nuevo ID */
    public void setCityId(int id)           { this.cityId = id; }
    /** @return nombre de la ciudad */
    public String getCity()                 { return city; }
    /** @param c nuevo nombre */
    public void setCity(String c)           { this.city = c; }
    /** @return objeto Country asociado */
    public Country getObjCountry()          { return objCountry; }
    /** @param obj nuevo Country */
    public void setObjCountry(Country obj)  { this.objCountry = obj; }
    /** @return ultima actualizacion */
    public Date getLastUpdate()             { return lastUpdate; }
    /** @param d nueva fecha */
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        String countryName = (objCountry != null) ? objCountry.country : "N/A";
        return String.format("[City] ID:%-3d | %-20s | Pais: %s", cityId, city, countryName);
    }
}
