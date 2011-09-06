package org.mmx.xdtl.model;

public class Variable extends AbstractElement {
    private final String m_name;
    private final boolean m_readOnly;
    private Object m_value;

    public Variable(String name) {
        super();
        m_name = name;
        m_readOnly = false;

        validateName();
    }
    
    public Variable(String name, Object value) {
        super();
        m_name = name;
        m_value = value;
        m_readOnly = false;

        validateName();
    }

    public Variable(String name, Object value, boolean readOnly) {
        super();
        m_name = name;
        m_value = value;
        m_readOnly = readOnly;

        validateName();
    }

    private void validateName() {
        if (m_name == null || m_name.equals("")) {
            throw new XdtlException("Variable must have a name");
        }
    }
    
    public String getName() {
        return m_name;
    }

    public Object getValue() {
        return m_value;
    }

    public void setValue(Object value) {
        if (m_readOnly) {
            throw new XdtlException("Variable '" + m_name + "' is read only");
        }

        m_value = value;
    }

    public boolean isReadOnly() {
        return m_readOnly;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
        result = prime * result + (m_readOnly ? 1231 : 1237);
        result = prime * result + ((m_value == null) ? 0 : m_value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Variable other = (Variable) obj;
        if (m_name == null) {
            if (other.m_name != null) {
                return false;
            }
        } else if (!m_name.equals(other.m_name)) {
            return false;
        }

        if (m_readOnly != other.m_readOnly) {
            return false;
        }

        if (m_value == null) {
            if (other.m_value != null) {
                return false;
            }
        } else if (!m_value.equals(other.m_value)) {
            return false;
        }

        return true;
    }
    
    @Override
    public String toString() {
        return "Variable [m_name=" + m_name + ", m_readOnly=" + m_readOnly
                + ", m_value=" + m_value + "]";
    }
}
