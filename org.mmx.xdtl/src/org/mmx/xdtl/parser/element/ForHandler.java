package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.For;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ForHandler extends AbstractElementHandler {
    private For m_elem;
    
    @Override
    public void startElement(Attributes attr) {
        m_elem = new For(attr.getStringValue("item"),
                attr.getStringValue("index"),
                attr.getStringValue("count"),
                attr.getStringValue("rowset"));
    }

    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Command) {
            m_elem.getCommandList().add((Command)child);
        }
    }
    
    @Override
    public Element endElement() {
        return m_elem;
    }
}
