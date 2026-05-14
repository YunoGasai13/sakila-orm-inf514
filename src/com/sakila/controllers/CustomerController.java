package com.sakila.controllers;

import com.sakila.data.Customer;
import com.sakila.model.CustomerModel;
import com.sakila.util.Validator;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public class CustomerController {
    private CustomerModel model;
    public CustomerController() { model = new CustomerModel(); }

    /** Menu de clientes: listar, buscar, alta, activar/desactivar y exports. */
    public void showMenu(Scanner sc) {
        int opt;
        do {
            System.out.println("\n╔═════════════════════════════╗");
            System.out.println("║    GESTION DE CLIENTES      ║");
            System.out.println("╠═════════════════════════════╣");
            System.out.println("║  1. Listar clientes         ║");
            System.out.println("║  2. Buscar por nombre       ║");
            System.out.println("║  3. Buscar por ID           ║");
            System.out.println("║  4. Agregar cliente         ║");
            System.out.println("║  5. Activar/Desactivar      ║");
            System.out.println("║  6. Ver JSON                ║");
            System.out.println("║  7. Ver CSV                 ║");
            System.out.println("║  0. Volver                  ║");
            System.out.println("╚═════════════════════════════╝");
            System.out.print("  Opcion: ");
            opt=sc.nextInt(); sc.nextLine();
            switch(opt) {
                case 1 -> model.Get().forEach(System.out::println);
                case 2 -> { System.out.print("  Nombre/email: "); model.Get(sc.nextLine()).forEach(System.out::println); }
                case 3 -> { System.out.print("  Customer ID: "); int id=sc.nextInt(); sc.nextLine(); model.Get(id).forEach(System.out::println); }
                case 4 -> addCustomer(sc);
                case 5 -> toggleActive(sc);
                case 6 -> { model.Get(); System.out.println(model.Serializer()); }
                case 7 -> { model.Get(); System.out.println(model.SerializerCSV()); }
                case 0 -> {}
                default -> System.out.println("Opcion invalida.");
            }
        } while(opt!=0);
    }

    /** Alta de cliente. El email se valida con regex antes de insertar. */
    private void addCustomer(Scanner sc) {
        // Email se valida con Validator antes de seguir, si falla aborto la operacion
        System.out.print("  Nombre: "); String fn=sc.nextLine().toUpperCase();
        System.out.print("  Apellido: "); String ln=sc.nextLine().toUpperCase();
        System.out.print("  Email: "); String email=sc.nextLine();
        if(!Validator.isValidEmail(email)){ System.out.println("  Email invalido. Formato: usuario@dominio.com"); return; }
        System.out.print("  Tienda (1/2): "); int store=sc.nextInt(); sc.nextLine();
        Customer c=new Customer();
        c.firstName=fn; c.lastName=ln; c.email=email; c.active=true;
        c.objStore.storeId=store;
        c.objAddress.addressId=1;
        System.out.println(model.Post(c) ? "  Cliente agregado. ID:"+c.customerId : "  Error: "+model.getMessage());
    }

    /** Activa o desactiva el cliente (soft delete reversible via campo active). */
    private void toggleActive(Scanner sc) {
        // Invierte el valor de active y guarda. Permite reactivar un cliente sin perder su historial
        System.out.print("  Customer ID: "); int id=sc.nextInt(); sc.nextLine();
        ArrayList<Customer> found=model.Get(id);
        if(found==null||found.isEmpty()){ System.out.println("  No encontrado."); return; }
        Customer c=found.get(0); c.active=!c.active;
        System.out.println(model.Put(c) ? "  Cliente "+(c.active?"ACTIVADO":"DESACTIVADO (soft delete)")+"." : "  Error: "+model.getMessage());
    }

    public void close() { model.close(); }
}
