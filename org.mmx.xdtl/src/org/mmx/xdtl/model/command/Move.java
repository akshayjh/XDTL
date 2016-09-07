/**
 * 
 */
package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;


/**
 * @author vsi
 */
public class Move extends AbstractElement implements Command {
	private final String m_source;
    private final String m_target;
    private final String m_overwrite;
    
    public Move(String source, String target, String overwrite) {
        m_source = source;
        m_target = target;
        m_overwrite = overwrite;
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
}