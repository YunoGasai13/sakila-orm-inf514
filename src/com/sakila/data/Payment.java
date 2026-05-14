package com.sakila.data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * FK customer_id, staff_id y rental_id manejadas por agregacion.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Payment extends Entity {

    public int paymentId;
    /** FK customer_id en forma de objeto. */
    public Customer objCustomer;
    /** FK staff_id en forma de objeto. */
    public Staff objStaff;
    /** FK rental_id en forma de objeto. */
    public Rental objRental;
    public BigDecimal amount;
    public Date paymentDate;
    public Date lastUpdate;

    public Payment() {
        this.objCustomer = new Customer();
        this.objStaff    = new Staff();
        this.objRental   = new Rental();
    }

    public Payment(int paymentId, Customer objCustomer, Staff objStaff, Rental objRental,
                   BigDecimal amount, Date paymentDate, Date lastUpdate) {
        this.paymentId   = paymentId;
        this.objCustomer = objCustomer;
        this.objStaff    = objStaff;
        this.objRental   = objRental;
        this.amount      = amount;
        this.paymentDate = paymentDate;
        this.lastUpdate  = lastUpdate;
    }

    public int getPaymentId()               { return paymentId; }
    public void setPaymentId(int id)        { this.paymentId = id; }
    public Customer getObjCustomer()        { return objCustomer; }
    public void setObjCustomer(Customer c)  { this.objCustomer = c; }
    public Staff getObjStaff()              { return objStaff; }
    public void setObjStaff(Staff s)        { this.objStaff = s; }
    public Rental getObjRental()            { return objRental; }
    public void setObjRental(Rental r)      { this.objRental = r; }
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
