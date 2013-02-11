package org.mmx.xdtl.runtime.command;

import org.apache.log4j.Logger;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.XdtlError;

public class ErrorCmd implements RuntimeCommand {
    private static final Logger logger = Logger.getLogger("xdtl.cmd.error");

    private final String m_code;
    private final String m_msg;
    
    public ErrorCmd(String code, String msg) {
        m_code = code;
        m_msg = msg;
    }
    
    @Override
    public void run(Context context) throws Throwable {
        logger.info("code=" + m_code + ", msg=" + m_msg);
        throw new XdtlError(m_code, m_msg);
    }
}
