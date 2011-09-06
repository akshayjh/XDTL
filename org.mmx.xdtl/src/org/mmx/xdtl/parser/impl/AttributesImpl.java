package org.mmx.xdtl.parser.impl;

import org.mmx.xdtl.parser.Attribute;
import org.mmx.xdtl.parser.Attributes;

public class AttributesImpl implements Attributes {
    private org.xml.sax.Attributes m_attrs;
    
    public AttributesImpl(org.xml.sax.Attributes attrs) {
        m_attrs = attrs;
    }
    
    @Override
    public boolean getBooleanValue(String name, boolean deflt) {
        String value = m_attrs.getValue(name);
        if (value != null) {
            return Integer.parseInt(value) != 0;
        }
        
        return deflt;
    }

    @Override
    public String getStringValue(String name) {
        return getStringValue(name, "");
    }
    
    @Override
    public String getStringValue(String name, String deflt) {
        String value = m_attrs.getValue(name);
        if (value != null) {
            return value;
        }
        
        return deflt;
    }

    @Override
    public int getIntValue(String name, int deflt) {
        String value = m_attrs.getValue(name);
        if (value != null) {
            return Integer.parseInt(value);
        }
        
        return deflt;
    }

    @Override
    public String getValue(String name) {
        return m_attrs.getValue(name);
    }
    
    public int getLength() {
        return m_attrs.getLength();
    }
    
    public Attribute get(int index) {
        return new Attribute(m_attrs.getQName(index), m_attrs.getValue(index));
    }
}
