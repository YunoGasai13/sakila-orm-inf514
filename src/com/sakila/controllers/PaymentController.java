package com.sakila.controllers;

import com.sakila.data.Payment;
import com.sakila.model.PaymentModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public class PaymentController {
    private PaymentModel model;
    public PaymentController() { model = new PaymentModel(); }

    /** Menu de pagos: listar, buscar, registrar y exports. */
    public void showMenu(Scanner sc) {
        int opt;
        do {
            System.out.println("\n╔═════════════════════════════╗");
            System.out.println("║      GESTION DE PAGOS       ║");
            System.out.println("╠═════════════════════════════╣");
            System.out.println("║  1. Listar pagos recientes  ║");
            System.out.println("║  2. Buscar por ID           ║");
            System.out.println("║  3. Registrar pago          ║");
            System.out.println("║  4. Total + Promedio        ║");
            System.out.println("║  5. Ver JSON                ║");
            System.out.println("║  6. Ver CSV                 ║");
            System.out.println("║  0. Volver                  ║");
            System.out.println("╚═════════════════════════════╝");
            System.out.print("  Opcion: ");
            opt=sc.nextInt(); sc.nextLine();
            switch(opt) {
                case 1 -> model.Get().forEach(System.out::println);
                case 2 -> { System.out.print("  Payment ID: "); int id=sc.nextInt(); sc.nextLine(); model.Get(id).forEach(System.out::println); }
                case 3 -> addPayment(sc);
                case 4 -> showStats();
                case 5 -> { model.Get(); System.out.println(model.Serializer()); }
                case 6 -> { model.Get(); System.out.println(model.SerializerCSV()); }
                case 0 -> {}
                default -> System.out.println("Opcion invalida.");
            }
        } while(opt!=0);
    }

    /** Registra un pago ligando customer + rental + staff. */
    private void addPayment(Scanner sc) {
        // Solo necesito los IDs de las entidades relacionadas; el payment_id se asigna en el modelo
        System.out.print("  Customer ID: "); int cid=sc.nextInt();
        System.out.print("  Rental ID: "); int rid=sc.nextInt();
        System.out.print("  Staff ID (1/2): "); int sid=sc.nextInt();
        System.out.print("  Monto (ej:4.99): "); BigDecimal amt=sc.nextBigDecimal(); sc.nextLine();
        Payment p=new Payment();
        p.objCustomer.customerId=cid;
        p.objRental.rentalId=rid;
        p.objStaff.staffId=sid;
        p.amount=amt;
        System.out.println(model.Post(p) ? "  Pago registrado. ID:"+p.paymentId : "  Error: "+model.getMessage());
    }

    /** Total y promedio sobre la lista actual cargada. */
    private void showStats() {
        // Refresca la lista y muestra metricas calculadas con Streams
        ArrayList<Payment> data = model.Get();
        System.out.println("  Registros: " + (data!=null?data.size():0));
        System.out.println("  Total:    $" + model.getTotalAmount());
        System.out.println("  Promedio: $" + model.getAverageAmount());
    }

    public void close() { model.close(); }
}
