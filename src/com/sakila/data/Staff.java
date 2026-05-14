package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * FK address_id manejada por agregacion. store_id se deja como int
 * para evitar la referencia circular Staff <-> Store.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Staff extends Entity {

    public int staffId;
    public String firstName;
    public String lastName;
    /** FK address_id en forma de objeto. */
    public Address objAddress;
    public String email;
    /** FK store_id como int para romper el ciclo Staff <-> Store. */
    public int storeId;
    /** true=ACTIVO, false=INACTIVO (soft delete). */
    public boolean active;
    public String username;
    public Date lastUpdate;

    public Staff() {
        this.objAddress = new Address();
        this.active = true;
    }

    public Staff(int staffId, String firstName, String lastName, Address objAddress,
                 String email, int storeId, boolean active, String username, Date lastUpdate) {
        this.staffId    = staffId;
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.objAddress = objAddress;
        this.email      = email;
        this.storeId    = storeId;
        this.active     = active;
        this.username   = username;
        this.lastUpdate = lastUpdate;
    }

    public int getStaffId()                 { return staffId; }
    public void setStaffId(int id)          { this.staffId = id; }
    public String getFirstName()            { return firstName; }
    public void setFirstName(String n)      { this.firstName = n; }
    public String getLastName()             { return lastName; }
    public void setLastName(String n)       { this.lastName = n; }
    public Address getObjAddress()          { return objAddress; }
    public void setObjAddress(Address a)    { this.objAddress = a; }
    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }
    public int getStoreId()                 { return storeId; }
    public void setStoreId(int id)          { this.storeId = id; }
    public boolean isActive()               { return active; }
    public void setActive(boolean a)        { this.active = a; }
    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        return String.format("[Staff] ID:%-3d | %-12s %-15s | Tienda:%d | %s | %s",
                staffId, firstName, lastName, storeId, email,
                active ? "ACTIVO" : "INACTIVO");
    }
}
