package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Entidad Actor - mapea la tabla `actor` de sakila.
 * Sin FK externas. Relacionada con Film via tabla film_actor.
 *
 * Tabla: actor(actor_id, first_name, last_name, last_update)
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class Actor extends Entity {

    /** PK autoincrement */
    public int actorId;
    /** Nombre del actor */
    public String firstName;
    /** Apellido del actor */
    public String lastName;
    /** Fecha ultima actualizacion */
    public Date lastUpdate;

    /** Constructor vacio */
    public Actor() {}

    /**
     * Constructor completo.
     * @param actorId    ID del actor
     * @param firstName  nombre
     * @param lastName   apellido
     * @param lastUpdate ultima actualizacion
     */
    public Actor(int actorId, String firstName, String lastName, Date lastUpdate) {
        this.actorId    = actorId;
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.lastUpdate = lastUpdate;
    }

    public int getActorId()                     { return actorId; }
    public void setActorId(int id)              { this.actorId = id; }
    public String getFirstName()                { return firstName; }
    public void setFirstName(String n)          { this.firstName = n; }
    public String getLastName()                 { return lastName; }
    public void setLastName(String n)           { this.lastName = n; }
    public Date getLastUpdate()                 { return lastUpdate; }
    public void setLastUpdate(Date d)           { this.lastUpdate = d; }

    @Override
    public String toString() {
        return String.format("[Actor] ID:%-4d | %-12s %-15s", actorId, firstName, lastName);
    }
}
