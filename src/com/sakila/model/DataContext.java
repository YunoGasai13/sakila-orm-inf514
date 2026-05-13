package com.sakila.model;

import com.sakila.util.PropertyFile;
import java.io.Closeable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Universidad Autonoma de Santo Domingo | Facultad de Ciencias
 * INF514 Z06 | ORM Sakila DB
 *
 * Clase abstracta DataContext - PADRE de todos los modelos ORM.
 * NOMBRE REQUERIDO POR EL PROFESOR: DataContext (no EntityModel).
 *
 * Es un "padre abstracto hibrido" porque:
 *   - Tiene metodos CONCRETOS (con implementacion real)
 *   - Los metodos son declarados FINAL: los hijos NO pueden hacer override
 *   - Es abstracto: no se puede instanciar directamente
 *
 * Encapsula TODA la conectividad con MySQL via JDBC.
 * Usa PreparedStatement para prevenir SQL Injection.
 *
 * Metodos de escritura (CRUD):
 *   dbPost()   = INSERT
 *   dbPut()    = UPDATE
 *   dbDelete() = soft delete (UPDATE active=0)
 *
 * Metodos de lectura (Find overloads):
 *   Find()                  = SELECT top 10 default
 *   Find(boolean)           = SELECT hasta 5000
 *   Find(Object pkVal)      = SELECT por PK
 *   Find(String search)     = SELECT LIKE texto
 *   Find(String, Object fk) = SELECT LIKE + FK filter
 *   Find(Date, Date)        = SELECT BETWEEN fechas
 *   Find(String, boolean)   = SELECT raw SQL libre
 *
 * @author [TU NOMBRE] | Matricula: [TU MATRICULA]
 * @version 1.0
 */
public abstract class DataContext implements Closeable {

    // ─── Configuracion del objeto/tabla ───────────────────────────────────────
    /** Nombre de la tabla en la DB */
    private final String _objectName;
    /** Columna llave primaria */
    private final String _pkColumn;
    /** Columna(s) para busqueda LIKE */
    private final String _searchExpr;
    /** Columna FK para filtros combinados */
    private final String _fkColumn;
    /** Columna(s) para ORDER BY */
    private final String _ordColumns;
    /** Columna de fecha para BETWEEN */
    private final String _dateColumn;
    /** Limite de registros default */
    private static final int REC_TOP = 10;

    // ─── Objetos JDBC ─────────────────────────────────────────────────────────
    private Connection          oConn            = null;
    private ResultSetMetaData   objectMeta       = null;
    private HashMap<String, Integer> colIndexes  = null;
    private PreparedStatement   prepDefault      = null;
    private PreparedStatement   prepByPK         = null;
    private PreparedStatement   prepBySearch     = null;
    private PreparedStatement   prepByDateRange  = null;
    private PreparedStatement   prepBySearchFK   = null;
    private PreparedStatement   prepMaxID        = null;
    private PreparedStatement   prepFull         = null;
    private Statement           rawStm           = null;
    private ArrayList<Statement> allStms         = null;

    /** Mensaje de la ultima operacion (error o exito) */
    protected String actionMessage;

    /** Patron SQL base reutilizable para todos los SELECT */
    private static final String SQL_BASE =
            "SELECT * FROM {obj} WHERE {filter} ORDER BY {order} LIMIT 0,{top}";

    // ─── Constructores ────────────────────────────────────────────────────────

    /**
     * Constructor basico: nombre de tabla, PK y columna de busqueda.
     * @param objName nombre de la tabla
     * @param pkCol   columna PK
     * @param search  columna(s) para busqueda LIKE
     */
    public DataContext(String objName, String pkCol, String search) {
        this(objName, pkCol, search, "", "2,1", "");
    }

    /**
     * Constructor completo: incluye FK, orden y columna de fecha.
     * @param objName nombre de la tabla
     * @param pkCol   columna PK
     * @param search  columna(s) busqueda
     * @param fkCol   columna FK
     * @param ordCol  columna(s) ORDER BY
     * @param dateCol columna de fecha para BETWEEN
     */
    public DataContext(String objName, String pkCol, String search,
                       String fkCol, String ordCol, String dateCol) {
        this._objectName = objName;
        this._pkColumn   = pkCol;
        this._searchExpr = search;
        this._fkColumn   = fkCol;
        this._ordColumns = ordCol.isEmpty() ? "2,1" : ordCol;
        this._dateColumn = dateCol;
        initConnection();
    }

