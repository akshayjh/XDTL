package org.mmx.xdtl.runtime.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.db.Loader;
import org.mmx.xdtl.db.RowSet;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Fetch;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

import com.opencsv.CSVWriter;
/**
 * "Fetch" command implementation. Reads rows from database into external files
 * and/or rowset variable.
 *
 * @author vsi
 */
public class FetchCmd implements RuntimeCommand {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.fetch");

    private String m_source;
    private JdbcConnection m_connection;
    private JdbcConnection m_destination;
    private boolean m_overwrite;
    private String m_target;
    private String m_rowset;
    private RtTextFileProperties<Fetch.Type> m_textFileProperties;
    private boolean m_header;

    public FetchCmd(String source, JdbcConnection connection, boolean overwrite,
            RtTextFileProperties<Fetch.Type> textFileProperties, boolean header,
            String target, String rowset, JdbcConnection destination) {
        m_source = source;
        m_connection = connection;
        m_overwrite = overwrite;
        m_textFileProperties = textFileProperties;
        m_header = header;
        m_target = target;
        m_rowset = rowset;
        m_destination = destination;
    }

    @Override
    public void run(Context context) throws Throwable {
        logCmdStart();

        RowsetRowHandler rowsetRowHandler = null;
        Statement stmt = m_connection.createStatement();

        try {
            MyResultSet rs = new MyResultSet(stmt.executeQuery(m_source));

            try {
                RowHandlerList rowHandlers = new RowHandlerList();
                RowHandler rowHandler = getRowHandlerForTarget();
                if (rowHandler != null) {
                    rowHandlers.add(rowHandler);
                }

                if (m_rowset != null) {
                    rowsetRowHandler = new RowsetRowHandler();
                    rowHandlers.add(rowsetRowHandler);
                }

                rowHandlers.open(rs);

                int rowCount = 0;
                while (rs.next()) {
                    rowHandlers.write(rs);
                    rowCount++;
                }

                rowHandlers.close();
                logCmdEnd(rowCount);
            } finally {
                close(rs);
            }
        } finally {
            close(stmt);
        }

        if (rowsetRowHandler != null) {
            context.assignVariable(m_rowset, rowsetRowHandler.getRowset());
        }
    }

    private void logCmdStart() {
        if (logger.isDebugEnabled()) {
            if (m_target != null) {
                if (m_destination != null) {
                    logger.debug(String.format("source=%s, target=%s", m_source,
                            m_target));
                } else {
                    logger.debug(String.format("source=%s, target=%s," +
                    		" overwrite=%s, %s",  m_source, m_target,
                    		m_overwrite, m_textFileProperties));
                }
            } else {
                logger.debug(String.format("source=%s", m_source));
            }
        } else {
            if (m_target != null) {
                if (m_destination != null) {
                    logger.info(String.format("target=%s", m_target));
                }
            }
        }
    }

