package com.sakila.controllers;

import com.sakila.data.Staff;
import com.sakila.model.StaffModel;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public class StaffController {
    private StaffModel model;
    public StaffController() { model = new StaffModel(); }

    /** Menu del personal: listar, buscar, alta, activar/desactivar y exports. */
    public void showMenu(Scanner sc) {
        int opt;
        do {
            System.out.println("\n╔═════════════════════════════╗");
            System.out.println("║      GESTION DE STAFF       ║");
            System.out.println("╠═════════════════════════════╣");
            System.out.println("║  1. Listar personal         ║");
            System.out.println("║  2. Buscar por nombre       ║");
            System.out.println("║  3. Buscar por ID           ║");
            System.out.println("║  4. Agregar empleado        ║");
            System.out.println("║  5. Activar/Desactivar      ║");
            System.out.println("║  6. Ver JSON                ║");
            System.out.println("║  7. Ver CSV                 ║");
            System.out.println("║  0. Volver                  ║");
            System.out.println("╚═════════════════════════════╝");
            System.out.print("  Opcion: ");
            opt=sc.nextInt(); sc.nextLine();
            switch(opt) {
                case 1 -> model.Get().forEach(System.out::println);
                case 2 -> { System.out.print("  Nombre: "); model.Get(sc.nextLine()).forEach(System.out::println); }
                case 3 -> { System.out.print("  Staff ID: "); int id=sc.nextInt(); sc.nextLine(); model.Get(id).forEach(System.out::println); }
                case 4 -> addStaff(sc);
                case 5 -> toggleActive(sc);
                case 6 -> { model.Get(); System.out.println(model.Serializer()); }
                case 7 -> { model.Get(); System.out.println(model.SerializerCSV()); }
                case 0 -> {}
                default -> System.out.println("Opcion invalida.");
            }
        } while(opt!=0);
    }

    /** Alta de empleado con datos minimos: nombre, email, tienda. */
    private void addStaff(Scanner sc) {
        // Por simplicidad uso address_id=1 (cualquier direccion existente sirve para la demo)
        System.out.print("  Nombre: "); String fn=sc.nextLine().toUpperCase();
        System.out.print("  Apellido: "); String ln=sc.nextLine().toUpperCase();
        System.out.print("  Email: "); String email=sc.nextLine();
        System.out.print("  Tienda (1/2): "); int store=sc.nextInt(); sc.nextLine();
        System.out.print("  Username: "); String user=sc.nextLine();
        Staff s=new Staff();
        s.firstName=fn; s.lastName=ln; s.email=email; s.storeId=store; s.username=user; s.active=true;
        s.objAddress.addressId=1;
        System.out.println(model.Post(s) ? "  Empleado agregado. ID:"+s.staffId : "  Error: "+model.getMessage());
    }

    /** Soft delete reversible via campo active. */
    private void toggleActive(Scanner sc) {
        // Igual que Customer: invierto active para activar o desactivar al empleado
        System.out.print("  Staff ID: "); int id=sc.nextInt(); sc.nextLine();
        ArrayList<Staff> found=model.Get(id);
        if(found==null||found.isEmpty()){ System.out.println("  No encontrado."); return; }
        Staff s=found.get(0); s.active=!s.active;
        System.out.println(model.Put(s) ? "  Empleado "+(s.active?"ACTIVADO":"DESACTIVADO")+"." : "  Error: "+model.getMessage());
    }

    public void close() { model.close(); }
}
