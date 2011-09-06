package org.mmx.xdtl.runtime.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.mmx.xdtl.db.JdbcConnection;

/**
 * A cache of JDBC connections.
 *  
 * @author vsi
 */
public class JdbcConnectionCache {
    private final HashMap<String, JdbcConnection> m_cache = new HashMap<String, JdbcConnection>();
   
    public void put(JdbcConnection cnn) {
        if (cnn == null) return;

        if (m_cache.containsKey(cnn.getName())) {
            return;
        }
        
        m_cache.put(cnn.getName(), cnn);
        cnn.addRef();
    }
    
    public JdbcConnection findByName(String name) {
        return m_cache.get(name);
    }
    
    public void removeAll() {
        for (JdbcConnection cnn: m_cache.values()) {
            cnn.release();
        }
        
        m_cache.clear();
    }
    
    public Collection<JdbcConnection> getAll() {
        return Collections.unmodifiableCollection(m_cache.values());
    }
    
    public void putAll(Collection<JdbcConnection> connections) {
        for (JdbcConnection cnn: connections) {
            put(cnn);
        }
    }
}
