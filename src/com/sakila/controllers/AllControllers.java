package com.sakila.controllers;

import com.sakila.data.*;
import com.sakila.model.*;
import com.sakila.util.Validator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * INF514 Z06 | FilmController - Controlador MVC para Peliculas
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 */
public class FilmController {
    private FilmModel model;
    public FilmController() { model = new FilmModel(); }

    public void showMenu(Scanner sc) {
        int opt;
        do {
            System.out.println("\n╔═════════════════════════════╗");
            System.out.println("║    GESTION DE PELICULAS     ║");
            System.out.println("╠═════════════════════════════╣");
            System.out.println("║  1. Listar peliculas        ║");
            System.out.println("║  2. Buscar por titulo       ║");
            System.out.println("║  3. Buscar por ID           ║");
            System.out.println("║  4. Agregar pelicula        ║");
            System.out.println("║  5. Actualizar pelicula     ║");
            System.out.println("║  6. Desactivar pelicula     ║");
            System.out.println("║  7. Ver JSON                ║");
            System.out.println("║  8. Ver CSV                 ║");
            System.out.println("║  0. Volver                  ║");
            System.out.println("╚═════════════════════════════╝");
            System.out.print("  Opcion: ");
            opt = sc.nextInt(); sc.nextLine();
            switch(opt) {
                case 1 -> { model.Get().forEach(System.out::println); }
                case 2 -> { System.out.print("  Titulo: "); model.Get(sc.nextLine()).forEach(System.out::println); }
                case 3 -> { System.out.print("  Film ID: "); int id=sc.nextInt(); sc.nextLine(); model.Get(id).forEach(System.out::println); }
                case 4 -> addFilm(sc);
                case 5 -> updateFilm(sc);
                case 6 -> deleteFilm(sc);
                case 7 -> { model.Get(); System.out.println(model.Serializer()); }
                case 8 -> { model.Get(); System.out.println(model.SerializerCSV()); }
                case 0 -> {}
                default -> System.out.println("Opcion invalida.");
            }
        } while(opt!=0);
    }

    private void addFilm(Scanner sc) {
        System.out.print("  Titulo: "); String title=sc.nextLine().toUpperCase();
        System.out.print("  Descripcion: "); String desc=sc.nextLine();
        System.out.print("  Año (ej:2024): "); int yr=sc.nextInt();
        System.out.print("  Duracion (min): "); int len=sc.nextInt();
        System.out.print("  Precio alquiler (ej:2.99): "); BigDecimal rate=sc.nextBigDecimal();
        System.out.print("  Rating (G/PG/PG-13/R/NC-17): "); sc.nextLine(); String rating=sc.nextLine();
        Film f=new Film(); f.title=title; f.description=desc; f.releaseYear=yr;
        f.length=len; f.rentalRate=rate; f.rating=rating;
        f.rentalDuration=3; f.replacementCost=new BigDecimal("19.99");
        f.objLanguage=new Language(1,"English",null);
        System.out.println(model.Post(f) ? "  Pelicula agregada. ID:"+f.filmId : "  Error: "+model.getMessage());
    }

    private void updateFilm(Scanner sc) {
        System.out.print("  Film ID: "); int id=sc.nextInt(); sc.nextLine();
        ArrayList<Film> found=model.Get(id);
        if(found==null||found.isEmpty()){ System.out.println("  No encontrado."); return; }
        Film f=found.get(0); System.out.println("  Actual: "+f);
        System.out.print("  Nuevo titulo (ENTER omite): "); String t=sc.nextLine();
        System.out.print("  Nuevo rating (ENTER omite): "); String r=sc.nextLine();
        if(!t.isBlank()) f.title=t.toUpperCase();
        if(!r.isBlank()) f.rating=r;
        System.out.println(model.Put(f) ? "  Actualizado." : "  Error: "+model.getMessage());
    }

    private void deleteFilm(Scanner sc) {
        System.out.print("  Film ID: "); int id=sc.nextInt(); sc.nextLine();
        ArrayList<Film> found=model.Get(id);
        if(found==null||found.isEmpty()){ System.out.println("  No encontrado."); return; }
        System.out.println("  Pelicula: "+found.get(0));
        System.out.print("  Confirmar [S/N]: ");
        if(sc.nextLine().equalsIgnoreCase("S"))
            System.out.println(model.Delete(found.get(0)) ? "  Desactivada (rental_rate=0)." : "  Error: "+model.getMessage());
        else System.out.println("  Cancelado.");
    }

