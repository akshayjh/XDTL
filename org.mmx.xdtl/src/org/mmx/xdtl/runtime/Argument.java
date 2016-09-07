package org.mmx.xdtl.runtime;

public class Argument {
    private Object m_value;
    private boolean m_loggingDisabled;

    public Argument(Object value, boolean loggingDisabled) {
        super();
        m_value = value;
        m_loggingDisabled = loggingDisabled;
    }

    public Object getValue() {
        return m_value;
    }

    public boolean isLoggingDisabled() {
        return m_loggingDisabled;
    }
}
