package org.mmx.xdtl.runtime.util;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.slf4j.Logger;

public class LogChuteToSlf4jAdapter implements LogChute {
    private final Logger m_logger; 
    
    public LogChuteToSlf4jAdapter(Logger logger) {
        m_logger = logger;
    }
    
    @Override
    public void init(RuntimeServices arg0) throws Exception {
    }

    @Override
    public boolean isLevelEnabled(int level) {
        switch (level) {
        case LogChute.TRACE_ID:
            return m_logger.isTraceEnabled();
        case LogChute.DEBUG_ID:
            return m_logger.isDebugEnabled();
        case LogChute.INFO_ID:
            return m_logger.isInfoEnabled();
        case LogChute.WARN_ID:
            return m_logger.isWarnEnabled();
        case LogChute.ERROR_ID:
            return m_logger.isErrorEnabled();
        }

        return false;
    }

    @Override
    public void log(int level, String msg) {
        log(level, msg, null);
    }

    @Override
    public void log(int level, String msg, Throwable t) {
        switch (level) {
        case LogChute.TRACE_ID:
            m_logger.trace(msg, t);
            break;
        case LogChute.DEBUG_ID:
            m_logger.debug(msg, t);
            break;
        case LogChute.INFO_ID:
            m_logger.info(msg, t);
            break;
        case LogChute.WARN_ID:
            m_logger.warn(msg, t);
            break;
        case LogChute.ERROR_ID:
            m_logger.error(msg, t);
            break;
        default:
            // ??
        }
    }
}
