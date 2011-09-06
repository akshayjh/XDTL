/**
 * 
 */
package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

/**
 * @author vsi
 */
public class Clear extends AbstractElement implements Command {
    private final String m_target;

    public Clear(String target) {
        super();
        m_target = target;
    }

    public String getTarget() {
        return m_target;
    }
}
