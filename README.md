# 🎬 Sakila ORM Data Manager

**Universidad Autónoma de Santo Domingo — Facultad de Ciencias**  
**Escuela de Informática | INF514 Z06 | Programación II Java**

> Proyecto Final: ORM CRUD Manager sobre la base de datos **sakila** de MySQL.  
> Autor: **[TU NOMBRE]** | Matrícula: **[TU MATRICULA]**  
> Profesor: **Mae. Silverio Del Orbe**

---

## 📋 Descripción

Sistema de gestión de datos (ORM) para la base de datos **sakila** de MySQL — un modelo de renta de películas de cine. Implementa el patrón **ORM (Object-Relational Mapping)** con una arquitectura en capas usando **MVC (Model-View-Controller)** en consola Java.

### Funcionalidades principales
- CRUD completo para: Actor, Film, Customer, Inventory, Rental, Payment
- Patrón FK por **composición/agregación** de objetos (`City{Country objCountry}`, `Film{Language objLanguage}`)
- **Soft Delete**: marca registros como inactivos (`active=0`) en lugar de borrar físicamente
- Export a **CSV** y **JSON** por cada entidad
- Módulo de **reportes y estadísticas** con métricas de rentas, pagos y aging
- Validaciones con **expresiones regulares** (email, teléfono, fecha, cédula)

---

## 🏗️ Arquitectura del Proyecto

```
src/com/sakila/
├── data/               ← Entidades (mapean tablas DB)
│   ├── Entity.java     ← Padre abstracto de todas las entidades
│   ├── Actor.java
│   ├── Film.java       ← Film { Language objLanguage }
│   ├── City.java       ← City  { Country objCountry  }
│   ├── Customer.java   ← Soft delete via campo active
│   ├── Inventory.java  ← Inventory { Film objFilm }
│   ├── Rental.java     ← Rental { Customer + Inventory }
│   ├── Payment.java    ← Payment { Customer objCustomer }
│   ├── Country.java
│   ├── Language.java
│   ├── Category.java
│   └── ...
├── model/              ← ORM Layer
│   ├── DataContext.java    ← Padre abstracto híbrido (métodos FINAL)
│   ├── iDatapost.java      ← Interface CRUD: Post/Get/Put/Delete
│   ├── ActorModel.java     ← Hijo final de DataContext
│   ├── FilmModel.java
│   ├── CustomerModel.java
│   ├── RentalModel.java
│   ├── PaymentModel.java
│   ├── InventoryModel.java
│   └── LanguageModel.java
├── controllers/        ← MVC Controllers
│   ├── ActorController.java
│   ├── FilmController.java
│   ├── CustomerController.java
│   ├── InventoryController.java
│   └── RentalController.java
├── reports/
│   └── ReportEngine.java   ← Estadísticas + export CSV/JSON
├── util/
│   ├── PropertyFile.java   ← Lee config.properties
│   └── Validator.java      ← Regex: email, teléfono, fecha, cédula
└── Main.java               ← Punto de entrada (menú consola do-while)
config.properties           ← Credenciales DB (no subir al repo con password real)
```

---

## 🔑 Patrones de Diseño Implementados

### 1. Padre abstracto híbrido — `DataContext`
```java
// Los métodos son FINAL: los hijos NO pueden hacer override
public abstract class DataContext implements Closeable {
    protected final ResultSet Find() { ... }       // SELECT default 10
    protected final ResultSet Find(Object pkVal)   // SELECT por PK
    protected final boolean dbPost(HashMap data)   // INSERT
    protected final boolean dbPut(HashMap data)    // UPDATE
    protected final boolean dbDelete(Object pk...) // Soft delete active=0
}
```

### 2. Interface CRUD — `iDatapost`
```java
public interface iDatapost {
    void    Mapping(ResultSet rSet);        // ResultSet → ArrayList
    boolean Post(Entity odata);             // CREATE
    ArrayList<?> Get();                     // READ (varios overloads)
    boolean Put(Entity odata);              // UPDATE
    boolean Delete(Entity odata);           // SOFT DELETE
    String  Serializer();                   // → JSON
    String  SerializerCSV();                // → CSV
}
```

### 3. FK por Composición/Agregación
```java
// En lugar de: int countryId;
public final class City extends Entity {
    public Country objCountry;  // FK gestionada por objeto
}
// Acceso directo: city.objCountry.country

// En lugar de: int languageId;
public final class Film extends Entity {
    public Language objLanguage;
}
// Acceso directo: film.objLanguage.name
```

