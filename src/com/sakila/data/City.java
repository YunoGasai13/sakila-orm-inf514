package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * FK country_id manejada por agregacion de objeto Country.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class City extends Entity {

    public int cityId;
    public String city;
    /** FK country_id en forma de objeto. Acceso: city.objCountry.country */
    public Country objCountry;
    public Date lastUpdate;

    public City() {
        this.objCountry = new Country();
    }

    public City(int cityId, String city, Country objCountry, Date lastUpdate) {
        this.cityId      = cityId;
        this.city        = city;
        this.objCountry  = objCountry;
        this.lastUpdate  = lastUpdate;
    }

    public int getCityId()                  { return cityId; }
    public void setCityId(int id)           { this.cityId = id; }
    public String getCity()                 { return city; }
    public void setCity(String c)           { this.city = c; }
    public Country getObjCountry()          { return objCountry; }
    public void setObjCountry(Country obj)  { this.objCountry = obj; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        String countryName = (objCountry != null) ? objCountry.country : "N/A";
        return String.format("[City] ID:%-3d | %-20s | Pais: %s", cityId, city, countryName);
    }
}
