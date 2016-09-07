package org.mmx.xdtl.runtime.command;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class ExitCmd implements RuntimeCommand {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.exit");

    private final int m_code;
    private final boolean m_global;

    public ExitCmd(int code, boolean global) {
        m_code = code;
        m_global = global;
    }

    @Override
    public void run(Context context) throws Throwable {
    	if (m_global) {
    		logger.info("exit runtime, code=" + m_code);
    		context.getEngineControl().exit(m_code);
    	} else {
    		logger.info("exit current package");
    		context.getEngineControl().exit();
    	}
    }
}
