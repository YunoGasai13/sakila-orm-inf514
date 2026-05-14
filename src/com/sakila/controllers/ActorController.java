package com.sakila.controllers;

import com.sakila.data.Actor;
import com.sakila.model.ActorModel;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public class ActorController {
    private ActorModel model;
    public ActorController() { model = new ActorModel(); }

    /** Menu de actores: listar, buscar, CRUD y exports. */
    public void showMenu(Scanner sc) {
        // Loop del menu: muestra opciones y enruta a la accion correspondiente hasta que el usuario elija 0
        int opt;
        do {
            System.out.println("\n╔═════════════════════════════╗");
            System.out.println("║     GESTION DE ACTORES      ║");
            System.out.println("╠═════════════════════════════╣");
            System.out.println("║  1. Listar actores          ║");
            System.out.println("║  2. Buscar por nombre       ║");
            System.out.println("║  3. Buscar por ID           ║");
            System.out.println("║  4. Agregar actor           ║");
            System.out.println("║  5. Actualizar actor        ║");
            System.out.println("║  6. Eliminar actor (soft)   ║");
            System.out.println("║  7. Ver como JSON           ║");
            System.out.println("║  8. Ver como CSV            ║");
            System.out.println("║  0. Volver                  ║");
            System.out.println("╚═════════════════════════════╝");
            System.out.print("  Opcion: ");
            opt = sc.nextInt(); sc.nextLine();
            switch(opt) {
                case 1 -> { ArrayList<Actor> list = model.Get(); list.forEach(System.out::println); System.out.println("Total: "+list.size()); }
                case 2 -> { System.out.print("  Nombre/Apellido: "); String s=sc.nextLine(); model.Get(s).forEach(System.out::println); }
                case 3 -> { System.out.print("  Actor ID: "); int id=sc.nextInt(); sc.nextLine(); model.Get(id).forEach(System.out::println); }
                case 4 -> addActor(sc);
                case 5 -> updateActor(sc);
                case 6 -> deleteActor(sc);
                case 7 -> { model.Get(); System.out.println(model.Serializer()); }
                case 8 -> { model.Get(); System.out.println(model.SerializerCSV()); }
                case 0 -> {}
                default -> System.out.println("Opcion invalida.");
            }
        } while (opt != 0);
    }

    /** Crea un actor nuevo pidiendo nombre y apellido. */
    private void addActor(Scanner sc) {
        // Leo datos por consola, armo el objeto y lo envio al modelo para que haga el INSERT
        System.out.print("  Nombre: "); String fn = sc.nextLine().toUpperCase();
        System.out.print("  Apellido: "); String ln = sc.nextLine().toUpperCase();
        Actor a = new Actor(); a.firstName=fn; a.lastName=ln;
        System.out.println(model.Post(a) ? "  Actor agregado. ID: "+a.actorId : "  Error: "+model.getMessage());
    }

    /** Actualiza nombre y/o apellido. ENTER vacio mantiene el valor actual. */
    private void updateActor(Scanner sc) {
        // Busco el actor por ID, muestro el actual, leo los cambios y disparo el UPDATE
        System.out.print("  ID del actor: "); int id=sc.nextInt(); sc.nextLine();
        ArrayList<Actor> found = model.Get(id);
        if(found==null||found.isEmpty()){ System.out.println("  No encontrado."); return; }
        Actor a = found.get(0); System.out.println("  Actual: "+a);
        System.out.print("  Nuevo nombre (ENTER omite): "); String fn=sc.nextLine();
        System.out.print("  Nuevo apellido (ENTER omite): "); String ln=sc.nextLine();
        if(!fn.isBlank()) a.firstName=fn.toUpperCase();
        if(!ln.isBlank()) a.lastName=ln.toUpperCase();
        System.out.println(model.Put(a) ? "  Actualizado." : "  Error: "+model.getMessage());
    }

    /** Soft delete con confirmacion. */
    private void deleteActor(Scanner sc) {
        // Confirmo antes de ejecutar para evitar borrados accidentales
        System.out.print("  ID del actor a desactivar: "); int id=sc.nextInt(); sc.nextLine();
        ArrayList<Actor> found = model.Get(id);
        if(found==null||found.isEmpty()){ System.out.println("  No encontrado."); return; }
        System.out.println("  Actor: "+found.get(0));
        System.out.print("  Confirmar desactivacion [S/N]: ");
        if(sc.nextLine().equalsIgnoreCase("S"))
            System.out.println(model.Delete(found.get(0)) ? "  Desactivado (soft delete)." : "  Error: "+model.getMessage());
        else System.out.println("  Cancelado.");
    }

    public void close() { model.close(); }
}
