package com.sakila.data;

import java.time.LocalDate;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Clase abstracta Entity - padre de TODAS las entidades de datos.
 * Cada tabla de sakila tiene una clase hija que hereda de aqui.
 *
 * Provee la fecha actual como utilitario estatico.
 * Define toString() como abstracto para forzar implementacion en hijos.
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public abstract class Entity {

    /**
     * Retorna la fecha actual del sistema.
     * Metodo utilitario estatico accesible desde cualquier hijo.
     *
     * @return LocalDate con la fecha de hoy
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * Cada entidad hija debe implementar su representacion en texto.
     * Usado para mostrar datos en consola.
     *
     * @return String con los datos del objeto formateados
     */
    @Override
    public abstract String toString();
}
