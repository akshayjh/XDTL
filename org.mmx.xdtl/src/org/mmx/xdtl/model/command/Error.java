package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public class Error extends AbstractElement implements Command {
    private final String m_code;
    private final String m_msg;
    
    public Error(String code, String msg) {
        super();
        m_code = code;
        m_msg = msg;
    }

    public String getCode() {
        return m_code;
    }

    public String getMsg() {
        return m_msg;
    }
}
