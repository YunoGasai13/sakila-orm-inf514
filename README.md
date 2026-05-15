# Sakila ORM Data Manager

**Universidad Autonoma de Santo Domingo — Facultad de Ciencias**
**Escuela de Informatica | INF514 Z06 | Programacion II Java**

**Autor:** Ismailyn Reyes
**Matricula:** 100437845

---

## Descripcion

Sistema ORM/CRUD en consola Java sobre la base de datos `sakila` de MySQL (renta de peliculas). Implementa arquitectura por capas con padre abstracto, interface CRUD, gestion de FK por agregacion de objetos y soft delete.

### Funcionalidad
- CRUD para Actor, Film, Customer, Inventory, Rental, Payment, Store, Staff, Address
- FK por composicion/agregacion (ej: `Film.objLanguage`, `City.objCountry`, `Customer.objStore`, `Rental.objStaff`)
- Soft delete (`active=0` u otro campo equivalente segun la tabla)
- Export a CSV y JSON por cada entidad
- Reportes con totales, promedios y aging
- Validaciones con regex (email, telefono, fecha, cedula)

---

## Estructura

```
src/com/sakila/
├── data/                  Entidades (mapean tablas de la DB)
│   ├── Entity.java        Padre abstracto
│   ├── Actor.java
│   ├── Film.java
│   ├── Customer.java
│   ├── Inventory.java
│   ├── Rental.java
│   ├── Payment.java
│   ├── Address.java
│   ├── Staff.java
│   ├── Store.java
│   ├── City.java
│   ├── Country.java
│   ├── Language.java
│   └── Category.java
├── model/                 Capa ORM
│   ├── DataContext.java   Padre abstracto hibrido (CRUD final)
│   ├── iDatapost.java     Interface CRUD
│   ├── ActorModel.java
│   ├── FilmModel.java
│   ├── CustomerModel.java
│   ├── InventoryModel.java
│   ├── RentalModel.java
│   ├── PaymentModel.java
│   ├── AddressModel.java
│   ├── StaffModel.java
│   ├── StoreModel.java
│   └── LanguageModel.java
├── controllers/           MVC
│   ├── ActorController.java
│   ├── FilmController.java
│   ├── CustomerController.java
│   ├── InventoryController.java
│   ├── RentalController.java
│   ├── PaymentController.java
│   ├── StoreController.java
│   ├── StaffController.java
│   └── AddressController.java
├── reports/
│   └── ReportEngine.java
├── util/
│   ├── PropertyFile.java
│   └── Validator.java
└── Main.java
config.properties           (no se sube al repo, esta en .gitignore)
```

---

## Requisitos

| Tecnologia        | Version |
|-------------------|---------|
| Java              | 17+     |
| MySQL             | 8.x     |
| MySQL Workbench   | 8.x     |
| MySQL Connector/J | 8.x     |

---

## Configuracion

1. Verificar que la base `sakila` esta instalada en MySQL.
2. Copiar `config.properties.example` a `config.properties` y reemplazar `TU_PASSWORD` con la password real de tu MySQL:
   ```bash
   cp config.properties.example config.properties
   ```
3. Marcar `src` como Sources Root en IntelliJ.
4. Agregar el driver al classpath (ver seccion siguiente).
5. Ejecutar `com.sakila.Main`.

---

## Dependencias incluidas

El MySQL Connector/J ya viene en la carpeta `lib/`. Solo agrega el JAR al classpath del proyecto en IntelliJ:

```
File → Project Structure → Libraries → + → Java
   → seleccionar lib/mysql-connector-j-8.x.x.jar
```

---

## Ejecutar desde el JAR ya compilado

Para correr sin compilar, desde la raiz del proyecto:

```bash
java -jar sakila-orm-inf514.jar
```

Necesita que en la misma carpeta esten: `sakila-orm-inf514.jar`, `lib/mysql-connector-j-8.x.x.jar` y `config.properties`.

---

## Menu principal

```
╔══════════════════════════════════════════════════════╗
║        SAKILA ORM DATA MANAGER - INF514 Z06          ║
║        Universidad Autonoma de Santo Domingo          ║
║        Facultad de Ciencias - Escuela Informatica     ║
╚══════════════════════════════════════════════════════╝

╔══════════════════════════════════════════════════════╗
║                  MENU PRINCIPAL                      ║
╠══════════════════════════════════════════════════════╣
║   1. Gestion de Actores                              ║
║   2. Gestion de Peliculas (Film)                     ║
║   3. Gestion de Clientes (Customer)                  ║
║   4. Gestion de Inventario                           ║
║   5. Gestion de Rentas                               ║
║   6. Gestion de Pagos                                ║
║   7. Gestion de Tiendas (Store)                      ║
║   8. Gestion de Personal (Staff)                     ║
║   9. Gestion de Direcciones (Address)                ║
║  10. Reportes y Estadisticas (CSV / JSON)            ║
║  11. Acerca del sistema                              ║
║   0. Salir                                           ║
╚══════════════════════════════════════════════════════╝
```

---

## Reportes (opcion 10 del menu)

1. Top 10 peliculas mas rentadas
2. Pagos/cobranzas totales por tienda
3. Aging: rentas pendientes de devolucion
4. Top actores por cantidad de peliculas
5. Rentas por ciudad y pais
6. Estadisticas globales (totales, promedios)
7-9. Export CSV + JSON de Actor, Film, Customer
