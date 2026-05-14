package com.sakila.data;

import java.time.LocalDate;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * Padre abstracto de todas las entidades. toString() obligatorio en cada hija.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public abstract class Entity {

    /** Fecha de hoy, usada en los campos last_update de cada tabla. */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    @Override
    public abstract String toString();
}
