package com.sakila.data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Entidad Film - mapea la tabla `film` de sakila.
 *
 * FK gestionada por COMPOSICION/AGREGACION:
 *   Language objLanguage  <--- en lugar de int languageId
 *
 * Acceso: film.objLanguage.name  (ej: "English")
 *
 * Tabla: film(film_id, title, description, release_year, language_id,
 *             rental_duration, rental_rate, length, replacement_cost,
 *             rating, last_update)
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class Film extends Entity {

    /** PK autoincrement */
    public int filmId;
    /** Titulo de la pelicula */
    public String title;
    /** Sinopsis */
    public String description;
    /** Año de lanzamiento */
    public int releaseYear;
    /**
     * FK language_id gestionada por AGREGACION de objeto Language.
     * Ejemplo: film.objLanguage.name => "English"
     */
    public Language objLanguage;
    /** Duracion del alquiler en dias */
    public int rentalDuration;
    /** Precio de alquiler */
    public BigDecimal rentalRate;
    /** Duracion en minutos */
    public int length;
    /** Costo de reemplazo */
    public BigDecimal replacementCost;
    /** Clasificacion: G, PG, PG-13, R, NC-17 */
    public String rating;
    /** Fecha ultima actualizacion */
    public Date lastUpdate;

    /** Constructor vacio */
    public Film() {
        this.objLanguage = new Language();
    }

    /**
     * Constructor completo con objeto Language embebido.
     * @param filmId          ID de la pelicula
     * @param title           titulo
     * @param description     sinopsis
     * @param releaseYear     año
     * @param objLanguage     objeto Language (FK por agregacion)
     * @param rentalDuration  dias de alquiler
     * @param rentalRate      precio alquiler
     * @param length          duracion minutos
     * @param replacementCost costo reemplazo
     * @param rating          clasificacion
     * @param lastUpdate      ultima actualizacion
     */
    public Film(int filmId, String title, String description, int releaseYear,
                Language objLanguage, int rentalDuration, BigDecimal rentalRate,
                int length, BigDecimal replacementCost, String rating, Date lastUpdate) {
        this.filmId          = filmId;
        this.title           = title;
        this.description     = description;
        this.releaseYear     = releaseYear;
        this.objLanguage     = objLanguage;
        this.rentalDuration  = rentalDuration;
        this.rentalRate      = rentalRate;
        this.length          = length;
        this.replacementCost = replacementCost;
        this.rating          = rating;
        this.lastUpdate      = lastUpdate;
    }

    public int getFilmId()                      { return filmId; }
    public void setFilmId(int id)               { this.filmId = id; }
    public String getTitle()                    { return title; }
    public void setTitle(String t)              { this.title = t; }
    public String getDescription()              { return description; }
    public void setDescription(String d)        { this.description = d; }
    public int getReleaseYear()                 { return releaseYear; }
    public void setReleaseYear(int y)           { this.releaseYear = y; }
    public Language getObjLanguage()            { return objLanguage; }
    public void setObjLanguage(Language l)      { this.objLanguage = l; }
    public int getRentalDuration()              { return rentalDuration; }
    public void setRentalDuration(int d)        { this.rentalDuration = d; }
    public BigDecimal getRentalRate()           { return rentalRate; }
    public void setRentalRate(BigDecimal r)     { this.rentalRate = r; }
    public int getLength()                      { return length; }
    public void setLength(int l)               { this.length = l; }
    public BigDecimal getReplacementCost()      { return replacementCost; }
    public void setReplacementCost(BigDecimal c){ this.replacementCost = c; }
    public String getRating()                   { return rating; }
    public void setRating(String r)             { this.rating = r; }
    public Date getLastUpdate()                 { return lastUpdate; }
    public void setLastUpdate(Date d)           { this.lastUpdate = d; }

    @Override
    public String toString() {
        String lang = (objLanguage != null) ? objLanguage.name : "N/A";
        return String.format("[Film] ID:%-4d | %-30s | %s | $%-5s | %dmin | %s",
                filmId, title, rating, rentalRate, length, lang);
    }
}
