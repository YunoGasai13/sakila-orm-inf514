package com.sakila.model;

import com.sakila.data.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Modelo FilmModel - hijo CONCRETO y FINAL de DataContext.
 * Gestiona el ArrayList de objetos Film con CRUD completo.
 *
 * En el Mapping() se construye el objeto Language embebido en Film
 * mediante un JOIN implicito para respetar la agregacion de FK:
 *   film.objLanguage.name => "English"
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public final class FilmModel extends DataContext implements iDatapost {

    /** ArrayList con las peliculas cargadas en memoria */
    private ArrayList<Film> allData;
    /** Modelo de Language para resolver la FK por agregacion */
    private LanguageModel langModel;

    /**
     * Constructor: configura DataContext para la tabla film.
     * Tabla: film | PK: film_id | Busqueda: title | FK: language_id
     */
    public FilmModel() {
        super("film", "film_id", "title",
              "language_id", "title", "last_update");
        langModel = new LanguageModel();
    }

    /**
     * Mapea ResultSet a objetos Film.
     * Construye el objeto Language embebido (FK por agregacion).
     * Usa inMemSearch del LanguageModel para resolver la FK en memoria.
     *
     * @param rSet ResultSet de la tabla film
     */
    @Override
    public void Mapping(ResultSet rSet) {
        allData = new ArrayList<>();
        if (rSet == null) return;
        // Carga todos los idiomas en memoria para resolver FK
        if (langModel.getData() == null || langModel.getData().isEmpty())
            langModel.Get(true);
        try {
            while (rSet.next()) {
                int langId = rSet.getInt("language_id");
                Language objLang = (Language) langModel.inMemSearch(langId);
                if (objLang == null) objLang = new Language(langId, "Unknown", null);

                Film f = new Film(
                    rSet.getInt("film_id"),
                    rSet.getString("title"),
                    rSet.getString("description"),
                    rSet.getInt("release_year"),
                    objLang,                         // FK resuelta por agregacion
                    rSet.getInt("rental_duration"),
                    rSet.getBigDecimal("rental_rate"),
                    rSet.getInt("length"),
                    rSet.getBigDecimal("replacement_cost"),
                    rSet.getString("rating"),
                    rSet.getDate("last_update")
                );
                allData.add(f);
            }
            rSet.close();
        } catch (SQLException e) {
            actionMessage = "Mapping Film: " + e.getMessage();
        }
    }

    @Override public ArrayList<Film> Get()                      { Mapping(super.Find());       return allData; }
    @Override public ArrayList<Film> Get(Object id)             { Mapping(super.Find(id));     return allData; }
    @Override public ArrayList<Film> Get(String search)         { Mapping(super.Find(search)); return allData; }
    @Override public ArrayList<Film> Get(String s, Object fk)   { Mapping(super.Find(s,fk));   return allData; }
    @Override public ArrayList<Film> Get(Date d1, Date d2)      { Mapping(super.Find(d1,d2));  return allData; }
    @Override public ArrayList<Film> getData()                   { return allData; }

    /**
     * Obtiene lista completa de peliculas (hasta 5000).
     * @param full true para modo completo
     * @return ArrayList con todas las peliculas
     */
    public ArrayList<Film> Get(boolean full) {
        Mapping(super.Find(full));
        return allData;
    }

    @Override
    public boolean Post(Entity odata) {
        Film f = (Film) odata;
        f.filmId     = (int)(super.getMaxID() + 1);
        f.lastUpdate = new Date();
        return super.dbPost(SerializerMap(f));
    }

    @Override public boolean Put(Entity odata)    { return super.dbPut(SerializerMap(odata)); }

    /**
     * Delete: Film no tiene campo active.
     * Se usa soft delete cambiando rental_rate a 0 (pelicula desactivada).
     */
    @Override
    public boolean Delete(Entity odata) {
        Film f = (Film) odata;
        HashMap<String, String> map = SerializerMap(f);
        map.put("rental_rate", "0.00"); // precio 0 = desactivada
        return super.dbPut(map);
    }

    @Override
    public HashMap<String, String> SerializerMap(Entity odata) {
        if (!(odata instanceof Film)) return null;
        Film f = (Film) odata;
        int langId = (f.objLanguage != null) ? f.objLanguage.languageId : 1;
        HashMap<String, String> map = new HashMap<>();
        map.put("film_id",          String.valueOf(f.filmId));
        map.put("title",            f.title != null ? f.title : "");
        map.put("description",      f.description != null ? f.description : "");
        map.put("release_year",     String.valueOf(f.releaseYear));
        map.put("language_id",      String.valueOf(langId));
        map.put("rental_duration",  String.valueOf(f.rentalDuration > 0 ? f.rentalDuration : 3));
        map.put("rental_rate",      f.rentalRate != null ? f.rentalRate.toString() : "2.99");
        map.put("length",           String.valueOf(f.length));
        map.put("replacement_cost", f.replacementCost != null ? f.replacementCost.toString() : "19.99");
        map.put("rating",           f.rating != null ? f.rating : "G");
        map.put("last_update",      Entity.getCurrentDate().toString());
        return map;
    }

    @Override
    public String Serializer() {
        if (allData == null || allData.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        char sep = ' ';
        for (Film f : allData) {
            String lang = (f.objLanguage != null) ? f.objLanguage.name : "N/A";
            sb.append(sep).append("{film_id:").append(f.filmId)
              .append(",title:\"").append(f.title)
              .append("\",rating:\"").append(f.rating)
              .append("\",language:\"").append(lang)
              .append("\",rental_rate:").append(f.rentalRate).append("}");
            sep = ',';
        }
        return sb.append("]").toString();
    }

    @Override
    public String SerializerCSV() {
        if (allData == null || allData.isEmpty()) return "film_id,title,rating,language,rental_rate\n(sin datos)";
        StringBuilder sb = new StringBuilder("film_id,title,rating,language,rental_rate,length\n");
        for (Film f : allData) {
            String lang = (f.objLanguage != null) ? f.objLanguage.name : "N/A";
            sb.append(f.filmId).append(",\"").append(f.title).append("\",")
              .append(f.rating).append(",").append(lang).append(",")
              .append(f.rentalRate).append(",").append(f.length).append("\n");
        }
        return sb.toString();
    }

    @Override
    public Entity inMemSearch(Object pid) {
        if (allData == null) return null;
        int id = Integer.parseInt(pid.toString());
        List<Film> found = allData.stream().filter(f -> f.filmId == id).collect(Collectors.toList());
        return found.isEmpty() ? null : found.get(0);
    }

    /** Cierra tambien el LanguageModel auxiliar */
    @Override
    public void close() {
        super.close();
        if (langModel != null) langModel.close();
    }
}
