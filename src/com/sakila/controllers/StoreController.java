package com.sakila.controllers;

import com.sakila.model.StoreModel;
import java.util.Scanner;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public class StoreController {
    private StoreModel model;
    public StoreController() { model = new StoreModel(); }

    /** Menu de tiendas: listar, buscar, exports. */
    public void showMenu(Scanner sc) {
        int opt;
        do {
            System.out.println("\n╔═════════════════════════════╗");
            System.out.println("║     GESTION DE TIENDAS      ║");
            System.out.println("╠═════════════════════════════╣");
            System.out.println("║  1. Listar tiendas          ║");
            System.out.println("║  2. Buscar por ID           ║");
            System.out.println("║  3. Ver JSON                ║");
            System.out.println("║  4. Ver CSV                 ║");
            System.out.println("║  0. Volver                  ║");
            System.out.println("╚═════════════════════════════╝");
            System.out.print("  Opcion: ");
            opt=sc.nextInt(); sc.nextLine();
            switch(opt) {
                case 1 -> model.Get().forEach(System.out::println);
                case 2 -> { System.out.print("  Store ID: "); int id=sc.nextInt(); sc.nextLine(); model.Get(id).forEach(System.out::println); }
                case 3 -> { model.Get(); System.out.println(model.Serializer()); }
                case 4 -> { model.Get(); System.out.println(model.SerializerCSV()); }
                case 0 -> {}
                default -> System.out.println("Opcion invalida.");
            }
        } while(opt!=0);
    }

    public void close() { model.close(); }
}
