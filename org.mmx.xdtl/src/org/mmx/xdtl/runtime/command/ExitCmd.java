package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExitCmd implements RuntimeCommand {
    private final Logger m_logger = LoggerFactory.getLogger(ExitCmd.class);
    
    private final int m_code;
    private final boolean m_global;
    
    public ExitCmd(int code, boolean global) {
        m_code = code;
        m_global = global;
    }
    
    @Override
    public void run(Context context) throws Throwable {
    	if (m_global) {
    		m_logger.info("exit runtime");
    		context.getEngineControl().exit(m_code);
    	} else {
    		m_logger.info("exit current package");
    		context.getEngineControl().exit();
    	}
    }
}
