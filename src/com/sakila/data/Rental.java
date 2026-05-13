package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Entidad Rental - mapea la tabla `rental` de sakila.
 * Representa una transaccion de alquiler de pelicula.
 *
 * FK gestionadas por AGREGACION/COMPOSICION:
 *   Customer objCustomer   (en lugar de int customerId)
 *   Inventory objInventory (en lugar de int inventoryId)
 *
 * Acceso: rental.objCustomer.firstName
 *         rental.objInventory.objFilm.title
 *
 * Tabla: rental(rental_id, rental_date, inventory_id,
 *               customer_id, return_date, staff_id, last_update)
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class Rental extends Entity {

    /** PK autoincrement */
    public int rentalId;
    /** Fecha en que se alquilo */
    public Date rentalDate;
    /**
     * FK inventory_id gestionada por AGREGACION.
     * Acceso: rental.objInventory.objFilm.title
     */
    public Inventory objInventory;
    /**
     * FK customer_id gestionada por AGREGACION.
     * Acceso: rental.objCustomer.firstName
     */
    public Customer objCustomer;
    /** Fecha de devolucion (null si no ha sido devuelta = PENDIENTE) */
    public Date returnDate;
    /** FK empleado que proceso */
    public int staffId;
    /** Fecha ultima actualizacion */
    public Date lastUpdate;

    /** Constructor vacio */
    public Rental() {
        this.objInventory = new Inventory();
        this.objCustomer  = new Customer();
    }

    /**
     * Constructor completo con objetos embebidos.
     */
    public Rental(int rentalId, Date rentalDate, Inventory objInventory,
                  Customer objCustomer, Date returnDate, int staffId, Date lastUpdate) {
        this.rentalId     = rentalId;
        this.rentalDate   = rentalDate;
        this.objInventory = objInventory;
        this.objCustomer  = objCustomer;
        this.returnDate   = returnDate;
        this.staffId      = staffId;
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
    public int getStaffId()                     { return staffId; }
    public void setStaffId(int id)              { this.staffId = id; }
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
