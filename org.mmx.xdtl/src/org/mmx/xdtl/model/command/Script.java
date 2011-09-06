package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public class Script extends AbstractElement implements Command {
    private final String m_target;
    private final String m_script;
    
    public Script(String script, String target) {
        super();
        m_script = script;
        m_target = target;
    }

    public String getTarget() {
        return m_target;
    }

    public String getScript() {
        return m_script;
    }
}
