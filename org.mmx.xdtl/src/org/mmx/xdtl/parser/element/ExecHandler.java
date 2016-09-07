package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Exec;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ExecHandler extends AbstractElementHandler {
    private String m_shell;
    private String m_cmd;
    private String m_target;

    @Override
    public Element endElement() {
        String cmd = getText();
        if (cmd != null) {
            cmd = cmd.trim();
            if (cmd.length() > 0) {
                m_cmd = cmd;
            }
        }

        return new Exec(m_shell, m_cmd, m_target);
    }

    @Override
    public void startElement(Attributes attr) {
        m_shell = attr.getStringValue("shell");
        m_cmd = attr.getStringValue("cmd");
        m_target = attr.getStringValue("target");
    }
}
