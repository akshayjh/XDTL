package org.mmx.xdtl.runtime.command;

import java.sql.Statement;

import org.apache.log4j.Logger;
import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.runtime.Context;

public class PostgresqlReadCmd extends PostgresqlReadWriteCmd {
    private static final Logger logger = Logger.getLogger("xdtl.read");
    
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
                String sql = "truncate " + getTarget();
                if (logger.isDebugEnabled()) {
                    logger.debug(sql);
                }
                stmt.execute(sql);
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

    @Override
    protected void logCmdStart() {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("source=%s, type=%s, target=%s," +
                    " delimiter=%s, quote=%s, errors=%s, overwrite=%s," +
                    " encoding=%s, header=%s" +
                    getSource(), getType(), getTarget(), getDelimiter(), getQuote(),
                    m_errors, isOverwrite(), getEncoding(), getHeader()));
        } else {
            logger.info("target=" + getTarget());
        }
    }
}
