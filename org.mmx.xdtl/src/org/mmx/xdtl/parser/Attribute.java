package org.mmx.xdtl.parser;

public class Attribute {
    private final String m_name;
    private final String m_value;

    public Attribute(String name, String value) {
        super();
        m_name = name;
        m_value = value;
    }

    public String getName() {
        return m_name;
    }

    public String getValue() {
        return m_value;
    }
}
