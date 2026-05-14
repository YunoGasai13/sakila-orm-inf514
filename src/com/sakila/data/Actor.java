package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Actor extends Entity {

    public int actorId;
    public String firstName;
    public String lastName;
    public Date lastUpdate;

    public Actor() {}

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
