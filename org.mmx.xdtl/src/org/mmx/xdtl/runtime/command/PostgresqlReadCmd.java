package org.mmx.xdtl.runtime.command;

import java.sql.Statement;

import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.runtime.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresqlReadCmd extends PostgresqlReadWriteCmd {
    private final Logger m_logger = LoggerFactory.getLogger(PostgresqlReadCmd.class);
    
    private final String m_errors;

    public PostgresqlReadCmd(Object source, String target, String type,
            boolean overwrite, String delimiter, String quote, String encoding,
            Connection cnn, String errors, boolean header, int rowOffset, int batch) {

        super((String) source, target, type, overwrite, delimiter, quote, encoding, cnn, header, true);
        m_errors = errors;        
    }

    @Override
    public void run(Context context) throws Throwable {
        JdbcConnection cnn = context.getConnectionManager().getJdbcConnection(getConnection());
        if (isOverwrite()) {           
            Statement stmt = cnn.createStatement();
            try {
                m_logger.info("Truncating table '{}'", getTarget());
                stmt.execute("truncate " + getTarget());
            } finally {
                close(stmt);
            }
        }
    
        super.run(context);
    }

    @Override
    protected String createSql() {
        String sql = super.createSql();
        if (m_errors != null && m_errors.length() > 0) {
            sql = sql + " log errors into " + m_errors; 
        }
        
        return sql;
    }
}