    public void close() { model.close(); }
}


/**
 * INF514 Z06 | CustomerController - Controlador MVC para Clientes
 * Usa Validator para validar email con expresiones regulares.
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 */
public class CustomerController {
    private CustomerModel model;
    public CustomerController() { model = new CustomerModel(); }

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

    private void addCustomer(Scanner sc) {
        System.out.print("  Nombre: "); String fn=sc.nextLine().toUpperCase();
        System.out.print("  Apellido: "); String ln=sc.nextLine().toUpperCase();
        System.out.print("  Email: "); String email=sc.nextLine();
        // Validacion con expresion regular (requerido por normas generales)
        if(!Validator.isValidEmail(email)){ System.out.println("  Email invalido. Formato: usuario@dominio.com"); return; }
        System.out.print("  Tienda (1/2): "); int store=sc.nextInt(); sc.nextLine();
        Customer c=new Customer(); c.firstName=fn; c.lastName=ln; c.email=email; c.storeId=store; c.addressId=1; c.active=true;
        System.out.println(model.Post(c) ? "  Cliente agregado. ID:"+c.customerId : "  Error: "+model.getMessage());
    }

    private void toggleActive(Scanner sc) {
        System.out.print("  Customer ID: "); int id=sc.nextInt(); sc.nextLine();
        ArrayList<Customer> found=model.Get(id);
        if(found==null||found.isEmpty()){ System.out.println("  No encontrado."); return; }
        Customer c=found.get(0); c.active=!c.active;
        System.out.println(model.Put(c) ? "  Cliente "+(c.active?"ACTIVADO":"DESACTIVADO (soft delete)")+"." : "  Error: "+model.getMessage());
    }

    public void close() { model.close(); }
}


/**
 * INF514 Z06 | InventoryController - Controlador MVC para Inventario
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 */
public class InventoryController {
    private InventoryModel model;
    public InventoryController() { model = new InventoryModel(); }

    public void showMenu(Scanner sc) {
        int opt;
        do {
            System.out.println("\n╔═════════════════════════════╗");
            System.out.println("║    GESTION DE INVENTARIO    ║");
            System.out.println("╠═════════════════════════════╣");
            System.out.println("║  1. Ver inventario          ║");
            System.out.println("║  2. Buscar por ID           ║");
            System.out.println("║  3. Ver JSON                ║");
            System.out.println("║  4. Ver CSV                 ║");
            System.out.println("║  0. Volver                  ║");
            System.out.println("╚═════════════════════════════╝");
            System.out.print("  Opcion: ");
            opt=sc.nextInt(); sc.nextLine();
            switch(opt) {
                case 1 -> model.Get().forEach(System.out::println);
                case 2 -> { System.out.print("  Inventory ID: "); int id=sc.nextInt(); sc.nextLine(); model.Get(id).forEach(System.out::println); }
                case 3 -> { model.Get(); System.out.println(model.Serializer()); }
                case 4 -> { model.Get(); System.out.println(model.SerializerCSV()); }
                case 0 -> {}
                default -> System.out.println("Opcion invalida.");
            }
        } while(opt!=0);
    }

    public void close() { model.close(); }
}


/**
 * INF514 Z06 | RentalController - Controlador MVC para Rentas
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 */
public class RentalController {
    private RentalModel model;
    public RentalController() { model = new RentalModel(); }

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

    private void addRental(Scanner sc) {
        System.out.print("  Customer ID: "); int cid=sc.nextInt();
        System.out.print("  Inventory ID: "); int iid=sc.nextInt();
        System.out.print("  Staff ID (1/2): "); int sid=sc.nextInt(); sc.nextLine();
        Rental r=new Rental();
        r.objCustomer=new Customer(); r.objCustomer.customerId=cid;
        r.objInventory=new Inventory(); r.objInventory.inventoryId=iid;
        r.staffId=sid;
        System.out.println(model.Post(r) ? "  Renta creada. ID:"+r.rentalId : "  Error: "+model.getMessage());
    }

    private void markReturned(Scanner sc) {
        System.out.print("  Rental ID a marcar devuelta: "); int id=sc.nextInt(); sc.nextLine();
        ArrayList<Rental> found=model.Get(id);
        if(found==null||found.isEmpty()){ System.out.println("  No encontrado."); return; }
        System.out.println(model.Delete(found.get(0)) ? "  Marcada como devuelta (return_date=hoy)." : "  Error: "+model.getMessage());
    }

    public void close() { model.close(); }
}
