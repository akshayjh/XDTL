package org.mmx.xdtl.runtime.impl;

import java.sql.SQLException;

import org.mmx.xdtl.model.SourceLocator;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.OsProcessException;
import org.mmx.xdtl.runtime.XdtlError;

public class ErrorProperties {
    private final Throwable m_exception;

    public ErrorProperties(Throwable exception) {
        m_exception = exception;     
    }
    
    public String getError() {
        return m_exception.getMessage();
    }
    
    public SourceLocator getSourceLocator() {
        if (m_exception instanceof XdtlException) {
            return ((XdtlException) m_exception).getSourceLocator();
        }
        
        return SourceLocator.NULL;
    }
    
    public String getErrorCode() {
        Throwable cause = m_exception.getCause();
        if (cause != null) {
            if (cause instanceof XdtlError) {
                return ((XdtlError) cause).getCode();
            }
            
            if (cause instanceof SQLException) {
                return String.valueOf(((SQLException) cause).getErrorCode());
            }
            
            if (cause instanceof OsProcessException) {
                return String.valueOf(((OsProcessException) cause).getErrorCode());
            }
        }
        
        return null;
    }
    
    public String getErrorDesc() {
        Throwable cause = m_exception.getCause();
        if (cause == null) {
            return "";
        }
        
        return cause.getMessage();
    }
    
    public String getErrorType() {
        Throwable cause = m_exception.getCause();
        if (cause == null) {
            return "";
        }
        
        return cause.getClass().getName();
    }
    
    public Throwable getErrorCause() {
        Throwable cause = m_exception.getCause();
        return cause == null ? m_exception : cause;
    }
}
