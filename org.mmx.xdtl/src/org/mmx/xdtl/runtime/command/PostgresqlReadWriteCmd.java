package org.mmx.xdtl.runtime.command;

import java.sql.Statement;

import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresqlReadWriteCmd implements RuntimeCommand {
    private final Logger m_logger = LoggerFactory.getLogger(PostgresqlReadWriteCmd.class);
    
    private final String m_source;
    private final String m_target;
    private final String m_type;
    private final String m_delimiter;
    private final String m_quote;
    private final boolean m_read;
    private final boolean m_overwrite;
    private final Connection m_connection;
    private final String m_encoding;
    
    public PostgresqlReadWriteCmd(String source, String target, String type,
            boolean overwrite, String delimiter, String quote, String encoding,
            Connection cnn, boolean read) {

        m_source = source;
        m_target = target;
        m_type = type;
        m_delimiter = delimiter;
        m_quote = quote;
        m_encoding = encoding;
        m_overwrite = overwrite;
        m_connection = cnn;
        m_read = read;
        
        if (!(m_type.equals("CSV") || m_type.equals("FIXED"))) {
            throw new XdtlException("Invalid type: '" + m_type + "', only CSV " +
                    "and FIXED are supported");
        }
    }

    @Override
    public void run(Context context) throws Throwable {
        JdbcConnection cnn = context.getConnectionManager().getJdbcConnection(m_connection);
        String sql = createSql();
        Statement stmt = cnn.createStatement();
        try {
            m_logger.info("COPY: sql={}", sql);
            stmt.execute(sql);
        } finally {
            close(stmt);
        }
    }

    protected String createSql() {
        String str;
        
        if (m_read) {
            str = "COPY " + m_target + " FROM '" + m_source + "'";
        } else {
            str = "COPY " + m_source + " TO '" + m_target + "'";
        }
        
        StringBuilder options = new StringBuilder();
        if (m_delimiter.length() != 0) {
            options.append(" DELIMITER '").append(m_delimiter).append('\'');
        }
        
        if (m_type.equals("CSV")) {
            options.append(" CSV");
            
            if (m_quote.length() != 0) {
                options.append(" QUOTE '").append(m_quote).append('\'');
            }
        }

        if (options.length() > 0) {
            str = str + " WITH" + options.toString();
        }
        
        return str;
    }
    
    protected void close(Statement stmt) {
        try {
            stmt.close();
        } catch (Throwable t) {
            m_logger.warn("Failed to close statement", t);
        }
    }

    protected String getSource() {
        return m_source;
    }

    protected String getTarget() {
        return m_target;
    }

    protected String getType() {
        return m_type;
    }

    protected String getDelimiter() {
        return m_delimiter;
    }

    protected String getQuote() {
        return m_quote;
    }

    protected boolean isOverwrite() {
        return m_overwrite;
    }

    protected Connection getConnection() {
        return m_connection;
    }

    protected String getEncoding() {
        return m_encoding;
    }
}
