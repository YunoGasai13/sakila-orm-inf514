package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * FK city_id manejada por agregacion de objeto City.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Address extends Entity {

    public int addressId;
    public String address;
    public String address2;
    public String district;
    /** FK city_id en forma de objeto. Acceso: addr.objCity.city */
    public City objCity;
    public String postalCode;
    public String phone;
    public Date lastUpdate;

    public Address() {
        this.objCity = new City();
    }

    public Address(int addressId, String address, String address2, String district,
                   City objCity, String postalCode, String phone, Date lastUpdate) {
        this.addressId  = addressId;
        this.address    = address;
        this.address2   = address2;
        this.district   = district;
        this.objCity    = objCity;
        this.postalCode = postalCode;
        this.phone      = phone;
        this.lastUpdate = lastUpdate;
    }

    public int getAddressId()               { return addressId; }
    public void setAddressId(int id)        { this.addressId = id; }
    public String getAddress()              { return address; }
    public void setAddress(String a)        { this.address = a; }
    public String getAddress2()             { return address2; }
    public void setAddress2(String a)       { this.address2 = a; }
    public String getDistrict()             { return district; }
    public void setDistrict(String d)       { this.district = d; }
    public City getObjCity()                { return objCity; }
    public void setObjCity(City c)          { this.objCity = c; }
    public String getPostalCode()           { return postalCode; }
    public void setPostalCode(String p)     { this.postalCode = p; }
    public String getPhone()                { return phone; }
    public void setPhone(String p)          { this.phone = p; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        String city = (objCity != null) ? objCity.city : "N/A";
        return String.format("[Address] ID:%-4d | %-25s | %-15s | Tel:%s",
                addressId, address, city, phone);
    }
}
