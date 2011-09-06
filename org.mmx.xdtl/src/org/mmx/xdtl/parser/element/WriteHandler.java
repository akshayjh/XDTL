package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Write;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class WriteHandler extends AbstractElementHandler {
    private Write m_write;
    
    @Override
    public Element endElement() {
        return m_write;
    }

    @Override
    public void startElement(Attributes attr) {
        m_write = new Write(
                attr.getStringValue("source"),
                attr.getStringValue("target"),
                attr.getStringValue("connection"),
                attr.getStringValue("type"),
                attr.getStringValue("overwrite"),
                attr.getStringValue("delimiter"),
                attr.getStringValue("quote"),
                attr.getStringValue("encoding"));
    }
}
