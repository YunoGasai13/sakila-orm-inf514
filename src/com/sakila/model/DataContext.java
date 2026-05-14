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
 * INF514 Z06 | Proyecto Final: ORM Data Manager - Sakila DB
 *
 * Padre abstracto hibrido con metodos concretos final. Encapsula la
 * conectividad JDBC y los queries base reutilizados por cada modelo.
 *
 * @author Ismailyn Reyes
 * Matricula: 100437845
 */
public abstract class DataContext implements Closeable {

    // Configuracion de la tabla, recibida via constructor
    private final String _objectName;
    private final String _pkColumn;
    private final String _searchExpr;
    private final String _fkColumn;
    private final String _ordColumns;
    private final String _dateColumn;
    private static final int REC_TOP = 10;

    // Conexion y statements reutilizables (uno por tipo de query)
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

    /** Ultimo mensaje de error o resultado de operacion. */
    protected String actionMessage;

    private static final String SQL_BASE =
            "SELECT * FROM {obj} WHERE {filter} ORDER BY {order} LIMIT 0,{top}";

    /** Constructor basico: tabla + PK + columna de busqueda. */
    public DataContext(String objName, String pkCol, String search) {
        this(objName, pkCol, search, "", "2,1", "");
    }

    /** Constructor completo: incluye FK, orden y columna de fecha. */
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

    // ── Find: overloads de lectura. Final para que no se puedan sobreescribir.

    /** Primeros 10 registros con orden default. */
    protected final ResultSet Find() {
        // Ejecuta el SELECT default ya preparado y devuelve el ResultSet
        try {
            return prepDefault.executeQuery();
        } catch (SQLException e) {
            actionMessage = "Find(): " + e.getMessage();
            return null;
        }
    }

    /** Modo full: hasta 5000 registros. */
    protected final ResultSet Find(boolean isFull) {
        try {
            return isFull ? prepFull.executeQuery() : Find();
        } catch (SQLException e) {
            actionMessage = "Find(full): " + e.getMessage();
            return null;
        }
    }

    /** Busqueda por PK exacta. */
    protected final ResultSet Find(Object pkVal) {
        // Inyecta el valor del PK en el statement parametrizado
        try {
            prepByPK.setObject(1, pkVal);
            return prepByPK.executeQuery();
        } catch (SQLException e) {
            actionMessage = "Find(PK): " + e.getMessage();
            return null;
        }
    }

    /** Busqueda LIKE en la columna de busqueda configurada. */
    protected final ResultSet Find(String search) {
        try {
            prepBySearch.setString(1, search);
            return prepBySearch.executeQuery();
        } catch (SQLException e) {
            actionMessage = "Find(search): " + e.getMessage();
            return null;
        }
    }

    /** LIKE + filtro por FK. */
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

    /** Rango de fechas BETWEEN. */
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

    /** SQL libre para reportes con JOIN. */
    public final ResultSet Find(String rawSql, boolean isRaw) {
        if (!isRaw) return null;
        try {
            return rawStm.executeQuery(rawSql);
        } catch (SQLException e) {
            actionMessage = "Find(raw): " + e.getMessage();
            return null;
        }
    }

    // ── Operaciones de escritura. Cada modelo arma el HashMap col->valor y delega aqui.

    /** INSERT con los campos del HashMap. */
    protected final boolean dbPost(HashMap<String, String> insertData) {
        // Arma el INSERT con los datos del modelo y lo ejecuta
        try {
            rawStm.execute(buildInsertSQL(insertData));
            return true;
        } catch (SQLException e) {
            actionMessage = "dbPost(): " + e.getMessage();
            return false;
        }
    }

    /** UPDATE usando el PK del HashMap. */
    protected final boolean dbPut(HashMap<String, String> updateData) {
        try {
            rawStm.execute(buildUpdateSQL(updateData));
            return true;
        } catch (SQLException e) {
            actionMessage = "dbPut(): " + e.getMessage();
            return false;
        }
    }

    /** Soft delete: marca el registro como inactivo en lugar de borrarlo. */
    protected final boolean dbDelete(Object pkVal, String activeCol) {
        // No hace DELETE fisico: cambia la columna active a 0 para preservar FKs
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

    /** MAX(PK) actual. Usado por Post() para asignar el siguiente ID. */
    protected final long getMaxID() {
        // Consulta el mayor PK de la tabla; el Post() le suma 1 para el nuevo registro
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

    // ── JDBC: conexion y preparacion de statements (privado interno)

    /** Abre conexion, prepara statements y carga metadatos de la tabla. */
    @SuppressWarnings("deprecation")
    private void initConnection() {
        try {
            // Valida que el hijo haya pasado los datos minimos
            if (_objectName.isBlank() || _pkColumn.isBlank() || _searchExpr.isBlank())
                throw new Exception("objectName, pkColumn y searchExpr son requeridos.");

            // Lee config.properties y arma la URL JDBC con usuario y password
            PropertyFile cfg = new PropertyFile();
            String driver = cfg.getPropValue("dbdriver");
            String dbUrl  = cfg.getPropValue("dburl")
                          + "?user="     + cfg.getPropValue("dbuser")
                          + "&password=" + cfg.getPropValue("dbpassword");

            // Carga el driver y abre la conexion
            Class.forName(driver).newInstance();
            oConn  = DriverManager.getConnection(dbUrl);
            rawStm = oConn.createStatement();
            prepareStatements();

            // PK imposible para leer metadatos sin traer registros reales
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

    /** Prepara los PreparedStatement reusando SQL_BASE con distintos filtros. */
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

    /** Mapa columna -> tipo SQL para decidir si va con comillas o no en INSERT/UPDATE. */
    private void buildColumnIndex() throws SQLException {
        colIndexes = new HashMap<>();
        for (int i = 1; i <= objectMeta.getColumnCount(); i++)
            colIndexes.put(objectMeta.getColumnName(i), objectMeta.getColumnType(i));
    }

    /** Arma UPDATE ... SET col=val WHERE pk=val. */
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

    /** Arma INSERT INTO tabla(cols) VALUES(vals). */
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

    /** Numerico va sin comillas, texto con comillas simples. */
    private String toSQLValue(String colName, String value) {
        if (colIndexes == null || !colIndexes.containsKey(colName)) return "'" + value + "'";
        return isNumericType(colIndexes.get(colName)) ? value : "'" + value + "'";
    }

    private boolean isNumericType(int sqlType) {
        return switch (sqlType) {
            case Types.INTEGER, Types.BIGINT, Types.SMALLINT, Types.TINYINT,
                 Types.DECIMAL, Types.NUMERIC, Types.FLOAT, Types.DOUBLE,
                 Types.BIT, Types.BOOLEAN -> true;
            default -> false;
        };
    }

    public final String getMessage() { return actionMessage; }

    /** Cierra statements y conexion. Se sobreescribe en modelos con auxiliares para cerrarlos tambien. */
    @Override
    public void close() {
        // Libera todos los statements preparados y la conexion JDBC
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
