package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Category extends Entity {

    public int categoryId;
    public String name;
    public Date lastUpdate;

    public Category() {}

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
