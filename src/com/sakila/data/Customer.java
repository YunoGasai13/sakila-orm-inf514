package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Entidad Customer - mapea la tabla `customer` de sakila.
 *
 * NOTA SOBRE DELETE:
 * El campo `active` (1/0) permite el "soft delete":
 * en lugar de borrar el registro, se marca active=0 (inactivo).
 * Esto respeta la integridad referencial (rentas y pagos quedan intactos).
 *
 * Tabla: customer(customer_id, store_id, first_name, last_name,
 *                 email, address_id, active, create_date, last_update)
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class Customer extends Entity {

    /** PK autoincrement */
    public int customerId;
    /** FK tienda (1 o 2) */
    public int storeId;
    /** Nombre */
    public String firstName;
    /** Apellido */
    public String lastName;
    /** Correo electronico */
    public String email;
    /** FK address */
    public int addressId;
    /**
     * Estado activo/inactivo.
     * TRUE = activo | FALSE = inactivo (soft delete).
     * El Delete() del modelo cambia este campo a false en lugar de eliminar.
     */
    public boolean active;
    /** Fecha de registro del cliente */
    public Date createDate;
    /** Fecha ultima actualizacion */
    public Date lastUpdate;

    /** Constructor vacio */
    public Customer() { this.active = true; }

    /**
     * Constructor completo.
     */
    public Customer(int customerId, int storeId, String firstName, String lastName,
                    String email, int addressId, boolean active,
                    Date createDate, Date lastUpdate) {
        this.customerId = customerId;
        this.storeId    = storeId;
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.email      = email;
        this.addressId  = addressId;
        this.active     = active;
        this.createDate = createDate;
        this.lastUpdate = lastUpdate;
    }

    public int getCustomerId()              { return customerId; }
    public void setCustomerId(int id)       { this.customerId = id; }
    public int getStoreId()                 { return storeId; }
    public void setStoreId(int id)          { this.storeId = id; }
    public String getFirstName()            { return firstName; }
    public void setFirstName(String n)      { this.firstName = n; }
    public String getLastName()             { return lastName; }
    public void setLastName(String n)       { this.lastName = n; }
    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }
    public int getAddressId()               { return addressId; }
    public void setAddressId(int id)        { this.addressId = id; }
    public boolean isActive()               { return active; }
    public void setActive(boolean a)        { this.active = a; }
    public Date getCreateDate()             { return createDate; }
    public void setCreateDate(Date d)       { this.createDate = d; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        return String.format("[Customer] ID:%-4d | %-12s %-15s | %-35s | Tienda:%d | %s",
                customerId, firstName, lastName, email, storeId,
                active ? "ACTIVO" : "INACTIVO");
    }
}
