package org.mmx.xdtl.runtime.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.util.StringShortener;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class QueryCmd implements RuntimeCommand {
    private static final Logger logger = Logger.getLogger("xdtl.cmd.query");

    private final JdbcConnection m_connection;
    private final String m_sqlStatement;
    private final String m_target;
    private final List<Object> m_params;
    private StringShortener m_sqlShortener;
    
    public QueryCmd(JdbcConnection connection, String sqlStatement, String target, List<Object> params) {
        m_connection = connection;
        m_sqlStatement = sqlStatement;
        m_target = target;
        m_params = params;
    }
    
    @Override
    public void run(Context context) throws SQLException {
        Level logLevel = Level.DEBUG;
        String logSql = m_sqlStatement;
        
        if (!logger.isDebugEnabled()) {
            logLevel = Level.INFO;
            logSql = m_sqlShortener.shorten(m_sqlStatement);
        }

        logger.log(logLevel, "sql=" + logSql);

        Object result;
        String resultName;

        if (m_target != null && m_target.length() > 0) {
            result = executeStatementReturnScalar();
            resultName = "result";
            context.assignVariable(m_target, result);
        } else {
            result = executeStatement();
            resultName = "rowcount";
        }
        
        logger.log(logLevel, resultName + "=" + result);
    }

    private Object executeStatementReturnScalar() throws SQLException {
        PreparedStatement statement = m_connection.prepareStatement(m_sqlStatement);

        try {
            setParameters(statement);
            if (statement.execute()) {
                ResultSet rs = statement.getResultSet();
                try {
                    if (rs.next()) {
                        return rs.getObject(1);
                    }
                    
                    return null;
                } finally {
                    close(rs);
                }
            } else {
                return statement.getUpdateCount();
            }
        } finally {
            close(statement);
        }
    }
    
    private void setParameters(PreparedStatement statement) throws SQLException {
        int i = 1;
        for (Object value: m_params) {
            statement.setObject(i++, value);
        }
    }

    private Object executeStatement() throws SQLException {
        PreparedStatement statement = m_connection.prepareStatement(m_sqlStatement);

        try {
            setParameters(statement);
            statement.execute();
            return statement.getUpdateCount();
        } finally {
            close(statement);
        }
    }

    private void close(Statement stmt) {
        try {
            stmt.close();
        } catch (Throwable t) {
            logger.warn("Failed to close statement", t);
        }
    }

    private void close(ResultSet rs) {
        try {
            rs.close();
        } catch (Throwable t) {
            logger.warn("Failed to close result set", t);
        }
    }
    
    @Inject
    protected void setSqlShortener(@Named("SqlShortener") StringShortener sqlShortener) {
        m_sqlShortener = sqlShortener;
    }
}
