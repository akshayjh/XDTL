/**
 * 
 */
package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.CommandList;

/**
 * "for" element.
 * 
 * @author vsi
 */
public class For extends AbstractElement implements Command {
    private final String m_itemVarName;
    private final String m_indexVarName;
    private final String m_countVarName;
    private final String m_iterable;
    private final CommandList m_commandList = new CommandList();
    
    public For(String itemVarName, String indexVarName, String countVarName, String iterable) {
        super();
        m_itemVarName = itemVarName;
        m_indexVarName = indexVarName;
        m_countVarName = countVarName;
        m_iterable = iterable;
    }

    public String getItemVarName() {
        return m_itemVarName;
    }

    public String getIterable() {
        return m_iterable;
    }

    public CommandList getCommandList() {
        return m_commandList;
    }

    public String getIndexVarName() {
        return m_indexVarName;
    }

    public String getCountVarName() {
        return m_countVarName;
    }
}
