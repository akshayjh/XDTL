package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public class Echo extends AbstractElement implements Command {
    private String m_message;

    public Echo(String message) {
        m_message = message;
    }

    public String getMessage() {
        return m_message;
    }

    public void setMessage(String message) {
        m_message = message;
    }
}
