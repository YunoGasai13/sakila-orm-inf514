package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Country extends Entity {

    public int countryId;
    public String country;
    public Date lastUpdate;

    public Country() {}

    public Country(int countryId, String country, Date lastUpdate) {
        this.countryId  = countryId;
        this.country    = country;
        this.lastUpdate = lastUpdate;
    }

    public int getCountryId()               { return countryId; }
    public void setCountryId(int id)        { this.countryId = id; }
    public String getCountry()              { return country; }
    public void setCountry(String c)        { this.country = c; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        return String.format("[Country] ID:%-3d | %s", countryId, country);
    }
}
