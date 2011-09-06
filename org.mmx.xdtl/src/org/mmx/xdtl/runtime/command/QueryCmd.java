package org.mmx.xdtl.runtime.command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryCmd implements RuntimeCommand {
    private final Logger m_logger = LoggerFactory.getLogger(QueryCmd.class);    
    private final JdbcConnection m_connection;
    private final String m_sqlStatement;
    private final String m_target;
    
    public QueryCmd(JdbcConnection connection, String sqlStatement, String target) {
        m_connection = connection;
        m_sqlStatement = sqlStatement;
        m_target = target;
    }
    
    @Override
    public void run(Context context) throws SQLException {
        m_logger.info("query: '{}'", m_sqlStatement);

        if (m_target == null || m_target.length() == 0) {
            executeStatement();
            return;
        }
        
        Object result = executeStatementReturnScalar();
        context.assignVariable(m_target, result);
    }

    private Object executeStatementReturnScalar() throws SQLException {
        Statement statement = m_connection.createStatement();

        try {
            if (statement.execute(m_sqlStatement)) {
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
    
    private void executeStatement() throws SQLException {
        Statement statement = m_connection.createStatement();

        try {
            statement.execute(m_sqlStatement);
        } finally {
            close(statement);
        }
    }

    private void close(Statement stmt) {
        try {
            stmt.close();
        } catch (Throwable t) {
            m_logger.warn("Failed to close statement", t);
        }
    }

    private void close(ResultSet rs) {
        try {
            rs.close();
        } catch (Throwable t) {
            m_logger.warn("Failed to close result set", t);
        }
    }
}
