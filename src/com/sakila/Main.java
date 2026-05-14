package com.sakila;

import com.sakila.controllers.*;
import com.sakila.reports.ReportEngine;
import java.util.Scanner;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public class Main {

    public static void main(String[] args) {
        // Punto de entrada: inicializo controllers, lanzo el menu y cierro conexiones al salir
        Scanner scanner = new Scanner(System.in);

        // Cada controller abre su propia conexion a la DB; se cierran al salir
        ActorController     actorCtrl    = new ActorController();
        FilmController      filmCtrl     = new FilmController();
        CustomerController  customerCtrl = new CustomerController();
        InventoryController invCtrl      = new InventoryController();
        RentalController    rentalCtrl   = new RentalController();
        PaymentController   payCtrl      = new PaymentController();
        StoreController     storeCtrl    = new StoreController();
        StaffController     staffCtrl    = new StaffController();
        AddressController   addrCtrl     = new AddressController();
        ReportEngine        reports      = new ReportEngine();

        printWelcome();

        // Loop principal hasta que el usuario elija 0
        int opt;
        do {
            printMainMenu();
            System.out.print("  Seleccione opcion: ");
            while (!scanner.hasNextInt()) { System.out.print("  Numero valido: "); scanner.next(); }
            opt = scanner.nextInt(); scanner.nextLine();

            switch (opt) {
                case 1  -> actorCtrl.showMenu(scanner);
                case 2  -> filmCtrl.showMenu(scanner);
                case 3  -> customerCtrl.showMenu(scanner);
                case 4  -> invCtrl.showMenu(scanner);
                case 5  -> rentalCtrl.showMenu(scanner);
                case 6  -> payCtrl.showMenu(scanner);
                case 7  -> storeCtrl.showMenu(scanner);
                case 8  -> staffCtrl.showMenu(scanner);
                case 9  -> addrCtrl.showMenu(scanner);
                case 10 -> reports.showReportMenu(scanner);
                case 11 -> printAbout();
                case 0  -> System.out.println("\n  Cerrando sistema...");
                default -> System.out.println("  Opcion invalida.");
            }
        } while (opt != 0);

        // Libera todas las conexiones JDBC antes de salir
        actorCtrl.close(); filmCtrl.close(); customerCtrl.close();
        invCtrl.close();   rentalCtrl.close(); payCtrl.close();
        storeCtrl.close(); staffCtrl.close(); addrCtrl.close();
        reports.close();
        scanner.close();
        System.out.println("  Sistema cerrado.");
    }

    private static void printWelcome() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║        SAKILA ORM DATA MANAGER - INF514 Z06          ║");
        System.out.println("║        Universidad Autonoma de Santo Domingo          ║");
        System.out.println("║        Facultad de Ciencias - Escuela Informatica     ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println("  Conectando a sakila DB... Sistema listo.\n");
    }

    private static void printMainMenu() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║                  MENU PRINCIPAL                      ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║   1. Gestion de Actores                              ║");
        System.out.println("║   2. Gestion de Peliculas (Film)                     ║");
        System.out.println("║   3. Gestion de Clientes (Customer)                  ║");
        System.out.println("║   4. Gestion de Inventario                           ║");
        System.out.println("║   5. Gestion de Rentas                               ║");
        System.out.println("║   6. Gestion de Pagos                                ║");
        System.out.println("║   7. Gestion de Tiendas (Store)                      ║");
        System.out.println("║   8. Gestion de Personal (Staff)                     ║");
        System.out.println("║   9. Gestion de Direcciones (Address)                ║");
        System.out.println("║  10. Reportes y Estadisticas (CSV / JSON)            ║");
        System.out.println("║  11. Acerca del sistema                              ║");
        System.out.println("║   0. Salir                                           ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    private static void printAbout() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              ACERCA DEL SISTEMA                      ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  Sistema:   Sakila ORM Data Manager v1.0             ║");
        System.out.println("║  Curso:     INF514 - Programacion II Java            ║");
        System.out.println("║  Seccion:   Z06                                      ║");
        System.out.println("║  Autor:     Ismailyn Reyes | 100437845               ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
