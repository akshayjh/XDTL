package org.mmx.xdtl.db;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.mmx.xdtl.db.converter.DateConverter;
import org.mmx.xdtl.db.converter.DoubleConverter;
import org.mmx.xdtl.db.converter.IConverter;
import org.mmx.xdtl.db.converter.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loader {
    private static final Logger logger = LoggerFactory.getLogger(Loader.class);
    private static final int DEFAULT_BATCH_SIZE = 1000;

    private final JdbcConnection m_cnn;
    private final String m_table;
    private final ArrayList<Column> m_columns = new ArrayList<Column>();
    private final int m_commitRowCount;
    private final int m_batchSize;
    private PreparedStatement m_statement;
    private HashMap<Class<?>, IConverter<?>> m_converters = new HashMap<Class<?>, IConverter<?>>();
    private int m_rowNum;
    private int m_lastCommit;
    private boolean m_initialAutoCommit;

    public Loader(JdbcConnection cnn, String table, int batchSize,
            int commitRowCount) throws SQLException {
        m_cnn = cnn;
        m_commitRowCount = commitRowCount;
        m_batchSize = batchSize > 0 ? batchSize : DEFAULT_BATCH_SIZE;

        DatabaseMetaData metaData = cnn.getMetaData();
        m_table = metaData.storesUpperCaseIdentifiers()
                ? table.toUpperCase()
                : table;
        init(metaData);

        m_initialAutoCommit = m_cnn.getAutoCommit();
        if (logger.isDebugEnabled()) {
            String msg = "batch size=%d, initial autocommit=%s, commitRowCount=%d";
            msg = String.format(msg, m_batchSize, m_initialAutoCommit ? "on"
                    : "off", m_commitRowCount);
            logger.debug("ctor: {}", msg);
        }

        if (m_initialAutoCommit) {
            logger.debug("ctor: turning autocommit off");
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
                m_columns.add(new Column(rs.getInt("DATA_TYPE"), rs
                        .getString("TYPE_NAME"), rs.getString("COLUMN_NAME")));
                // m_logger.debug(rs.getString("COLUMN_NAME") + " " +
                // rs.getInt("DATA_TYPE") + " " + rs.getString("TYPE_NAME"));
                sql.append("?,");
            }
        } finally {
            close(rs);
        }

        sql.setLength(sql.length() - 1);
        sql.append(")");

        m_statement = m_cnn.prepareStatement(sql.toString());

        m_converters.put(Double.class, new DoubleConverter());
        m_converters.put(String.class, new StringConverter());
        m_converters.put(java.util.Date.class, new DateConverter());
    }

    public void close() throws SQLException {
        if (m_rowNum % m_batchSize != 0) {
            logger.debug("close: final batch");
            m_statement.executeBatch();
        }

        if (m_rowNum - m_lastCommit != 0) {
            logger.debug("close: final commit");
            m_cnn.commit();
        }

        try {
            m_statement.close();
        } catch (SQLException e) {
            logger.warn("Failed to close statement");
        }

        try {
            if (m_initialAutoCommit) {
                logger.debug("close: turning autocommit back on");
                m_cnn.setAutoCommit(m_initialAutoCommit);
            }
        } catch (SQLException e) {
            logger.warn("Failed to restore autocommit flag to '{}'",
                    m_initialAutoCommit);
        }
    }

    public void loadRow(Object[] values) throws Exception {
        int count = m_columns.size();

        if (logger.isTraceEnabled()) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < m_columns.size(); i++) {
                buf.append("\n");
                buf.append(m_columns.get(i).getName()).append(": ");
                Object value = i < values.length ? values[i] : "";
                buf.append(value);
            }
            logger.trace("row {}: {}", m_rowNum, buf.toString());
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
            logger.debug("loadRow: executeBatch, rowNum={}", m_rowNum);
            m_statement.executeBatch();

            if (m_commitRowCount != 0
                    && (m_rowNum - m_lastCommit >= m_commitRowCount)) {
                logger.debug("loadRow: commit");
                m_cnn.commit();
                m_lastCommit = m_rowNum;
            }
        }
    }

    private Object convert(Object object, Column col) throws Exception {
        assert object != null;
        IConverter<Object> converter = getConverter(object);
        // m_logger.debug("About to convert column " + col.getName() +
        // " with value " + (object == null ? "null" : object.toString()));
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
