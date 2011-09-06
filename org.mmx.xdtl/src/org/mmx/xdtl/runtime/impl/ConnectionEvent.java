package org.mmx.xdtl.runtime.impl;

public class ConnectionEvent {
    private final Object m_sender;
    private final String m_connectionName;

    public ConnectionEvent(Object sender, String connectionName) {
        m_sender = sender;
        m_connectionName = connectionName;
    }

    public String getConnectionName() {
        return m_connectionName;
    }

    public Object getSender() {
        return m_sender;
    }
}
