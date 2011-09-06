package org.mmx.xdtl.model;

public class Parameter extends AbstractElement {
    private String m_name;
    private String m_default;
    private boolean m_required;
    private String m_value;

    public Parameter() {
    }

    public Parameter(String name, String value) {
        m_name = name;
        m_default = "";
        m_required = false;
        m_value = value;
    }

    public Parameter(String name, String deflt, boolean required, String value) {
        m_name = name;
        m_default = deflt;
        m_required = required;
        m_value = value;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getDefault() {
        return m_default;
    }

    public void setDefault(String default1) {
        m_default = default1;
    }

    public boolean isRequired() {
        return m_required;
    }

    public void setRequired(boolean required) {
        m_required = required;
    }

    public String getValue() {
        return m_value;
    }

    public void setValue(String value) {
        m_value = value;
    }
}
