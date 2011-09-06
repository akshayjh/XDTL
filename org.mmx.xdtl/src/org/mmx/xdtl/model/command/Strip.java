/**
 * 
 */
package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

/**
 * @author vsi
 */
public class Strip extends AbstractElement implements Command {
    private final String m_cmd;
    private final String m_source;
    private final String m_target;
    private final String m_overwrite;
    private final String m_expr;
    
    public Strip(String cmd, String source, String target, String overwrite,
            String expr) {
        super();
        m_cmd = cmd;
        m_source = source;
        m_target = target;
        m_overwrite = overwrite;
        m_expr = expr;
    }

    public String getCmd() {
        return m_cmd;
    }

    public String getSource() {
        return m_source;
    }

    public String getTarget() {
        return m_target;
    }

    public String getOverwrite() {
        return m_overwrite;
    }

    public String getExpr() {
        return m_expr;
    }
}
