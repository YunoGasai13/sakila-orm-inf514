package com.sakila;

import com.sakila.controllers.*;
import com.sakila.reports.ReportEngine;
import java.util.Scanner;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * Clase Principal Main - punto de entrada de la aplicacion.
 * Implementa la interfaz de usuario en CONSOLA con menu do-while.
 *
 * Arquitectura del proyecto:
 *   com.sakila.data/       - Entidades (mapean tablas DB). Patron: Entity padre abstracto
 *   com.sakila.model/      - ORM: DataContext (abstract padre) + iDatapost (interface CRUD)
 *   com.sakila.controllers/- MVC Controllers: ActorCtrl, FilmCtrl, CustomerCtrl, etc.
 *   com.sakila.reports/    - Reportes y estadisticas con export CSV/JSON
 *   com.sakila.util/       - PropertyFile (config), Validator (regex)
 *
 * Patron FK por AGREGACION: City{Country objCountry}, Film{Language objLanguage}
 * Soft Delete: active=0 en lugar de DELETE fisico
 * Metodos DataContext FINAL: los hijos no pueden hacer override
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public class Main {

    /**
     * Metodo principal - entrada de la aplicacion.
     * Inicializa controladores y muestra el menu principal en loop do-while.
     *
     * @param args argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Inicializar todos los controladores (patron MVC)
        ActorController    actorCtrl    = new ActorController();
        FilmController     filmCtrl     = new FilmController();
        CustomerController customerCtrl = new CustomerController();
        InventoryController invCtrl     = new InventoryController();
        RentalController   rentalCtrl   = new RentalController();
        ReportEngine       reports      = new ReportEngine();

        printWelcome();

        int opt;
        do {
            printMainMenu();
            System.out.print("  Seleccione opcion: ");
            while (!scanner.hasNextInt()) { System.out.print("  Numero valido: "); scanner.next(); }
            opt = scanner.nextInt(); scanner.nextLine();

            switch (opt) {
                case 1 -> actorCtrl.showMenu(scanner);
                case 2 -> filmCtrl.showMenu(scanner);
                case 3 -> customerCtrl.showMenu(scanner);
                case 4 -> invCtrl.showMenu(scanner);
                case 5 -> rentalCtrl.showMenu(scanner);
                case 6 -> reports.showReportMenu(scanner);
                case 7 -> printAbout();
                case 0 -> System.out.println("\n  Cerrando sistema...");
                default -> System.out.println("  Opcion invalida.");
            }
        } while (opt != 0);

        // Cerrar todos los recursos al salir
        actorCtrl.close(); filmCtrl.close(); customerCtrl.close();
        invCtrl.close(); rentalCtrl.close(); reports.close();
        scanner.close();
        System.out.println("  Sistema cerrado. ¡Hasta luego!");
    }

    /**
     * Imprime el banner de bienvenida.
     */
    private static void printWelcome() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║        SAKILA ORM DATA MANAGER - INF514 Z06          ║");
        System.out.println("║        Universidad Autonoma de Santo Domingo          ║");
        System.out.println("║        Facultad de Ciencias - Escuela Informatica     ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println("  Conectando a sakila DB... Sistema listo.\n");
    }

    /**
     * Imprime el menu principal.
     */
    private static void printMainMenu() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║                  MENU PRINCIPAL                      ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  1. Gestion de Actores                               ║");
        System.out.println("║  2. Gestion de Peliculas (Film)                      ║");
        System.out.println("║  3. Gestion de Clientes (Customer)                   ║");
        System.out.println("║  4. Gestion de Inventario                            ║");
        System.out.println("║  5. Gestion de Rentas                                ║");
        System.out.println("║  6. Reportes y Estadisticas (CSV / JSON)             ║");
        System.out.println("║  7. Acerca del sistema                               ║");
        System.out.println("║  0. Salir                                            ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    /**
     * Imprime informacion del sistema y del autor.
     */
    private static void printAbout() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              ACERCA DEL SISTEMA                      ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  Sistema:   Sakila ORM Data Manager v1.0             ║");
        System.out.println("║  Curso:     INF514 - Programacion II Java            ║");
        System.out.println("║  Seccion:   Z06                                      ║");
        System.out.println("║  Autor:     [TU NOMBRE] | [TU MATRICULA]             ║");
        System.out.println("║  Profesor:  Mae. Silverio Del Orbe                   ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║  Arquitectura:                                       ║");
        System.out.println("║  - Patron ORM: DataContext (abstract padre FINAL)    ║");
        System.out.println("║  - Interface:  iDatapost (CRUD: Post/Get/Put/Delete) ║");
        System.out.println("║  - FK:         Agregacion de objetos (City{Country}) ║");
        System.out.println("║  - Delete:     Soft delete (active=0, no SQL DELETE) ║");
        System.out.println("║  - Colecciones: ArrayList, HashMap, Streams          ║");
        System.out.println("║  - Regex:       Validator (email, telefono, fecha)   ║");
        System.out.println("║  - Export:      CSV + JSON por cada entidad          ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
