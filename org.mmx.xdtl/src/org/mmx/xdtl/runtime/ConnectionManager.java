package org.mmx.xdtl.runtime;

import java.sql.SQLException;
import java.util.Collection;

import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.model.Connection;

public interface ConnectionManager {
    public abstract void addListener(ConnectionManagerListener listener);
    public abstract void removeListener(ConnectionManagerListener listener);
    
    public abstract JdbcConnection getJdbcConnection(Connection modelCnn) throws SQLException;
    public abstract JdbcConnection getDefaultJdbcConnection();
    public abstract void setDefaultJdbcConnection(JdbcConnection cnn);
    public abstract void releaseAllJdbcConnections();
    public abstract Collection<JdbcConnection> getJdbcConnections();
    public abstract void addJdbcConnections(Collection<JdbcConnection> connections);
    public abstract void addJdbcConnection(JdbcConnection cnn);
}
