package org.mmx.xdtl.runtime.command;

import java.util.Map;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class CallCmd implements RuntimeCommand {
    private final String m_ref;
    private final Map<String, Object> m_args;
    
    public CallCmd(String ref, Map<String, Object> args) {
        m_args = args;
        m_ref = ref;
    }
    
    @Override
    public void run(Context context) {
        context.getEngineControl().call(m_ref, m_args);
    }
}
