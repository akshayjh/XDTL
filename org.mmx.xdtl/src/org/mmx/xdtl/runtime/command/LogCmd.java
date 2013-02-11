/**
 * 
 */
package org.mmx.xdtl.runtime.command;

import org.apache.log4j.Logger;
import org.mmx.xdtl.model.command.Log.Level;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

/**
 * @author vsi
 */
public class LogCmd implements RuntimeCommand {
    private static final Logger logger = Logger.getLogger("log");

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
            logger.trace(m_msg);
            break;
        case DEBUG:
            logger.debug(m_msg);
            break;
        case INFO:
            logger.info(m_msg);
            break;
        case WARNING:
            logger.warn(m_msg);
            break;
        case ERROR:
            logger.error(m_msg);
            break;            
        }
    }
}
