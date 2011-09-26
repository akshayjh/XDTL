package org.mmx.xdtl.runtime.command;

import java.util.Map;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class ExtensionCmd implements RuntimeCommand {
    private final String m_nsUri;
    private final String m_name;
    private final Map<String, Object> m_params;
    
    public ExtensionCmd(String nsUri, String name, Map<String, Object> params) {
        m_nsUri = nsUri;
        m_name = name;
        m_params = params;
    }
    
    @Override
    public void run(Context context) throws Throwable {
        context.getEngineControl().callExtension(m_nsUri, m_name, m_params);
    }
}
