package org.mmx.xdtl.db;

import java.sql.BatchUpdateException;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.db.converter.DateConverter;
import org.mmx.xdtl.db.converter.DoubleConverter;
import org.mmx.xdtl.db.converter.IConverter;
import org.mmx.xdtl.db.converter.StringConverter;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.XdtlException;

public class Loader {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.rt.db.loader");
    private static final Logger rowLogger = XdtlLogger.getLogger("xdtl.rt.db.loader.rows");
    private static final int DEFAULT_BATCH_SIZE = 1000;

    private final JdbcConnection m_cnn;
    private final String m_table;
    private final ArrayList<Column> m_columns = new ArrayList<Column>();
    private final HashMap<String, Integer> m_columnMap = new HashMap<String, Integer>();
    private final int m_commitRowCount;
    private final int m_batchSize;
    private PreparedStatement m_statement;
    private HashMap<Class<?>, IConverter<?>> m_converters = new HashMap<Class<?>, IConverter<?>>();
    private int m_rowNum;
    private int m_lastCommit;
    private boolean m_initialAutoCommit;
    private Object[] m_tempRowBuffer;
    private IdentifierConverter m_identifierConverter;
    private boolean m_inFailedState;

    public Loader(JdbcConnection cnn, String table, int batchSize,
            int commitRowCount) throws SQLException {
        m_cnn = cnn;
        m_commitRowCount = commitRowCount;
        m_batchSize = batchSize > 0 ? batchSize : DEFAULT_BATCH_SIZE;

        DatabaseMetaData metaData = cnn.getMetaData();
        m_identifierConverter = new IdentifierConverter(metaData);
        m_table = m_identifierConverter.toDbIdentifier(table);

        init(metaData);

        m_initialAutoCommit = m_cnn.getAutoCommit();
        if (logger.isTraceEnabled()) {
            String msg = "ctor: batch size=%d, initial autocommit=%s, commitRowCount=%d";
            msg = String.format(msg, m_batchSize, m_initialAutoCommit ? "on"
                    : "off", m_commitRowCount);
            logger.trace(msg);
        }

        if (m_initialAutoCommit) {
            logger.trace("ctor: turning autocommit off");
            m_cnn.setAutoCommit(false);
        }
    }

    private void init(DatabaseMetaData metaData) throws SQLException {

        String[] arr = m_table.split("\\.");
        String schema = arr.length >= 2 ? arr[arr.length - 2] : null;
        String table = arr[arr.length - 1];

        ResultSet rs = metaData.getColumns(null, schema, table, null);
        StringBuilder sql = new StringBuilder();
        sql.append("insert into " + m_table + " values(");

        try {
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                m_columns.add(new Column(columnName, rs.getInt("DATA_TYPE"),
                        rs.getString("TYPE_NAME")));
                m_columnMap.put(columnName, m_columns.size() - 1);
                sql.append("?,");
            }
        } finally {
            close(rs);
        }

        if (m_columns.size() == 0) {
            throw new XdtlException("init: no columns found in table '" + m_table + "'");
        }

        sql.setLength(sql.length() - 1);
        sql.append(")");

        m_statement = m_cnn.prepareStatement(sql.toString());

        m_converters.put(Double.class, new DoubleConverter());
        m_converters.put(String.class, new StringConverter());
        m_converters.put(java.util.Date.class, new DateConverter());
    }

    public void close() throws SQLException {
        SQLException sqlException = null;

        if (!m_inFailedState && (m_rowNum % m_batchSize != 0)) {
            logger.trace("close: final batch");
            try {
                executeBatch(m_statement);
            } catch (SQLException e) {
                logger.trace("executeBatch failed: " +  e.getMessage());
                sqlException = e;
            }
        }

        if (m_inFailedState) {
            logger.trace("close: rollback");
            m_cnn.rollback();
        } else if (m_rowNum - m_lastCommit != 0) {
            logger.trace("close: final commit");
            m_cnn.commit();
        }

        try {
            m_statement.close();
        } catch (SQLException e) {
            logger.warn("Failed to close statement");
        }

        try {
            if (m_initialAutoCommit) {
                logger.trace("close: turning autocommit back on");
                m_cnn.setAutoCommit(m_initialAutoCommit);
            }
        } catch (SQLException e) {
            logger.warn("Failed to restore autocommit flag to '" + m_initialAutoCommit + "'");
        }

        if (sqlException != null) {
            throw sqlException;
        }
    }

    public void loadRow(Object[] values, List<String> columnNames) throws Exception {
        if (m_inFailedState) {
            return;
        }

        int count = m_columns.size();

        if (rowLogger.isTraceEnabled()) {
            StringBuilder msg = new StringBuilder();
            msg.append("row ").append(m_rowNum).append(": ");

            for (int i = 0; i < m_columns.size(); i++) {
                msg.append("\n");
                msg.append(m_columns.get(i).getName()).append(": ");
                Object value = i < values.length ? values[i] : "";
                msg.append(value);
            }
            rowLogger.trace(msg);
        }

        if (columnNames != null) {
            values = remapValues(values, columnNames);
        }

        for (int i = 0; i < count; i++) {
            Column col = m_columns.get(i);
            Object value = i < values.length ? values[i] : null;
            if (value == null) {
                m_statement.setNull(i + 1, col.getType());
            } else {
                m_statement.setObject(i + 1, convert(value, col));
            }
        }

        m_statement.addBatch();
        m_rowNum++;
        if (m_rowNum % m_batchSize == 0) {
            if (logger.isTraceEnabled()) {
                logger.trace("loadRow: executeBatch, rowNum=" + m_rowNum);
            }

            executeBatch(m_statement);

            if (m_commitRowCount != 0
                    && (m_rowNum - m_lastCommit >= m_commitRowCount)) {
                logger.trace("loadRow: commit");
                m_cnn.commit();
                m_lastCommit = m_rowNum;
            }
        }
    }

    private Object[] remapValues(Object[] values, List<String> columnNames) {
        Object[] result = getTempRowBuffer();

        int i = 0;
        for (String columnName: columnNames) {
            String dbColumnName = m_identifierConverter.toDbIdentifier(columnName);
            Integer columnIndex = m_columnMap.get(dbColumnName);
            if (columnIndex == null) {
                throw new XdtlException("Column not found: " + dbColumnName);
            }

            result[columnIndex.intValue()] = values[i++];
        }

        return result;
    }

    /**
     * Returns the number of rows loaded.
     * @return number of rows loaded.
     */
    public int getRowCount() {
        return m_rowNum;
    }

    private Object[] getTempRowBuffer() {
        if (m_tempRowBuffer == null) {
            m_tempRowBuffer = new Object[m_columns.size()];
        } else {
            Arrays.fill(m_tempRowBuffer, null);
        }

        return m_tempRowBuffer;
    }

    private void executeBatch(PreparedStatement stmt) throws SQLException {
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException e) {
            m_inFailedState = true;
            throw e.getNextException();
        } catch (SQLException e) {
            m_inFailedState = true;
            throw e;
        }
    }

    private Object convert(Object object, Column col) throws Exception {
        assert object != null;
        IConverter<Object> converter = getConverter(object);
        return (converter != null) ? converter.convert(object, col) : object;
    }

    @SuppressWarnings("unchecked")
    private IConverter<Object> getConverter(Object object) {
        return (IConverter<Object>) m_converters.get(object.getClass());
    }

    private void close(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            logger.warn("Failed to close resultset", e);
        }
    }
}
