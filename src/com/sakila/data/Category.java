package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Entidad Category - mapea la tabla `category` de sakila.
 * Representa el genero de una pelicula (Action, Comedy, Drama...).
 *
 * Tabla: category(category_id, name, last_update)
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class Category extends Entity {

    /** PK autoincrement */
    public int categoryId;
    /** Nombre de la categoria */
    public String name;
    /** Fecha ultima actualizacion */
    public Date lastUpdate;

    /** Constructor vacio */
    public Category() {}

    /**
     * Constructor completo.
     * @param categoryId ID de la categoria
     * @param name       nombre
     * @param lastUpdate ultima actualizacion
     */
    public Category(int categoryId, String name, Date lastUpdate) {
        this.categoryId = categoryId;
        this.name       = name;
        this.lastUpdate = lastUpdate;
    }

    public int getCategoryId()              { return categoryId; }
    public void setCategoryId(int id)       { this.categoryId = id; }
    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        return String.format("[Category] ID:%-3d | %s", categoryId, name);
    }
}
