package org.mmx.xdtl.runtime.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

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
        return m_logger.isEnabledFor(logChuteLevelToLog4jLevel(level));
    }

    @Override
    public void log(int level, String msg) {
        m_logger.log(logChuteLevelToLog4jLevel(level), msg);
    }

    @Override
    public void log(int level, String msg, Throwable t) {
        m_logger.log(logChuteLevelToLog4jLevel(level), msg, t);
    }
    
    private Level logChuteLevelToLog4jLevel(int logChuteLevel) {
        switch (logChuteLevel) {
        case LogChute.TRACE_ID:
            return Level.TRACE;
        case LogChute.DEBUG_ID:
            return Level.DEBUG;
        case LogChute.INFO_ID:
            return Level.INFO;
        case LogChute.WARN_ID:
            return Level.WARN;
        case LogChute.ERROR_ID:
            return Level.ERROR;
        default:
            // never reached
            return Level.INFO;
        }
    }
}