    private void logCmdEnd(int rowCount) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("%d row(s) fetched", rowCount));
        } else if (m_rowset == null){
            logger.info(String.format("%d row(s) fetched", rowCount));
        }
    }

    private RowHandler getRowHandlerForTarget() throws Exception {
        if (m_target == null) {
            return null;
        }

        if (m_destination != null) {
            return new TableRowHandler();
        }

        switch (m_textFileProperties.getType()) {
        case CSV:
            return new CsvRowHandler();
        default:
            throw new XdtlException("Target type '" + m_textFileProperties.getType()
                    + "' not implemented");
        }
    }

    private void close(MyResultSet rs) {
        try {
            rs.close();
        } catch (Throwable t) {
            logger.warn("Failed to close resultset", t);
        }
    }

    private void close(Statement stmt) {
        try {
            stmt.close();
        } catch (Throwable t) {
            logger.warn("Failed to close statement", t);
        }
    }
    /**
     * An interface for objects which perform some work on each row in
     * resulset.
     *
     * @author vsi
     */
    private interface RowHandler {
        void open(MyResultSet rs) throws Exception;
        void write(MyResultSet rs) throws Exception;
        void close() throws Exception;
    }

    /**
     * Row handler to copy rows from resultset to CSV file.
     *
     * @author vsi
     */
    private class CsvRowHandler implements RowHandler {
        private CSVWriter m_csvWriter;

        @Override
        public void open(MyResultSet rs) throws Exception {
        	if (m_target.startsWith("file:"))
        		m_target = m_target.substring(5);

        	if (m_target.startsWith("//"))
        		m_target = m_target.substring(2);

            File f = new File(m_target);

            FileOutputStream os = new FileOutputStream(f, !m_overwrite);
            Writer writer = new BufferedWriter(new OutputStreamWriter(os, m_textFileProperties.getEncoding()));

            m_csvWriter = new CSVWriter(writer,
                    m_textFileProperties.getDelimiter(),
                    m_textFileProperties.getQuote(),
                    m_textFileProperties.getEscape());

            if (m_header) {
                m_csvWriter.writeNext(rs.getColumnNames());
            }
        }

        @Override
        public void write(MyResultSet rs) throws SQLException {
            m_csvWriter.writeNext(rs.getColumnsAsStrings());
        }

        @Override
        public void close() throws IOException {
            m_csvWriter.close();
        }
    }

    /**
     * Row handler to add rows from resultset to a list.
     *
     * @author vsi
     */
    private class RowsetRowHandler implements RowHandler {
        private RowSet m_rowset;

        @Override
        public void open(MyResultSet rs) throws Exception {
            m_rowset = new RowSet(m_connection.getMetaData(), rs.getMetaData());
        }

        @Override
        public void write(MyResultSet rs) throws Exception {
            ResultSetMetaData metaData = rs.getMetaData();
            RowSet.Row row = m_rowset.newRow();
            int rowSize = row.size();

            for (int i = 1; i <= rowSize; i++) {
                Object obj = rs.getObject(i);

                switch (metaData.getColumnType(i)) {
                case Types.CLOB:
                case Types.NCLOB:
                    obj = rs.getObjectAsString(i);
                    break;
                case Types.BLOB:
                    Blob blob = (Blob) obj;
                    obj = blob.getBytes(0, (int) blob.length());
                }

                row.set(i - 1, obj);
            }

            m_rowset.add(row);
        }

        @Override
        public void close() throws Exception {
        }

        public RowSet getRowset() {
            return m_rowset;
        }
    }

    private class TableRowHandler implements RowHandler {
        private Loader m_loader;
        private Object[] m_rowBuf;

        @Override
        public void open(MyResultSet rs) throws Exception {
            m_loader = new Loader(m_destination, m_target, 0, 0);
            m_rowBuf = new Object[rs.getMetaData().getColumnCount()];
        }

        @Override
        public void write(MyResultSet rs) throws Exception {
            rs.toArray(m_rowBuf);
            m_loader.loadRow(m_rowBuf, null);
        }

        @Override
        public void close() throws Exception {
            m_loader.close();
        }
    }

    /**
     * A list of row handlers.
     *
     * @author vsi
     */
    private static class RowHandlerList {
    	private static final Logger logger = XdtlLogger.getLogger(RowHandlerList.class);
        private ArrayList<RowHandler> m_list = new ArrayList<RowHandler>(3);

        public void add(RowHandler rowHandler) {
            m_list.add(rowHandler);
        }

        public void open(MyResultSet rs) throws Exception {
            for (RowHandler handler: m_list) {
                handler.open(rs);
            }
        }

        public void write(MyResultSet rs) throws Exception {
            for (RowHandler handler: m_list) {
                handler.write(rs);
            }
        }

        public void close() {
            for (RowHandler handler: m_list) {
                try {
                    handler.close();
                } catch (Exception e) {
                    logger.warn("Failed to close handler", e);
                }
            }
        }
    }

    private static class MyResultSet {
        private final ResultSet m_resultSet;
        private final String[]  m_columns;

        public MyResultSet(ResultSet resultSet) throws SQLException {
            m_resultSet = resultSet;
            m_columns = new String[resultSet.getMetaData().getColumnCount()];
        }

        public String[] getColumnNames() throws SQLException {
            ResultSetMetaData md = m_resultSet.getMetaData();
            int columnCount = md.getColumnCount();
            String[] result = new String[columnCount];

            for (int i = 1; i <= columnCount; i++) {
                result[i - 1] = md.getColumnName(i);
            }

            return result;
        }

        public Object getObject(int index) throws SQLException {
            return m_resultSet.getObject(index);
        }

        public String getObjectAsString(int index) throws SQLException {
            int arrIndex = index - 1;
            if (m_columns[arrIndex] == null) {
                m_columns[arrIndex] = asString(index);
            }

            return m_columns[arrIndex];
        }

        public String[] getColumnsAsStrings() throws SQLException {
            for (int i = 1; i <= m_columns.length; i++) {
                getObjectAsString(i);
            }

            return m_columns;
        }

        public void toArray(Object[] arr) throws Exception {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = m_resultSet.getObject(i + 1);
            }
        }

        public boolean next() throws SQLException {
            Arrays.fill(m_columns, null);
            return m_resultSet.next();
        }

        private String asString(int index) throws SQLException {
            Object o = m_resultSet.getObject(index);
            if (o == null) return null;

            int type = m_resultSet.getMetaData().getColumnType(index);

            switch (type) {
            case Types.CLOB:
            case Types.NCLOB:
                Clob clob = (Clob) o;
                String str = clob.getSubString(1, (int) clob.length());
                clob.free();
                return str;
            case Types.BLOB:
                throw new XdtlException("Blobs are not supported");
            }

            return o.toString();
        }

        public ResultSetMetaData getMetaData() throws SQLException {
            return m_resultSet.getMetaData();
        }

        public void close() throws SQLException {
            m_resultSet.close();
        }
    }
}
