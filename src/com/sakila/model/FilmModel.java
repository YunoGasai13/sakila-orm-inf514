package com.sakila.model;

import com.sakila.data.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public final class FilmModel extends DataContext implements iDatapost {

    private ArrayList<Film> allData;
    /** Modelo auxiliar para resolver la FK language_id por agregacion. */
    private LanguageModel langModel;

    /** tabla=film | PK=film_id | busqueda por title | FK=language_id. */
    public FilmModel() {
        super("film", "film_id", "title",
              "language_id", "title", "last_update");
        langModel = new LanguageModel();
    }

    /** ResultSet -> ArrayList<Film>. Inyecta el objeto Language en cada Film (FK agregada). */
    @Override
    public void Mapping(ResultSet rSet) {
        // Convierto cada fila en un Film y le adjunto su Language por agregacion
        allData = new ArrayList<>();
        if (rSet == null) return;
        // Precarga idiomas una sola vez para evitar N+1 queries
        if (langModel.getData() == null || langModel.getData().isEmpty())
            langModel.Get(true);
        try {
            while (rSet.next()) {
                // Resuelvo la FK language_id buscando en memoria
                int langId = rSet.getInt("language_id");
                Language objLang = (Language) langModel.inMemSearch(langId);
                if (objLang == null) objLang = new Language(langId, "Unknown", null);

                Film f = new Film(
                    rSet.getInt("film_id"),
                    rSet.getString("title"),
                    rSet.getString("description"),
                    rSet.getInt("release_year"),
                    objLang,
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

    /** Modo full: trae hasta 5000 films (usado por InventoryModel para resolver FK). */
    public ArrayList<Film> Get(boolean full) {
        Mapping(super.Find(full));
        return allData;
    }

    @Override
    public boolean Post(Entity odata) {
        // Asigno el siguiente film_id (MAX+1) y delego el INSERT al padre
        Film f = (Film) odata;
        f.filmId     = (int)(super.getMaxID() + 1);
        f.lastUpdate = new Date();
        return super.dbPost(SerializerMap(f));
    }

    @Override public boolean Put(Entity odata)    { return super.dbPut(SerializerMap(odata)); }

    /** Film no tiene campo active; se desactiva poniendo rental_rate=0. */
    @Override
    public boolean Delete(Entity odata) {
        // Sakila.film no tiene active, asi que marco la pelicula con precio 0 = no rentable
        Film f = (Film) odata;
        HashMap<String, String> map = SerializerMap(f);
        map.put("rental_rate", "0.00");
        return super.dbPut(map);
    }

    @Override
    public HashMap<String, String> SerializerMap(Entity odata) {
        // Vuelco cada campo del Film a su columna; el FK language_id sale del objeto agregado
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

    /** Cierra tambien el modelo auxiliar de idiomas. */
    @Override
    public void close() {
        super.close();
        if (langModel != null) langModel.close();
    }
}
