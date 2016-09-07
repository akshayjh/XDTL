/**
 * 
 */
package org.mmx.xdtl.runtime;

import org.mmx.xdtl.model.Connection;

/**
 * @author vsi
 */
public class ConnectionManagerEvent {
    private final Object m_sender;
    private final Connection m_connectionElement;
    
    public ConnectionManagerEvent(Object sender, Connection connectionElement) {
        super();
        m_sender = sender;
        m_connectionElement = connectionElement;
    }

    public Connection getConnectionElement() {
        return m_connectionElement;
    }
    
    public Object getSender() {
        return m_sender;
    }
}
