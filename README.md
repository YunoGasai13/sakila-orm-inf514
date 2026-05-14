# Sakila ORM Data Manager

**Universidad Autonoma de Santo Domingo — Facultad de Ciencias**
**Escuela de Informatica | INF514 Z06 | Programacion II Java**

**Autor:** Ismailyn Reyes
**Matricula:** 100437845

---

## Descripcion

Sistema ORM/CRUD en consola Java sobre la base de datos `sakila` de MySQL (renta de peliculas). Implementa arquitectura por capas con padre abstracto, interface CRUD, gestion de FK por agregacion de objetos y soft delete.

### Funcionalidad
- CRUD para Actor, Film, Customer, Inventory, Rental, Payment
- FK por composicion/agregacion (`City{Country objCountry}`, `Film{Language objLanguage}`)
- Soft delete (`active=0` u otro campo equivalente)
- Export a CSV y JSON por cada entidad
- Reportes con totales, promedios y aging
- Validaciones con regex (email, telefono, fecha, cedula)

---

## Estructura

```
src/com/sakila/
├── data/               Entidades (mapean tablas de la DB)
│   ├── Entity.java     Padre abstracto
│   ├── Actor.java
│   ├── Film.java
│   ├── City.java
│   ├── Customer.java
│   ├── Inventory.java
│   ├── Rental.java
│   ├── Payment.java
│   ├── Country.java
│   ├── Language.java
│   └── Category.java
├── model/              Capa ORM
│   ├── DataContext.java   Padre abstracto hibrido (CRUD final)
│   ├── iDatapost.java     Interface CRUD
│   ├── ActorModel.java
│   ├── FilmModel.java
│   ├── CustomerModel.java
│   ├── RentalModel.java
│   ├── PaymentModel.java
│   ├── InventoryModel.java
│   └── LanguageModel.java
├── controllers/        MVC
│   ├── ActorController.java
│   ├── FilmController.java
│   ├── CustomerController.java
│   ├── InventoryController.java
│   └── RentalController.java
├── reports/
│   └── ReportEngine.java
├── util/
│   ├── PropertyFile.java
│   └── Validator.java
└── Main.java
config.properties
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
2. Editar `config.properties` con las credenciales locales:
   ```properties
   dburl=jdbc:mysql://localhost:3306/sakila
   dbuser=root
   dbpassword=TU_PASSWORD
   dbdriver=com.mysql.cj.jdbc.Driver
   ```
3. Agregar `mysql-connector-j-8.x.x.jar` al classpath del proyecto.
4. Marcar `src` como Sources Root en IntelliJ.
5. Ejecutar `com.sakila.Main`.

---

## Menu principal

```
╔══════════════════════════════════════════════════════╗
║        SAKILA ORM DATA MANAGER - INF514 Z06          ║
║        Universidad Autonoma de Santo Domingo          ║
╚══════════════════════════════════════════════════════╝

  1. Gestion de Actores
  2. Gestion de Peliculas (Film)
  3. Gestion de Clientes (Customer)
  4. Gestion de Inventario
  5. Gestion de Rentas
  6. Reportes y Estadisticas (CSV / JSON)
  7. Acerca del sistema
  0. Salir
```

---

## Reportes

1. Top 10 peliculas mas rentadas
2. Pagos/cobranzas totales por tienda
3. Aging: rentas pendientes de devolucion
4. Top actores por cantidad de peliculas
5. Rentas por ciudad y pais
6. Estadisticas globales
7-9. Export CSV + JSON de Actor, Film, Customer
