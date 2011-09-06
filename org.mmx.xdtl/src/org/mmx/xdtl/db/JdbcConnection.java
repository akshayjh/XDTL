package org.mmx.xdtl.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.mmx.xdtl.model.XdtlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcConnection {
    private final Logger m_logger = LoggerFactory.getLogger(JdbcConnection.class);
    
    private String m_name;
    private Connection m_connection;
    private int refcount = 1;
    
    public JdbcConnection(String name, Connection connection) {
        m_connection = connection;
        m_name = name;
    }

    public void addRef() {
        refcount++;
    }
    
    public void release() {
        if (refcount == 0) {
            return;
        }
        
        refcount--;
        
        if (refcount == 0) {
            m_logger.debug("{}: closing connection", m_name);
            
            try {
                m_connection.close();
            } catch (SQLException e) {
                throw new XdtlException("Failed to close connection '" + m_name + "'", e);
            }
        }
    }

    public String getName() {
        return m_name;
    }

    public Statement createStatement() throws SQLException {
        return m_connection.createStatement();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        m_connection.setAutoCommit(autoCommit);
    }

    public void commit() throws SQLException {
        m_connection.commit();
    }

    public void rollback() throws SQLException {
        m_connection.rollback();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return m_connection.prepareStatement(sql);
    }

    public boolean getAutoCommit() throws SQLException {
        return m_connection.getAutoCommit();
    }
    
    public DatabaseMetaData getMetaData() throws SQLException {
    	return m_connection.getMetaData();
    }
}
