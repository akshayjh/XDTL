/**
 * 
 */
package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.CommandList;

/**
 * "If" element. 
 * 
 * @author vsi
 */
public class If extends AbstractElement implements Command {
    private final String m_expr;
    private final CommandList m_commandList = new CommandList();
    
    public If(String expr) {
        m_expr = expr;
    }

    public String getExpr() {
        return m_expr;
    }

    public CommandList getCommandList() {
        return m_commandList;
    }
}
