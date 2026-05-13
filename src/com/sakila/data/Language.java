package com.sakila.data;

import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Entidad Language - mapea la tabla `language` de sakila.
 * Sin FK externas. Usada por Film mediante composicion.
 *
 * Tabla: language(language_id, name, last_update)
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class Language extends Entity {

    /** PK autoincrement */
    public int languageId;
    /** Nombre del idioma (ej: "English", "French") */
    public String name;
    /** Fecha ultima actualizacion */
    public Date lastUpdate;

    /** Constructor vacio */
    public Language() {}

    /**
     * Constructor completo.
     * @param languageId ID del idioma
     * @param name       nombre del idioma
     * @param lastUpdate ultima actualizacion
     */
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
