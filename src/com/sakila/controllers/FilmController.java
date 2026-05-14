package com.sakila.controllers;

import com.sakila.data.*;
import com.sakila.model.FilmModel;
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
public class FilmController {
    private FilmModel model;
    public FilmController() { model = new FilmModel(); }

    /** Menu de peliculas: listar, buscar, CRUD y exports. */
    public void showMenu(Scanner sc) {
        // Loop del menu de peliculas hasta que el usuario elija 0
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

    /** Alta de pelicula. Por simplicidad usa idioma=English y duracion=3 dias. */
    private void addFilm(Scanner sc) {
        // Pido los datos minimos y completo el resto con defaults razonables
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

    /** Actualiza titulo y/o rating. ENTER vacio mantiene el valor actual. */
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

    /** Soft delete: pone rental_rate=0 (no hay campo active en film). */
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
