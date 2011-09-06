package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public class Raise extends AbstractElement implements Command {
    private String m_exception;

    public Raise(String exception) {
        m_exception = exception;
    }

    public String getException() {
        return m_exception;
    }

    public void setException(String exception) {
        m_exception = exception;
    }
}
