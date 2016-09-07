package org.mmx.xdtl.runtime;

import org.mmx.xdtl.model.SourceLocator;
import org.mmx.xdtl.model.XdtlException;

public class OsProcessException extends XdtlException {
    private static final long serialVersionUID = 1L;
    
    private final int m_errorCode;

    public OsProcessException(String msg, int errorCode) {
        super(msg);
        m_errorCode = errorCode;
    }
    
    public OsProcessException(String msg, int errorCode,
            SourceLocator sourceLocator, Throwable t) {
        super(msg, sourceLocator, t);

        m_errorCode = errorCode;
    }

    public int getErrorCode() {
        return m_errorCode;
    }
}
