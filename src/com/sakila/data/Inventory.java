package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * FK film_id y store_id manejadas por agregacion de objetos.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Inventory extends Entity {

    public int inventoryId;
    /** FK film_id en forma de objeto. Acceso: inventory.objFilm.title */
    public Film objFilm;
    /** FK store_id en forma de objeto. */
    public Store objStore;
    public Date lastUpdate;

    public Inventory() {
        this.objFilm = new Film();
        this.objStore = new Store();
    }

    public Inventory(int inventoryId, Film objFilm, Store objStore, Date lastUpdate) {
        this.inventoryId = inventoryId;
        this.objFilm     = objFilm;
        this.objStore    = objStore;
        this.lastUpdate  = lastUpdate;
    }

    public int getInventoryId()             { return inventoryId; }
    public void setInventoryId(int id)      { this.inventoryId = id; }
    public Film getObjFilm()                { return objFilm; }
    public void setObjFilm(Film f)          { this.objFilm = f; }
    public Store getObjStore()              { return objStore; }
    public void setObjStore(Store s)        { this.objStore = s; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        String filmTitle = (objFilm != null) ? objFilm.title : "N/A";
        int sid = (objStore != null) ? objStore.storeId : 0;
        return String.format("[Inventory] ID:%-5d | Film: %-25s | Tienda: %d",
                inventoryId, filmTitle, sid);
    }
}
