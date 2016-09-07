package org.mmx.xdtl.runtime;

public class XdtlError extends RuntimeException {
    private String m_code;
    
    private static final long serialVersionUID = 1L;

    public XdtlError(String code, String msg) {
        super(msg);
        m_code = code;
    }
    
    public String getCode() {
        return m_code;
    }
}
