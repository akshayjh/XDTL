package org.mmx.xdtl.runtime.command;

import java.util.Map;

import org.apache.log4j.Logger;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class CallCmd implements RuntimeCommand {
    private static final Logger logger = Logger.getLogger("xdtl.cmd.call");

    private final String m_ref;
    private final Map<String, Object> m_args;
    
    public CallCmd(String ref, Map<String, Object> args) {
        m_args = args;
        m_ref = ref;
    }
    
    @Override
    public void run(Context context) {
        if (logger.isDebugEnabled()) {
            logger.debug("href=" + m_ref + ", args=" + m_args);
        } else if (logger.isInfoEnabled() && !isLocal()) {
            logger.info("href=" + m_ref);
        }

        context.getEngineControl().call(m_ref, m_args);
    }
    
    private boolean isLocal() {
        return m_ref.startsWith("#");
    }
}
