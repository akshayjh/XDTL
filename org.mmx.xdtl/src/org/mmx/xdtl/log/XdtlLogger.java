package org.mmx.xdtl.log;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class XdtlLogger extends Logger {
    private static XdtlLoggerFactory m_factory = new XdtlLoggerFactory();

    protected XdtlLogger(String name) {
        super(name);
    }

    private boolean isLoggingEnabled() {
        return !XdtlMdc.isLoggingDisabled();
    }

    public static Logger getLogger(String name) {
        return Logger.getLogger(name, m_factory);
    }

    public static Logger getLogger(@SuppressWarnings("rawtypes") Class clazz) {
        return Logger.getLogger(clazz.getName());
    }

    @Override
    public boolean isTraceEnabled() {
        return super.isTraceEnabled() && isLoggingEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return super.isDebugEnabled() && isLoggingEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return super.isInfoEnabled() && isLoggingEnabled();
    }

    @Override
    protected void forcedLog(String fqcn, Priority level, Object message,
            Throwable t) {
        if (isLoggingEnabled()) {
            super.forcedLog(fqcn, level, message, t);
        }
    }

    @Override
    public boolean isEnabledFor(Priority level) {
        return super.isEnabledFor(level) && isLoggingEnabled();
    }
}
