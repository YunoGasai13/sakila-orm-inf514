package com.sakila.data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Entidad Payment - mapea la tabla `payment` de sakila.
 * Representa un pago realizado por un cliente.
 *
 * FK gestionada por AGREGACION:
 *   Customer objCustomer  (en lugar de int customerId)
 *
 * Tabla: payment(payment_id, customer_id, staff_id, rental_id,
 *                amount, payment_date, last_update)
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class Payment extends Entity {

    /** PK autoincrement */
    public int paymentId;
    /**
     * FK customer_id gestionada por AGREGACION.
     * Acceso: payment.objCustomer.firstName
     */
    public Customer objCustomer;
    /** FK empleado que proceso */
    public int staffId;
    /** FK alquiler asociado */
    public int rentalId;
    /** Monto pagado */
    public BigDecimal amount;
    /** Fecha del pago */
    public Date paymentDate;
    /** Fecha ultima actualizacion */
    public Date lastUpdate;

    /** Constructor vacio */
    public Payment() { this.objCustomer = new Customer(); }

    /**
     * Constructor completo con objeto Customer embebido.
     */
    public Payment(int paymentId, Customer objCustomer, int staffId, int rentalId,
                   BigDecimal amount, Date paymentDate, Date lastUpdate) {
        this.paymentId   = paymentId;
        this.objCustomer = objCustomer;
        this.staffId     = staffId;
        this.rentalId    = rentalId;
        this.amount      = amount;
        this.paymentDate = paymentDate;
        this.lastUpdate  = lastUpdate;
    }

    public int getPaymentId()               { return paymentId; }
    public void setPaymentId(int id)        { this.paymentId = id; }
    public Customer getObjCustomer()        { return objCustomer; }
    public void setObjCustomer(Customer c)  { this.objCustomer = c; }
    public int getStaffId()                 { return staffId; }
    public void setStaffId(int id)          { this.staffId = id; }
    public int getRentalId()                { return rentalId; }
    public void setRentalId(int id)         { this.rentalId = id; }
    public BigDecimal getAmount()           { return amount; }
    public void setAmount(BigDecimal a)     { this.amount = a; }
    public Date getPaymentDate()            { return paymentDate; }
    public void setPaymentDate(Date d)      { this.paymentDate = d; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        String customer = (objCustomer != null) ? objCustomer.firstName + " " + objCustomer.lastName : "N/A";
        return String.format("[Payment] ID:%-5d | %-20s | $%-8s | %s",
                paymentId, customer, amount, paymentDate);
    }
}
