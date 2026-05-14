package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Language extends Entity {

    public int languageId;
    public String name;
    public Date lastUpdate;

    public Language() {}

    public Language(int languageId, String name, Date lastUpdate) {
        this.languageId = languageId;
        this.name       = name;
        this.lastUpdate = lastUpdate;
    }

    public int getLanguageId()              { return languageId; }
    public void setLanguageId(int id)       { this.languageId = id; }
    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }
    public Date getLastUpdate()             { return lastUpdate; }
    public void setLastUpdate(Date d)       { this.lastUpdate = d; }

    @Override
    public String toString() {
        return String.format("[Language] ID:%-3d | %s", languageId, name);
    }
}
