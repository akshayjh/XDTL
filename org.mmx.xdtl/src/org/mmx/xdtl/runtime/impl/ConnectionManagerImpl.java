package org.mmx.xdtl.runtime.impl;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.ConnectionManager;
import org.mmx.xdtl.runtime.ConnectionManagerEvent;
import org.mmx.xdtl.runtime.ConnectionManagerListener;

import com.google.inject.Inject;

public class ConnectionManagerImpl implements ConnectionManager {
    private static final Logger logger = Logger.getLogger("xdtl.rt.connectionManager");

    private final JdbcConnectionCache m_cache;
    private final ArrayList<ConnectionManagerListener> m_listeners = new ArrayList<ConnectionManagerListener>();
    private JdbcConnection m_defaultConnection;
    
    @Inject
    public ConnectionManagerImpl(JdbcConnectionCache cache) {
        m_cache = cache;
    }
        
    @Override
    public void addJdbcConnection(JdbcConnection cnn) {
        m_cache.put(cnn);
    }

    @Override
    public JdbcConnection getJdbcConnection(Connection modelCnn) throws SQLException {
        if (modelCnn == null) {
            if (m_defaultConnection == null) {
                throw new XdtlException("No default connection");
            }
            
            return m_defaultConnection;
        }
        
        JdbcConnection result = m_cache.findByName(modelCnn.getName());
        if (result == null) {
            result = createJdbcConnection(modelCnn);
        }
        
        return result;
    }

    private JdbcConnection createJdbcConnection(Connection modelCnn)
            throws SQLException {
        
        if (!Connection.TYPE_DB.equals(modelCnn.getType())) {
            throw new XdtlException("'" + modelCnn.getName() +
                    "' must be a database connection, actual type: '" +
                    modelCnn.getType() + "'",
                    modelCnn.getSourceLocator());
        }

        if (logger.isTraceEnabled()) {
            logger.debug("Creating JDBC connection from URL '" + modelCnn.getValue() + "'");
        }

        java.sql.Connection cnn = DriverManager.getConnection(modelCnn.getValue());
        JdbcConnection result = new JdbcConnection(modelCnn.getName(), cnn);

        try {
            result.setAutoCommit(true);
        } catch (SQLException e) {
            close(cnn);
            throw e;
        }
        
        m_cache.put(result);
        
        // cache will hold the reference
        result.release();
        
        fireConnectionOpenedEvent(modelCnn);
        return result;
    }

    private void close(java.sql.Connection cnn) {
        try {
            cnn.close();
        } catch (SQLException e) {
            logger.warn("Failed to close connection", e);
        }
    }

    @Override
    public void releaseAllJdbcConnections() {
        m_cache.removeAll();
    }

    @Override
    public void addJdbcConnections(Collection<JdbcConnection> connections) {
        m_cache.putAll(connections);
    }

    @Override
    public Collection<JdbcConnection> getJdbcConnections() {
        return m_cache.getAll();
    }

    @Override
    public void setDefaultJdbcConnection(JdbcConnection cnn) {
        if (cnn != null) m_cache.put(cnn);
        m_defaultConnection = cnn;
    }

    @Override
    public JdbcConnection getDefaultJdbcConnection() {
        return m_defaultConnection;
    }

    @Override
    public void addListener(ConnectionManagerListener listener) {
        if (m_listeners.contains(listener)) {
            return;
        }
        
        m_listeners.add(listener);
    }

    @Override
    public void removeListener(ConnectionManagerListener listener) {
        m_listeners.remove(listener);
    }
    
    private void fireConnectionOpenedEvent(Connection connectionElement) {
        if (m_listeners.size() == 0) {
            return;
        }
        
        ConnectionManagerEvent event = new ConnectionManagerEvent(this, connectionElement);
        
        for (ConnectionManagerListener listener: m_listeners) {
            listener.connectionOpened(event);
        }
    }
}