    // ─── METODOS FINAL: los hijos NO pueden hacer override ───────────────────

    /**
     * Find 1: SELECT default (primeros 10 registros).
     * FINAL: el hijo no puede sobrescribir este comportamiento.
     *
     * @return ResultSet con los datos o null si hay error
     */
    protected final ResultSet Find() {
        try {
            return prepDefault.executeQuery();
        } catch (SQLException e) {
            actionMessage = "Find(): " + e.getMessage();
            return null;
        }
    }

    /**
     * Find 2: SELECT completo hasta 5000 registros.
     * FINAL: el hijo no puede sobrescribir este comportamiento.
     *
     * @param isFull true para activar modo completo
     * @return ResultSet con los datos
     */
    protected final ResultSet Find(boolean isFull) {
        try {
            return isFull ? prepFull.executeQuery() : Find();
        } catch (SQLException e) {
            actionMessage = "Find(full): " + e.getMessage();
            return null;
        }
    }

    /**
     * Find 3: SELECT por llave primaria.
     * FINAL: el hijo no puede sobrescribir este comportamiento.
     *
     * @param pkVal valor del PK a buscar
     * @return ResultSet con el registro encontrado
     */
    protected final ResultSet Find(Object pkVal) {
        try {
            prepByPK.setObject(1, pkVal);
            return prepByPK.executeQuery();
        } catch (SQLException e) {
            actionMessage = "Find(PK): " + e.getMessage();
            return null;
        }
    }

    /**
     * Find 4: SELECT por texto LIKE (busqueda parcial en columnas string).
     * FINAL: el hijo no puede sobrescribir este comportamiento.
     *
     * @param search texto a buscar (comodines % se agregan automaticamente)
     * @return ResultSet con los registros que contienen el texto
     */
    protected final ResultSet Find(String search) {
        try {
            prepBySearch.setString(1, search);
            return prepBySearch.executeQuery();
        } catch (SQLException e) {
            actionMessage = "Find(search): " + e.getMessage();
            return null;
        }
    }

    /**
     * Find 5: SELECT por texto LIKE + filtro de FK.
     * FINAL: el hijo no puede sobrescribir este comportamiento.
     *
     * @param search texto a buscar
     * @param fkVal  valor del FK para filtrar
     * @return ResultSet con los registros que coinciden
     */
    protected final ResultSet Find(String search, Object fkVal) {
        try {
            prepBySearchFK.setString(1, search);
            prepBySearchFK.setObject(2, fkVal);
            return prepBySearchFK.executeQuery();
        } catch (SQLException e) {
            actionMessage = "Find(search+FK): " + e.getMessage();
            return null;
        }
    }

    /**
     * Find 6: SELECT por rango de fechas BETWEEN.
     * FINAL: el hijo no puede sobrescribir este comportamiento.
     *
     * @param dateIn  fecha inicio del rango
     * @param dateOut fecha fin del rango
     * @return ResultSet con los registros en ese rango
     */
    protected final ResultSet Find(Date dateIn, Date dateOut) {
        try {
            prepByDateRange.setDate(1, new java.sql.Date(dateIn.getTime()));
            prepByDateRange.setDate(2, new java.sql.Date(dateOut.getTime()));
            return prepByDateRange.executeQuery();
        } catch (SQLException e) {
            actionMessage = "Find(dates): " + e.getMessage();
            return null;
        }
    }

    /**
     * Find 7: SELECT con SQL libre (raw query para reportes especiales).
     * FINAL: el hijo no puede sobrescribir este comportamiento.
     *
     * @param rawSql SQL completo a ejecutar
     * @param isRaw  debe ser true para confirmar uso raw
     * @return ResultSet con los resultados
     */
    public final ResultSet Find(String rawSql, boolean isRaw) {
        if (!isRaw) return null;
        try {
            return rawStm.executeQuery(rawSql);
        } catch (SQLException e) {
            actionMessage = "Find(raw): " + e.getMessage();
            return null;
        }
    }

    /**
     * dbPost: INSERT de un nuevo registro en la DB.
     * Recibe el HashMap col->valor del hijo via SerializerMap().
     * FINAL: el hijo no puede sobrescribir la logica de INSERT.
     *
     * @param insertData HashMap con columna -> valor
     * @return true si el INSERT fue exitoso
     */
    protected final boolean dbPost(HashMap<String, String> insertData) {
        try {
            rawStm.execute(buildInsertSQL(insertData));
            return true;
        } catch (SQLException e) {
            actionMessage = "dbPost(): " + e.getMessage();
            return false;
        }
    }

