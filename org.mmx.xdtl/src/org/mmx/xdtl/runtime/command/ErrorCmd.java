package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.XdtlError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorCmd implements RuntimeCommand {
    private final Logger m_logger = LoggerFactory.getLogger(ErrorCmd.class);

    private final String m_code;
    private final String m_msg;
    
    public ErrorCmd(String code, String msg) {
        m_code = code;
        m_msg = msg;
    }
    
    @Override
    public void run(Context context) throws Throwable {
        m_logger.info("ErrorCmd: code={}, msg={}", m_code, m_msg);
        throw new XdtlError(m_code, m_msg);
    }
}
