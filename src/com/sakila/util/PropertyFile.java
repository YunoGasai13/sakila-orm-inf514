package com.sakila.util;

import java.io.*;
import java.util.Properties;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public class PropertyFile {

    private Properties configProps = null;

    /** Carga config.properties desde la raiz del proyecto. */
    public PropertyFile() {
        // Lee el archivo una sola vez al crear el objeto, luego cada getPropValue() trabaja en memoria
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

    /** Retorna el valor de una propiedad por clave; vacio si no existe. */
    public String getPropValue(String key) {
        if (configProps != null && configProps.containsKey(key))
            return configProps.getProperty(key);
        return "";
    }
}
