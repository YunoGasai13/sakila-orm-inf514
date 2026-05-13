package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Entidad Country - mapea exactamente la tabla `country` de sakila.
 * Es la entidad mas basica; no tiene FK externas.
 * Otras entidades (City) la usan por composicion/agregacion.
 *
 * Tabla: country(country_id, country, last_update)
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class Country extends Entity {

    /** PK autoincrement */
    public int countryId;
    /** Nombre del pais (ej: "Dominican Republic", "USA") */
    public String country;
    /** Fecha ultima actualizacion */
    public Date lastUpdate;

    /** Constructor vacio requerido para instanciacion desde ResultSet */
    public Country() {}

    /**
     * Constructor completo.
     * @param countryId  ID del pais
     * @param country    nombre del pais
     * @param lastUpdate ultima actualizacion
     */
    public Country(int countryId, String country, Date lastUpdate) {
        this.countryId  = countryId;
        this.country    = country;
        this.lastUpdate = lastUpdate;
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────
    /** @return ID del pais */
    public int getCountryId()               { return countryId; }
    /** @param id nuevo ID */
    public void setCountryId(int id)        { this.countryId = id; }
    /** @return nombre del pais */
    public String getCountry()              { return country; }
    /** @param c nuevo nombre */
    public void setCountry(String c)        { this.country = c; }
    /** @return ultima actualizacion */
    public Date getLastUpdate()             { return lastUpdate; }
    /** @param d nueva fecha */
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        return String.format("[Country] ID:%-3d | %s", countryId, country);
    }
}
