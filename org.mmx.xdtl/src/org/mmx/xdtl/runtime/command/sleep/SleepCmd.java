package org.mmx.xdtl.runtime.command.sleep;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class SleepCmd implements RuntimeCommand {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.sleep");
    private int m_seconds;

    public SleepCmd(int seconds) {
        m_seconds = seconds;
    }

    @Override
    public void run(Context context) throws Throwable {
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("seconds=%s", m_seconds));
        }

        try {
            Thread.sleep(m_seconds * 1000);
        } catch (InterruptedException e) {
            logger.warn("Interrupted");
        }
    }
}
