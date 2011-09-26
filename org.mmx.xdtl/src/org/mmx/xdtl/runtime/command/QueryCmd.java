package org.mmx.xdtl.runtime.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
    private final List<Object> m_params;
    
    public QueryCmd(JdbcConnection connection, String sqlStatement, String target, List<Object> params) {
        m_connection = connection;
        m_sqlStatement = sqlStatement;
        m_target = target;
        m_params = params;
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

    private void executeStatement() throws SQLException {
        PreparedStatement statement = m_connection.prepareStatement(m_sqlStatement);

        try {
            setParameters(statement);
            statement.execute();
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
