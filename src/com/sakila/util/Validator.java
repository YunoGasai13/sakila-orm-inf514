package com.sakila.util;

import java.util.regex.Pattern;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public class Validator {

    /** Email estandar: usuario@dominio.tld */
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** Telefono: 10 digitos con o sin parentesis y separadores. */
    private static final Pattern PHONE_REGEX =
            Pattern.compile("^(\\(?\\d{3}\\)?[\\s.-]?)?\\d{3}[\\s.-]?\\d{4}$");

    /** Fecha YYYY-MM-DD valida. */
    private static final Pattern DATE_REGEX =
            Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");

    /** Cedula RD: 000-0000000-0 */
    private static final Pattern CEDULA_REGEX =
            Pattern.compile("^\\d{3}-\\d{7}-\\d{1}$");

    /** SSN US: 000-00-0000 */
    private static final Pattern SSN_REGEX =
            Pattern.compile("^\\d{3}-\\d{2}-\\d{4}$");

    /** Codigo postal de 5 digitos. */
    private static final Pattern ZIPCODE_REGEX =
            Pattern.compile("^\\d{5}$");

    /** true si el email cumple el formato; usado en CustomerController antes de insertar. */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_REGEX.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_REGEX.matcher(phone).matches();
    }

    public static boolean isValidDate(String date) {
        return date != null && DATE_REGEX.matcher(date).matches();
    }

    public static boolean isValidCedula(String cedula) {
        return cedula != null && CEDULA_REGEX.matcher(cedula).matches();
    }

    public static boolean isValidSSN(String ssn) {
        return ssn != null && SSN_REGEX.matcher(ssn).matches();
    }

    public static boolean isValidZipCode(String zip) {
        return zip != null && ZIPCODE_REGEX.matcher(zip).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }
}
