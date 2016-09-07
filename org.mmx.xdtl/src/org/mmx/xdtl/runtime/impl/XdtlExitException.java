package org.mmx.xdtl.runtime.impl;

/**
 * This exception exits current task. Execution of (possibly) nested command
 * lists must be stopped, an exception is one way to do this.
 *
 * @author vsi
 */
class XdtlExitException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private int m_code;
    private boolean m_global;

    public XdtlExitException() {
        super();
        m_code = 0;
        m_global = false;
    }
    
    public XdtlExitException(int code, boolean global) {
        super();
        m_code = code;
        m_global = global;
    }
    
    public int getCode() {
    	return m_code;
    }
    
    public boolean getGlobal() {
    	return m_global;
    }
}