    /**
     * dbPut: UPDATE de un registro existente en la DB.
     * Recibe el HashMap col->valor del hijo via SerializerMap().
     * FINAL: el hijo no puede sobrescribir la logica de UPDATE.
     *
     * @param updateData HashMap con columna -> valor (debe incluir PK)
     * @return true si el UPDATE fue exitoso
     */
    protected final boolean dbPut(HashMap<String, String> updateData) {
        try {
            rawStm.execute(buildUpdateSQL(updateData));
            return true;
        } catch (SQLException e) {
            actionMessage = "dbPut(): " + e.getMessage();
            return false;
        }
    }

    /**
     * dbDelete: SOFT DELETE - marca el registro como inactivo.
     * En lugar de DELETE fisico, hace UPDATE active=0.
     * Preserva la integridad referencial (rentas, pagos siguen siendo validos).
     * FINAL: el hijo no puede sobrescribir la logica de soft delete.
     *
     * @param pkVal     valor del PK del registro a desactivar
     * @param activeCol nombre de la columna active en esa tabla
     * @return true si el soft delete fue exitoso
     */
    protected final boolean dbDelete(Object pkVal, String activeCol) {
        try {
            String sql = "UPDATE " + _objectName + " SET " + activeCol +
                         " = 0 WHERE " + _pkColumn + " = " + pkVal;
            rawStm.execute(sql);
            return true;
        } catch (SQLException e) {
            actionMessage = "dbDelete(): " + e.getMessage();
            return false;
        }
    }

    /**
     * Obtiene el valor maximo actual del PK.
     * Usado por los hijos en Post() para asignar el siguiente ID.
     * FINAL: el hijo no puede sobrescribir este calculo.
     *
     * @return long con MAX(PK), -1 si hay error
     */
    protected final long getMaxID() {
        try {
            ResultSet rmax = prepMaxID.executeQuery();
            rmax.next();
            long max = rmax.getLong(1);
            rmax.close();
            return max;
        } catch (SQLException e) {
            actionMessage = "getMaxID(): " + e.getMessage();
            return -1;
        }
    }

    // ─── Metodos privados internos ────────────────────────────────────────────

