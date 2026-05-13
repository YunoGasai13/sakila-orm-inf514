package com.sakila.reports;

import com.sakila.model.*;
import com.sakila.data.*;
import java.sql.ResultSet;
import java.util.Scanner;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Clase ReportEngine - modulo completo de reportes y estadisticas.
 *
 * Reportes disponibles:
 *   1. Top 10 peliculas mas rentadas
 *   2. Pagos totales por tienda
 *   3. Rentas pendientes de devolucion (Aging)
 *   4. Top actores por cantidad de peliculas
 *   5. Estadisticas globales (total rentas, pagos, clientes activos)
 *   6. Renta por ciudad/pais (via JOIN)
 *   7. Export CSV de cualquier entidad
 *   8. Export JSON de cualquier entidad
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public class ReportEngine {

    private PaymentModel  payModel;
    private RentalModel   rentalModel;
    private FilmModel     filmModel;
    private CustomerModel customerModel;
    private ActorModel    actorModel;

    /**
     * Constructor: inicializa todos los modelos necesarios para reportes.
     */
    public ReportEngine() {
        payModel      = new PaymentModel();
        rentalModel   = new RentalModel();
        filmModel     = new FilmModel();
        customerModel = new CustomerModel();
        actorModel    = new ActorModel();
    }

    // ─── Reportes ─────────────────────────────────────────────────────────────

    /**
     * Reporte 1: Top 10 peliculas mas rentadas.
     * SQL con JOIN entre rental, inventory y film.
     */
    public void reportTopFilms() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║   ESTADISTICAS: TOP 10 PELICULAS MAS RENTADAS        ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        String sql = "SELECT f.title, COUNT(r.rental_id) AS total " +
                     "FROM rental r JOIN inventory i ON r.inventory_id=i.inventory_id " +
                     "JOIN film f ON i.film_id=f.film_id " +
                     "GROUP BY f.film_id ORDER BY total DESC LIMIT 10";
        try {
            ResultSet rs = rentalModel.Find(sql, true);
            int rank=1;
            while(rs!=null && rs.next())
                System.out.printf("║  %2d. %-35s | Rentas: %-5d      ║%n",rank++,rs.getString("title"),rs.getInt("total"));
            if(rs!=null) rs.close();
        } catch (Exception e) { System.out.println("Error: "+e.getMessage()); }
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    /**
     * Reporte 2: Total y promedio de pagos por tienda (agrupado por staff_id).
     */
    public void reportPaymentsByStore() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║   ESTADISTICAS: PAGOS/COBRANZAS POR TIENDA           ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        String sql = "SELECT p.staff_id, COUNT(*) AS total_pagos, SUM(amount) AS total, AVG(amount) AS promedio " +
                     "FROM payment p GROUP BY p.staff_id";
        try {
            ResultSet rs = payModel.Find(sql,true);
            while(rs!=null && rs.next())
                System.out.printf("║  Tienda/Staff:%-3d | Pagos:%-5d | Total:$%-10.2f | Prom:$%.2f    ║%n",
                        rs.getInt("staff_id"),rs.getInt("total_pagos"),rs.getDouble("total"),rs.getDouble("promedio"));
            if(rs!=null) rs.close();
        } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    /**
     * Reporte 3: Rentas pendientes de devolucion (Aging cuentas por cobrar).
     */
    public void reportPendingRentals() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║   AGING: RENTAS PENDIENTES DE DEVOLUCION             ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        String sql = "SELECT r.rental_id, CONCAT(c.first_name,' ',c.last_name) AS cliente, " +
                     "f.title, r.rental_date FROM rental r " +
                     "JOIN customer c ON r.customer_id=c.customer_id " +
                     "JOIN inventory i ON r.inventory_id=i.inventory_id " +
                     "JOIN film f ON i.film_id=f.film_id " +
                     "WHERE r.return_date IS NULL ORDER BY r.rental_date LIMIT 15";
        try {
            ResultSet rs = rentalModel.Find(sql,true);
            int cnt=0;
            while(rs!=null && rs.next()) {
                cnt++;
                System.out.printf("║  #%-5d %-20s | %-25s | %s ║%n",
                        rs.getInt("rental_id"),rs.getString("cliente"),rs.getString("title"),rs.getDate("rental_date"));
            }
            System.out.printf("║  Total pendientes mostrados: %-3d                        ║%n",cnt);
            if(rs!=null) rs.close();
        } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    /**
     * Reporte 4: Top 10 actores con mas peliculas en el catalogo.
     * Actores y peliculas en las que participa.
     */
    public void reportTopActors() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║   ESTADISTICAS: TOP ACTORES POR PELICULAS            ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        String sql = "SELECT a.first_name, a.last_name, COUNT(fa.film_id) AS num_films " +
                     "FROM actor a JOIN film_actor fa ON a.actor_id=fa.actor_id " +
                     "GROUP BY a.actor_id ORDER BY num_films DESC LIMIT 10";
        try {
            ResultSet rs = actorModel.Find(sql,true);
            int rank=1;
            while(rs!=null && rs.next())
                System.out.printf("║  %2d. %-12s %-15s | Peliculas: %-3d            ║%n",
                        rank++,rs.getString("first_name"),rs.getString("last_name"),rs.getInt("num_films"));
            if(rs!=null) rs.close();
        } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    /**
     * Reporte 5: Rentas por ciudad y pais.
     */
    public void reportRentalsByCityCountry() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║   ESTADISTICAS: RENTAS POR CIUDAD / PAIS             ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        String sql = "SELECT ci.city, co.country, COUNT(r.rental_id) AS rentas " +
                     "FROM rental r JOIN customer cu ON r.customer_id=cu.customer_id " +
                     "JOIN address a ON cu.address_id=a.address_id " +
                     "JOIN city ci ON a.city_id=ci.city_id " +
                     "JOIN country co ON ci.country_id=co.country_id " +
                     "GROUP BY ci.city_id ORDER BY rentas DESC LIMIT 10";
        try {
            ResultSet rs = rentalModel.Find(sql,true);
            while(rs!=null && rs.next())
                System.out.printf("║  %-20s %-15s | Rentas: %-6d          ║%n",
                        rs.getString("city"),rs.getString("country"),rs.getInt("rentas"));
            if(rs!=null) rs.close();
        } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    /**
     * Reporte 6: Estadisticas globales del sistema.
     */
    public void reportGlobalStats() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║   ESTADISTICAS GLOBALES DEL SISTEMA                  ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        try {
            ResultSet rs;
            rs = rentalModel.Find("SELECT COUNT(*) AS t FROM rental", true);
            if(rs!=null&&rs.next()) { System.out.printf("║  Total rentas          : %-10d                   ║%n",rs.getInt("t")); rs.close(); }
            rs = payModel.Find("SELECT COUNT(*) AS t, SUM(amount) AS s, AVG(amount) AS a FROM payment", true);
            if(rs!=null&&rs.next()) {
                System.out.printf("║  Total pagos           : %-10d                   ║%n",rs.getInt("t"));
                System.out.printf("║  Total cobrado         : $%-10.2f                  ║%n",rs.getDouble("s"));
                System.out.printf("║  Promedio por pago     : $%-10.2f                  ║%n",rs.getDouble("a"));
                rs.close();
            }
            rs = filmModel.Find("SELECT COUNT(*) AS t FROM film", true);
            if(rs!=null&&rs.next()) { System.out.printf("║  Peliculas catalogo    : %-10d                   ║%n",rs.getInt("t")); rs.close(); }
            rs = customerModel.Find("SELECT COUNT(*) AS t FROM customer WHERE active=1", true);
            if(rs!=null&&rs.next()) { System.out.printf("║  Clientes activos      : %-10d                   ║%n",rs.getInt("t")); rs.close(); }
            rs = rentalModel.Find("SELECT COUNT(*) AS t FROM rental WHERE return_date IS NULL", true);
            if(rs!=null&&rs.next()) { System.out.printf("║  Rentas pendientes     : %-10d                   ║%n",rs.getInt("t")); rs.close(); }
        } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    /**
     * Exporta la lista de actores en CSV y JSON.
     */
    public void exportActors() {
        System.out.println("\n--- EXPORT CSV: ACTORES ---");
        actorModel.Get();
        System.out.println(actorModel.SerializerCSV());
        System.out.println("\n--- EXPORT JSON: ACTORES ---");
        System.out.println(actorModel.Serializer());
    }

    /**
     * Exporta la lista de peliculas en CSV y JSON.
     */
    public void exportFilms() {
        System.out.println("\n--- EXPORT CSV: PELICULAS ---");
        filmModel.Get();
        System.out.println(filmModel.SerializerCSV());
        System.out.println("\n--- EXPORT JSON: PELICULAS ---");
        System.out.println(filmModel.Serializer());
    }

    /**
     * Exporta la lista de clientes en CSV y JSON.
     */
    public void exportCustomers() {
        System.out.println("\n--- EXPORT CSV: CLIENTES ---");
        customerModel.Get();
        System.out.println(customerModel.SerializerCSV());
        System.out.println("\n--- EXPORT JSON: CLIENTES ---");
        System.out.println(customerModel.Serializer());
    }

    /**
     * Muestra el menu de reportes y ejecuta la opcion seleccionada.
     * @param scanner Scanner para leer la opcion del usuario
     */
    public void showReportMenu(Scanner scanner) {
        int opt;
        do {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║       REPORTES Y ESTADISTICAS        ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Top peliculas rentadas           ║");
            System.out.println("║  2. Pagos por tienda                 ║");
            System.out.println("║  3. Aging: rentas pendientes         ║");
            System.out.println("║  4. Top actores por peliculas        ║");
            System.out.println("║  5. Rentas por ciudad/pais           ║");
            System.out.println("║  6. Estadisticas globales            ║");
            System.out.println("║  7. Export CSV+JSON actores          ║");
            System.out.println("║  8. Export CSV+JSON peliculas        ║");
            System.out.println("║  9. Export CSV+JSON clientes         ║");
            System.out.println("║  0. Volver                           ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("  Opcion: ");
            opt = scanner.nextInt(); scanner.nextLine();
            switch(opt) {
                case 1 -> reportTopFilms();
                case 2 -> reportPaymentsByStore();
                case 3 -> reportPendingRentals();
                case 4 -> reportTopActors();
                case 5 -> reportRentalsByCityCountry();
                case 6 -> reportGlobalStats();
                case 7 -> exportActors();
                case 8 -> exportFilms();
                case 9 -> exportCustomers();
                case 0 -> System.out.println("Regresando...");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opt != 0);
    }

    /**
     * Cierra todos los modelos y libera conexiones DB.
     */
    public void close() {
        payModel.close(); rentalModel.close(); filmModel.close();
        customerModel.close(); actorModel.close();
    }
}
