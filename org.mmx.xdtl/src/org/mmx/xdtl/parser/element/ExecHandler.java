package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Exec;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ExecHandler extends AbstractElementHandler {
    private Exec m_exec;
    
    @Override
    public Element endElement() {
        return m_exec;
    }

    @Override
    public void startElement(Attributes attr) {
        m_exec = new Exec(
                attr.getStringValue("shell"),
                attr.getStringValue("cmd"),
                attr.getStringValue("target"));
    }
}
