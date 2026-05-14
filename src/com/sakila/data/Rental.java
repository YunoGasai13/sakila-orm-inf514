package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * FK customer_id, inventory_id y staff_id manejadas por agregacion.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Rental extends Entity {

    public int rentalId;
    public Date rentalDate;
    /** FK inventory_id en forma de objeto. */
    public Inventory objInventory;
    /** FK customer_id en forma de objeto. */
    public Customer objCustomer;
    /** null = renta pendiente de devolver. */
    public Date returnDate;
    /** FK staff_id en forma de objeto. */
    public Staff objStaff;
    public Date lastUpdate;

    public Rental() {
        this.objInventory = new Inventory();
        this.objCustomer  = new Customer();
        this.objStaff     = new Staff();
    }

    public Rental(int rentalId, Date rentalDate, Inventory objInventory,
                  Customer objCustomer, Date returnDate, Staff objStaff, Date lastUpdate) {
        this.rentalId     = rentalId;
        this.rentalDate   = rentalDate;
        this.objInventory = objInventory;
        this.objCustomer  = objCustomer;
        this.returnDate   = returnDate;
        this.objStaff     = objStaff;
        this.lastUpdate   = lastUpdate;
    }

    public int getRentalId()                    { return rentalId; }
    public void setRentalId(int id)             { this.rentalId = id; }
    public Date getRentalDate()                 { return rentalDate; }
    public void setRentalDate(Date d)           { this.rentalDate = d; }
    public Inventory getObjInventory()          { return objInventory; }
    public void setObjInventory(Inventory i)    { this.objInventory = i; }
    public Customer getObjCustomer()            { return objCustomer; }
    public void setObjCustomer(Customer c)      { this.objCustomer = c; }
    public Date getReturnDate()                 { return returnDate; }
    public void setReturnDate(Date d)           { this.returnDate = d; }
    public Staff getObjStaff()                  { return objStaff; }
    public void setObjStaff(Staff s)            { this.objStaff = s; }
    public Date getLastUpdate()                 { return lastUpdate; }
    public void setLastUpdate(Date d)           { this.lastUpdate = d; }

    @Override
    public String toString() {
        String customer  = (objCustomer != null)  ? objCustomer.firstName + " " + objCustomer.lastName : "N/A";
        String film      = (objInventory != null && objInventory.objFilm != null) ? objInventory.objFilm.title : "N/A";
        String returned  = (returnDate != null)   ? returnDate.toString() : "PENDIENTE";
        return String.format("[Rental] ID:%-5d | %-20s | Film:%-25s | Dev:%s",
                rentalId, customer, film, returned);
    }
}
