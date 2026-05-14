package com.sakila.data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * FK language_id manejada por agregacion de objeto Language.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class Film extends Entity {

    public int filmId;
    public String title;
    public String description;
    public int releaseYear;
    /** FK language_id en forma de objeto. Acceso: film.objLanguage.name */
    public Language objLanguage;
    public int rentalDuration;
    public BigDecimal rentalRate;
    public int length;
    public BigDecimal replacementCost;
    /** G, PG, PG-13, R, NC-17 */
    public String rating;
    public Date lastUpdate;

    public Film() {
        this.objLanguage = new Language();
    }

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
