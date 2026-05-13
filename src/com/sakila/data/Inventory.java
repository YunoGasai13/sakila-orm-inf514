package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Entidad Inventory - mapea la tabla `inventory` de sakila.
 * Representa una copia fisica de una pelicula en una tienda.
 *
 * FK gestionada por AGREGACION:
 *   Film objFilm  (en lugar de int filmId)
 *
 * Tabla: inventory(inventory_id, film_id, store_id, last_update)
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class Inventory extends Entity {

    /** PK autoincrement */
    public int inventoryId;
    /**
     * FK film_id gestionada por AGREGACION.
     * Acceso: inventory.objFilm.title
     */
    public Film objFilm;
    /** FK tienda donde esta la copia */
    public int storeId;
    /** Fecha ultima actualizacion */
    public Date lastUpdate;

    /** Constructor vacio */
    public Inventory() { this.objFilm = new Film(); }

    /**
     * Constructor completo.
     * @param inventoryId ID del inventario
     * @param objFilm     objeto Film (FK por agregacion)
     * @param storeId     FK tienda
     * @param lastUpdate  ultima actualizacion
     */
    public Inventory(int inventoryId, Film objFilm, int storeId, Date lastUpdate) {
        this.inventoryId = inventoryId;
        this.objFilm     = objFilm;
        this.storeId     = storeId;
        this.lastUpdate  = lastUpdate;
    }

    public int getInventoryId()             { return inventoryId; }
    public void setInventoryId(int id)      { this.inventoryId = id; }
    public Film getObjFilm()                { return objFilm; }
    public void setObjFilm(Film f)          { this.objFilm = f; }
    public int getStoreId()                 { return storeId; }
    public void setStoreId(int id)          { this.storeId = id; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        String filmTitle = (objFilm != null) ? objFilm.title : "N/A";
        return String.format("[Inventory] ID:%-5d | Film: %-25s | Tienda: %d",
                inventoryId, filmTitle, storeId);
    }
}
