package com.sakila.controllers;

import com.sakila.data.Customer;
import com.sakila.data.Inventory;
import com.sakila.data.Rental;
import com.sakila.model.RentalModel;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public class RentalController {
    private RentalModel model;
    public RentalController() { model = new RentalModel(); }

    /** Menu de rentas: listar, nueva renta, marcar devuelta y exports. */
    public void showMenu(Scanner sc) {
        int opt;
        do {
            System.out.println("\n╔═════════════════════════════╗");
            System.out.println("║     GESTION DE RENTAS       ║");
            System.out.println("╠═════════════════════════════╣");
            System.out.println("║  1. Ver rentas recientes    ║");
            System.out.println("║  2. Buscar por ID           ║");
            System.out.println("║  3. Nueva renta             ║");
            System.out.println("║  4. Marcar devuelta         ║");
            System.out.println("║  5. Ver JSON                ║");
            System.out.println("║  6. Ver CSV                 ║");
            System.out.println("║  0. Volver                  ║");
            System.out.println("╚═════════════════════════════╝");
            System.out.print("  Opcion: ");
            opt=sc.nextInt(); sc.nextLine();
            switch(opt) {
                case 1 -> model.Get().forEach(System.out::println);
                case 2 -> { System.out.print("  Rental ID: "); int id=sc.nextInt(); sc.nextLine(); model.Get(id).forEach(System.out::println); }
                case 3 -> addRental(sc);
                case 4 -> markReturned(sc);
                case 5 -> { model.Get(); System.out.println(model.Serializer()); }
                case 6 -> { model.Get(); System.out.println(model.SerializerCSV()); }
                case 0 -> {}
                default -> System.out.println("Opcion invalida.");
            }
        } while(opt!=0);
    }

    /** Crea una renta nueva ligando customer + inventory + staff. */
    private void addRental(Scanner sc) {
        // Pido los IDs de customer, inventory y staff; el modelo genera el rental_id automaticamente
        System.out.print("  Customer ID: "); int cid=sc.nextInt();
        System.out.print("  Inventory ID: "); int iid=sc.nextInt();
        System.out.print("  Staff ID (1/2): "); int sid=sc.nextInt(); sc.nextLine();
        Rental r=new Rental();
        r.objCustomer=new Customer(); r.objCustomer.customerId=cid;
        r.objInventory=new Inventory(); r.objInventory.inventoryId=iid;
        r.objStaff.staffId=sid;
        System.out.println(model.Post(r) ? "  Renta creada. ID:"+r.rentalId : "  Error: "+model.getMessage());
    }

    /** Marca la renta como devuelta poniendo return_date = hoy. */
    private void markReturned(Scanner sc) {
        // Reutilizo el Delete() del modelo que pone return_date = hoy
        System.out.print("  Rental ID a marcar devuelta: "); int id=sc.nextInt(); sc.nextLine();
        ArrayList<Rental> found=model.Get(id);
        if(found==null||found.isEmpty()){ System.out.println("  No encontrado."); return; }
        System.out.println(model.Delete(found.get(0)) ? "  Marcada como devuelta (return_date=hoy)." : "  Error: "+model.getMessage());
    }

    public void close() { model.close(); }
}
