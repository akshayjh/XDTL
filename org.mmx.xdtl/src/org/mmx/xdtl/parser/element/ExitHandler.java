package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Exit;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ExitHandler extends AbstractElementHandler {
    private Exit m_exit;
    
    @Override
    public Element endElement() {
        return m_exit;
    }

    @Override
    protected void startElement(Attributes attr) {
        m_exit = new Exit();
    }
}
