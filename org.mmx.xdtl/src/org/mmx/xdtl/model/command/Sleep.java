package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public class Sleep extends AbstractElement implements Command {
    private final String m_seconds;

    public Sleep(String seconds) {
        m_seconds = seconds;
    }

    public String getSeconds() {
        return m_seconds;
    }
}
