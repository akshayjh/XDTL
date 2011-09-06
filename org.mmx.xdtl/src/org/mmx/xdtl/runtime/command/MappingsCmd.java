package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class MappingsCmd implements RuntimeCommand {
    private final String m_target;
    private final Mappings m_mappings;
    
    public MappingsCmd(String target, Mappings mappings) {
        m_target = target;
        m_mappings = mappings;
    }
    
    @Override
    public void run(Context context) throws Throwable {
        context.assignVariable(m_target, m_mappings);
    }
}
