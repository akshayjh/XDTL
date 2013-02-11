package org.mmx.xdtl.runtime.command;

import java.util.Map;

import org.apache.log4j.Logger;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class ExtensionCmd implements RuntimeCommand {
    private static final Logger logger = Logger.getLogger("xdtl.cmd.extension");

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
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("nsUri=%s, name=%s, params=%s", m_nsUri, m_name, m_params));
        } else if (logger.isInfoEnabled()) {
            logger.info(String.format("nsUri=%s, name=%s", m_nsUri, m_name));
        }
        context.getEngineControl().callExtension(m_nsUri, m_name, m_params);
    }
}
