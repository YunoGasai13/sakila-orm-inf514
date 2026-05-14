package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * FK store_id y address_id manejadas por agregacion de objetos.
 * El campo active permite soft delete.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Customer extends Entity {

    public int customerId;
    /** FK store_id en forma de objeto. */
    public Store objStore;
    public String firstName;
    public String lastName;
    public String email;
    /** FK address_id en forma de objeto. */
    public Address objAddress;
    /** true=ACTIVO, false=INACTIVO. Cambia a false en el soft delete. */
    public boolean active;
    public Date createDate;
    public Date lastUpdate;

    public Customer() {
        this.active = true;
        this.objStore = new Store();
        this.objAddress = new Address();
    }

    public Customer(int customerId, Store objStore, String firstName, String lastName,
                    String email, Address objAddress, boolean active,
                    Date createDate, Date lastUpdate) {
        this.customerId = customerId;
        this.objStore   = objStore;
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.email      = email;
        this.objAddress = objAddress;
        this.active     = active;
        this.createDate = createDate;
        this.lastUpdate = lastUpdate;
    }

    public int getCustomerId()              { return customerId; }
    public void setCustomerId(int id)       { this.customerId = id; }
    public Store getObjStore()              { return objStore; }
    public void setObjStore(Store s)        { this.objStore = s; }
    public String getFirstName()            { return firstName; }
    public void setFirstName(String n)      { this.firstName = n; }
    public String getLastName()             { return lastName; }
    public void setLastName(String n)       { this.lastName = n; }
    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }
    public Address getObjAddress()          { return objAddress; }
    public void setObjAddress(Address a)    { this.objAddress = a; }
    public boolean isActive()               { return active; }
    public void setActive(boolean a)        { this.active = a; }
    public Date getCreateDate()             { return createDate; }
    public void setCreateDate(Date d)       { this.createDate = d; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        int sid = (objStore != null) ? objStore.storeId : 0;
        return String.format("[Customer] ID:%-4d | %-12s %-15s | %-35s | Tienda:%d | %s",
                customerId, firstName, lastName, email, sid,
                active ? "ACTIVO" : "INACTIVO");
    }
}
