package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExitCmd implements RuntimeCommand {
    private final Logger m_logger = LoggerFactory.getLogger(ExitCmd.class);

    @Override
    public void run(Context context) throws Throwable {
        m_logger.info("exit");
        context.getEngineControl().exit();
    }
}
