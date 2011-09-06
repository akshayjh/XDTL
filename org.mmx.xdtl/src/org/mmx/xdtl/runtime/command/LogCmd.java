/**
 * 
 */
package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.model.command.Log.Level;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vsi
 */
public class LogCmd implements RuntimeCommand {
    private final Logger m_logger = LoggerFactory.getLogger("log");

    private final Level m_level;
    private final String m_msg;

    public LogCmd(Level level, String msg) {
        m_level = level;
        m_msg = msg;
    }
    
    /**
     * @see org.mmx.xdtl.runtime.RuntimeCommand#run(org.mmx.xdtl.runtime.Context)
     */
    @Override
    public void run(Context context) throws Throwable {
        switch (m_level) {
        case TRACE:
            m_logger.trace(m_msg);
            break;
        case DEBUG:
            m_logger.debug(m_msg);
            break;
        case INFO:
            m_logger.info(m_msg);
            break;
        case WARNING:
            m_logger.warn(m_msg);
            break;
        case ERROR:
            m_logger.error(m_msg);
            break;            
        }
    }
}
