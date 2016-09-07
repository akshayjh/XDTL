/**
 * 
 */
package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

/**
 * @author vsi
 */
public class Log extends AbstractElement implements Command {
    private final String m_msg;
    private final String m_level;
    
    public enum Level {
        TRACE,
        DEBUG,
        INFO,
        WARNING,
        ERROR,
    }
    
    public Log(String level, String msg) {
        m_level = level;
        m_msg = msg;
    }

    public String getMsg() {
        return m_msg;
    }

    public String getLevel() {
        return m_level;
    }
}