### 4. Soft Delete
```java
// Delete NO borra el registro → cambia active=0
public boolean Delete(Entity odata) {
    Customer c = (Customer) odata;
    return super.dbDelete(c.customerId, "active"); // UPDATE active=0
}
```

---

## 📊 Reportes disponibles

| # | Reporte |
|---|---------|
| 1 | Top 10 películas más rentadas |
| 2 | Pagos/cobranzas totales por tienda |
| 3 | Aging: rentas pendientes de devolución |
| 4 | Top actores por cantidad de películas |
| 5 | Rentas por ciudad y país |
| 6 | Estadísticas globales (totales, promedios) |
| 7-9 | Export CSV + JSON de Actor, Film, Customer |

---

## ⚙️ Requisitos

| Tecnología | Versión |
|------------|---------|
| Java | 17+ |
| MySQL | 8.x |
| MySQL Workbench | 8.x |
| MySQL Connector/J | 8.x (JDBC driver) |

---

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/YunoGasai13/sakila-orm-inf514
cd sakila-orm-inf514
```

### 2. Configurar la base de datos
Verifica que sakila existe en MySQL Workbench. Si no:
- Descarga el script: https://dev.mysql.com/doc/index-other.html
- Ejecuta `sakila-schema.sql` y luego `sakila-data.sql` en Workbench

### 3. Configurar credenciales
Edita `config.properties`:
```properties
dburl=jdbc:mysql://localhost:3306/sakila
dbuser=root
dbpassword=TU_PASSWORD_AQUI
dbdriver=com.mysql.cj.jdbc.Driver
```
> ⚠️ **Nunca subas tu password real al repositorio.** Usa `.gitignore` para excluir `config.properties` o reemplaza el password antes de hacer commit.

### 4. Agregar el JDBC Driver en IntelliJ
- **File → Project Structure → Libraries → + → Java**
- Selecciona `mysql-connector-j-8.x.x.jar`
- O usa Maven agregando la dependencia en `pom.xml`:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.3.0</version>
</dependency>
```

### 5. Configurar Source Root en IntelliJ
- Click derecho en carpeta `src` → **Mark Directory as → Sources Root**
- Run → Edit Configurations → Working Directory = raíz del proyecto

### 6. Ejecutar
- Clase principal: `com.sakila.Main`
- Run → Run 'Main'

---

## 🖥️ Demostración del menú

```
╔══════════════════════════════════════════════════════╗
║        SAKILA ORM DATA MANAGER - INF514 Z06          ║
║        Universidad Autonoma de Santo Domingo          ║
╚══════════════════════════════════════════════════════╝

╔══════════════════════════════════════════════════════╗
║                  MENU PRINCIPAL                      ║
╠══════════════════════════════════════════════════════╣
║  1. Gestion de Actores                               ║
║  2. Gestion de Peliculas (Film)                      ║
║  3. Gestion de Clientes (Customer)                   ║
║  4. Gestion de Inventario                            ║
║  5. Gestion de Rentas                                ║
║  6. Reportes y Estadisticas (CSV / JSON)             ║
║  0. Salir                                            ║
╚══════════════════════════════════════════════════════╝
```

---

## 📁 Colecciones utilizadas

- `ArrayList<T>` — lista principal de cada modelo
- `HashMap<String, String>` — serialización para INSERT/UPDATE dinámico
- `HashMap<String, Integer>` — índice de tipos de columna para SQL
- `Java Streams + Lambdas` — búsqueda en memoria (`inMemSearch`)

---

## ✅ Normas generales cumplidas

- [x] Interface `iDatapost` (estándar CRUD)
- [x] Padre abstracto `DataContext` con métodos `final`
- [x] Hijos `final` concretos por cada tabla
- [x] FK por composición/agregación de objetos
- [x] Soft delete (`active=0`)
- [x] Javadoc en todas las clases y métodos
- [x] Package `com.sakila` particionado en `data`, `model`, `controllers`, `reports`, `util`
- [x] `ArrayList`, `HashMap`, `Streams` para estadísticas y búsquedas
- [x] Expresiones regulares (`Validator.java`)
- [x] Wildcards `<?>` en la interface
- [x] Export CSV y JSON
- [x] Reportes: Total, Promedio, Aging

---

## 📧 Contacto

**Entregado a:** silverio.delorbe@gmail.com  
**Repositorio:** https://github.com/YunoGasai13/sakila-orm-inf514

---

*INF514 Z06 — © 2021 Universidad Autónoma de Santo Domingo*