    /**
     * Inicializa la conexion a DB y prepara todos los PreparedStatements.
     * Se llama automaticamente desde el constructor.
     */
    @SuppressWarnings("deprecation")
    private void initConnection() {
        try {
            if (_objectName.isBlank() || _pkColumn.isBlank() || _searchExpr.isBlank())
                throw new Exception("objectName, pkColumn y searchExpr son requeridos.");

            PropertyFile cfg = new PropertyFile();
            String driver = cfg.getPropValue("dbdriver");
            String dbUrl  = cfg.getPropValue("dburl")
                          + "?user="     + cfg.getPropValue("dbuser")
                          + "&password=" + cfg.getPropValue("dbpassword");

            Class.forName(driver).newInstance();
            oConn  = DriverManager.getConnection(dbUrl);
            rawStm = oConn.createStatement();
            prepareStatements();

            // Obtener metadatos con PK imposible (-999) sin datos reales
            ResultSet metaRs = Find(Integer.valueOf(-999));
            if (metaRs != null) {
                objectMeta = metaRs.getMetaData();
                buildColumnIndex();
                metaRs.close();
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            actionMessage = "Driver error: " + e.getMessage();
        } catch (SQLException e) {
            actionMessage = "SQL error: " + e.getMessage();
        } catch (Exception e) {
            actionMessage = "Init error: " + e.getMessage();
        }
    }

    /**
     * Prepara todos los PreparedStatements reutilizables en base al SQL_BASE.
     * @throws SQLException si algun statement falla al prepararse
     */
    private void prepareStatements() throws SQLException {
        String base = SQL_BASE
                .replace("{obj}",   _objectName)
                .replace("{order}", _ordColumns)
                .replace("{top}",   String.valueOf(REC_TOP));

        prepDefault  = oConn.prepareStatement(base.replace("{filter}", "1=1"));
        prepByPK     = oConn.prepareStatement(base.replace("{filter}", _pkColumn + " = ?"));
        prepBySearch = oConn.prepareStatement(base.replace("{filter}",
                       _searchExpr + " LIKE CONCAT('%',?,'%')"));

        if (!_dateColumn.isBlank())
            prepByDateRange = oConn.prepareStatement(base.replace("{filter}",
                              _dateColumn + " BETWEEN ? AND ?"));

        if (!_fkColumn.isBlank())
            prepBySearchFK = oConn.prepareStatement(base.replace("{filter}",
                             _searchExpr + " LIKE CONCAT('%',?,'%') AND " + _fkColumn + " = ?"));

        prepMaxID = oConn.prepareStatement("SELECT MAX(" + _pkColumn + ") FROM " + _objectName);
        prepFull  = oConn.prepareStatement("SELECT * FROM " + _objectName + " LIMIT 0,5000");

        allStms = new ArrayList<>();
        allStms.add(prepDefault);  allStms.add(prepByPK);       allStms.add(prepBySearch);
        allStms.add(prepByDateRange); allStms.add(prepBySearchFK); allStms.add(prepMaxID);
        allStms.add(prepFull);     allStms.add(rawStm);
    }

    /**
     * Construye el mapa colIndexes: nombre_columna -> tipo_SQL.
     * @throws SQLException si los metadatos no estan disponibles
     */
    private void buildColumnIndex() throws SQLException {
        colIndexes = new HashMap<>();
        for (int i = 1; i <= objectMeta.getColumnCount(); i++)
            colIndexes.put(objectMeta.getColumnName(i), objectMeta.getColumnType(i));
    }

    /**
     * Construye el SQL UPDATE desde un HashMap col->valor.
     * @param data HashMap con los datos (debe incluir el PK)
     * @return String con el SQL UPDATE completo
     */
    private String buildUpdateSQL(HashMap<String, String> data) {
        StringBuilder sb = new StringBuilder("UPDATE " + _objectName + " SET ");
        char sep = ' ';
        for (Map.Entry<String, String> e : data.entrySet()) {
            if (!e.getKey().equalsIgnoreCase(_pkColumn)) {
                sb.append(sep).append(e.getKey())
                  .append("=").append(toSQLValue(e.getKey(), e.getValue()));
                sep = ',';
            }
        }
        sb.append(" WHERE ").append(_pkColumn)
          .append("=").append(toSQLValue(_pkColumn, data.get(_pkColumn)));
        return sb.toString();
    }

    /**
     * Construye el SQL INSERT desde un HashMap col->valor.
     * @param data HashMap con los datos del nuevo registro
     * @return String con el SQL INSERT completo
     */
    private String buildInsertSQL(HashMap<String, String> data) {
        StringBuilder cols = new StringBuilder("INSERT INTO " + _objectName + " (");
        StringBuilder vals = new StringBuilder(" VALUES (");
        char sep = ' ';
        for (Map.Entry<String, String> e : data.entrySet()) {
            cols.append(sep).append(e.getKey());
            vals.append(sep).append(toSQLValue(e.getKey(), e.getValue()));
            sep = ',';
        }
        return cols + ") " + vals + ")";
    }

    /**
     * Formatea un valor para SQL: numerico sin comillas, texto con comillas simples.
     * @param colName nombre de la columna para detectar el tipo
     * @param value   valor a formatear
     * @return String listo para insertar en SQL
     */
    private String toSQLValue(String colName, String value) {
        if (colIndexes == null || !colIndexes.containsKey(colName)) return "'" + value + "'";
        return isNumericType(colIndexes.get(colName)) ? value : "'" + value + "'";
    }

    /**
     * Determina si un tipo SQL es numerico.
     * @param sqlType constante de java.sql.Types
     * @return true si es numerico
     */
    private boolean isNumericType(int sqlType) {
        return switch (sqlType) {
            case Types.INTEGER, Types.BIGINT, Types.SMALLINT, Types.TINYINT,
                 Types.DECIMAL, Types.NUMERIC, Types.FLOAT, Types.DOUBLE,
                 Types.BIT, Types.BOOLEAN -> true;
            default -> false;
        };
    }

    // ─── Metodos publicos de soporte ──────────────────────────────────────────

    /**
     * Retorna el ultimo mensaje de error o resultado de operacion.
     * @return String con el mensaje
     */
    public final String getMessage() { return actionMessage; }

    /**
     * Cierra todos los statements y la conexion a la DB.
     * SIEMPRE llamar al terminar de usar el modelo.
     */
    @Override
    public final void close() {
        try {
            if (allStms != null)
                for (Statement s : allStms)
                    if (s != null && !s.isClosed()) s.close();
            if (oConn != null && !oConn.isClosed()) oConn.close();
        } catch (SQLException e) {
            actionMessage = "close(): " + e.getMessage();
        }
    }
}
