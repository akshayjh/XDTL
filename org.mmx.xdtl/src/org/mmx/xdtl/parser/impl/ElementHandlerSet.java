package org.mmx.xdtl.parser.impl;

import java.util.HashMap;

import org.mmx.xdtl.parser.ElementHandler;

public class ElementHandlerSet {
    private Class<? extends ElementHandler> m_default;
    
    private final HashMap<String, Class<? extends ElementHandler>> m_map =
        new HashMap<String, Class<? extends ElementHandler>>();
    
    public void add(String elementName, Class<? extends ElementHandler> elementHandlerClass) {
        m_map.put(elementName, elementHandlerClass);
    }
    
    public Class<? extends ElementHandler> get(String elementName) {
        return m_map.get(elementName);
    }
    
    public Class<? extends ElementHandler> getDefault() {
        return m_default;
    }

    public void setDefault(Class<? extends ElementHandler> deflt) {
        m_default = deflt;
    }
}
