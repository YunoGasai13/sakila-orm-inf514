package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * FK address_id manejada por agregacion. manager_staff_id se deja
 * como int para romper el ciclo Staff <-> Store.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Store extends Entity {

    public int storeId;
    /** FK manager_staff_id como int para romper el ciclo Staff <-> Store. */
    public int managerStaffId;
    /** FK address_id en forma de objeto. */
    public Address objAddress;
    public Date lastUpdate;

    public Store() {
        this.objAddress = new Address();
    }

    public Store(int storeId, int managerStaffId, Address objAddress, Date lastUpdate) {
        this.storeId        = storeId;
        this.managerStaffId = managerStaffId;
        this.objAddress     = objAddress;
        this.lastUpdate     = lastUpdate;
    }

    public int getStoreId()                 { return storeId; }
    public void setStoreId(int id)          { this.storeId = id; }
    public int getManagerStaffId()          { return managerStaffId; }
    public void setManagerStaffId(int id)   { this.managerStaffId = id; }
    public Address getObjAddress()          { return objAddress; }
    public void setObjAddress(Address a)    { this.objAddress = a; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        String addr = (objAddress != null) ? objAddress.address : "N/A";
        return String.format("[Store] ID:%-2d | Manager:%d | Direccion: %s",
                storeId, managerStaffId, addr);
    }
}
