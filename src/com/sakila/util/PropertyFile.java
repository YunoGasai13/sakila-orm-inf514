package com.sakila.util;

import java.io.*;
import java.util.Properties;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Clase utilitaria para leer config.properties.
 * Evita hardcodear credenciales en el codigo fuente (buena practica).
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public class PropertyFile {

    /** Objeto que almacena todas las propiedades del archivo */
    private Properties configProps = null;

    /**
     * Constructor: carga config.properties desde el directorio raiz del proyecto.
     */
    public PropertyFile() {
        try {
            File configFile = new File("config.properties");
            FileReader reader = new FileReader(configFile);
            configProps = new Properties();
            configProps.load(reader);
            reader.close();
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: No se encontro config.properties en: " + new File(".").getAbsolutePath());
        } catch (IOException ex) {
            System.err.println("ERROR: No se pudo leer config.properties");
        }
    }

    /**
     * Retorna el valor de una propiedad por su clave.
     *
     * @param key nombre de la propiedad (ej: "dburl", "dbuser")
     * @return String con el valor, o vacio si no existe
     */
    public String getPropValue(String key) {
        if (configProps != null && configProps.containsKey(key))
            return configProps.getProperty(key);
        return "";
    }
}
