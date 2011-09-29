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
import java.util.List;

import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.db.Loader;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Fetch;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * "Fetch" command implementation. Reads rows from database into external files
 * and/or rowset variable. 
 * 
 * @author vsi
 */
public class FetchCmd implements RuntimeCommand {
    private static final Logger logger = LoggerFactory.getLogger(FetchCmd.class);
        
    private String m_source;
    private JdbcConnection m_connection;
    private JdbcConnection m_destination;
    private Fetch.Type m_type;
    private boolean m_overwrite;
    private char m_delimiter;
    private char m_quote;
    private String m_target;
    private String m_rowset;
    private String m_encoding;

    public FetchCmd(String source, JdbcConnection connection, Fetch.Type type,
            boolean overwrite, char delimiter, char quote, String target,
            String rowset, String encoding, JdbcConnection destination) {
        m_source = source;
        m_connection = connection;
        m_type = type;
        m_overwrite = overwrite;
        m_delimiter = delimiter;
        m_quote = quote;
        m_target = target;
        m_rowset = rowset;
        m_encoding = encoding;
        m_destination = destination;
    }

    @Override
    public void run(Context context) throws Throwable {
        logger.info(String.format(
                "fetch: source='%s', connection='%s' target='%s', encoding='%s', " +
                "type='%s', rowset='%s', overwrite='%s', delimiter='%s', " +
                "quote='%s', destination='%s'",
                m_source, m_connection.getName(), m_target, m_encoding,
                m_type, m_rowset, m_overwrite, m_delimiter, m_quote,
                m_destination != null ? m_destination.getName() : ""));
        
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
                logger.info(String.format("%d row(s) fetched", rowCount));
            } finally {
                close(rs);
            }
        } finally {
            close(stmt);
        }
        
        if (rowsetRowHandler != null) {
            context.assignVariable(m_rowset, rowsetRowHandler.getRows());
        }
    }

    private RowHandler getRowHandlerForTarget() throws Exception {
        if (m_target == null) {
            return null;
        }
        
        if (m_destination != null) {
            return new TableRowHandler();
        }

        switch (m_type) {
        case CSV:
            return new CsvRowHandler();
        default:
            throw new XdtlException("Target type '" + m_type
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
    private static interface RowHandler {
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
            File f = new File(m_target);
            if (!m_overwrite && f.exists()) {
                throw new XdtlException("File '" + m_target + "' exists");
            }
            
            FileOutputStream os = new FileOutputStream(f, false);
            Writer writer = new BufferedWriter(new OutputStreamWriter(os, m_encoding));
            
            m_csvWriter = new CSVWriter(writer, m_delimiter, m_quote);
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
        private ArrayList<Object[]> m_rows = new ArrayList<Object[]>();
        
        @Override
        public void open(MyResultSet rs) throws Exception {
        }

        @Override
        public void write(MyResultSet rs) throws Exception {
            ResultSetMetaData metaData = rs.getMetaData();
            Object[] row = new Object[metaData.getColumnCount()];
            
            for (int i = 1; i <= row.length; i++) {
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
                
                row[i - 1] = obj;
            }
            
            m_rows.add(row);
        }

        @Override
        public void close() throws Exception {
        }
        
        public List<Object[]> getRows() {
            return m_rows;
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
            m_loader.loadRow(m_rowBuf);
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
        private static final Logger logger = LoggerFactory.getLogger(RowHandlerList.class);
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
