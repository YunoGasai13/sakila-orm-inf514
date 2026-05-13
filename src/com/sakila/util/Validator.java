package com.sakila.util;

import java.util.regex.Pattern;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Clase utilitaria Validator - expresiones regulares para validacion de datos.
 * Cubre: email, telefono, fecha, cedula/SSN, codigo postal.
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public class Validator {

    /** Regex para email estandar */
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** Regex para telefono: acepta formatos (809)555-1234 o 8095551234 */
    private static final Pattern PHONE_REGEX =
            Pattern.compile("^(\\(?\\d{3}\\)?[\\s.-]?)?\\d{3}[\\s.-]?\\d{4}$");

    /** Regex para fecha formato YYYY-MM-DD */
    private static final Pattern DATE_REGEX =
            Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");

    /** Regex para cedula dominicana: 000-0000000-0 */
    private static final Pattern CEDULA_REGEX =
            Pattern.compile("^\\d{3}-\\d{7}-\\d{1}$");

    /** Regex para SSN USA: 000-00-0000 */
    private static final Pattern SSN_REGEX =
            Pattern.compile("^\\d{3}-\\d{2}-\\d{4}$");

    /** Regex para codigo postal de 5 digitos */
    private static final Pattern ZIPCODE_REGEX =
            Pattern.compile("^\\d{5}$");

    /**
     * Valida un correo electronico.
     * @param email correo a validar
     * @return true si el formato es valido
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_REGEX.matcher(email).matches();
    }

    /**
     * Valida un numero de telefono.
     * @param phone telefono a validar
     * @return true si el formato es valido
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_REGEX.matcher(phone).matches();
    }

    /**
     * Valida una fecha en formato YYYY-MM-DD.
     * @param date fecha a validar
     * @return true si el formato es valido
     */
    public static boolean isValidDate(String date) {
        return date != null && DATE_REGEX.matcher(date).matches();
    }

    /**
     * Valida una cedula dominicana con formato 000-0000000-0.
     * @param cedula cedula a validar
     * @return true si el formato es valido
     */
    public static boolean isValidCedula(String cedula) {
        return cedula != null && CEDULA_REGEX.matcher(cedula).matches();
    }

    /**
     * Valida un SSN de USA con formato 000-00-0000.
     * @param ssn SSN a validar
     * @return true si el formato es valido
     */
    public static boolean isValidSSN(String ssn) {
        return ssn != null && SSN_REGEX.matcher(ssn).matches();
    }

    /**
     * Valida un codigo postal de 5 digitos.
     * @param zip codigo postal a validar
     * @return true si el formato es valido
     */
    public static boolean isValidZipCode(String zip) {
        return zip != null && ZIPCODE_REGEX.matcher(zip).matches();
    }

    /**
     * Verifica que un String no sea nulo ni vacio.
     * @param value valor a verificar
     * @return true si tiene contenido
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }
}
