package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Read;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ReadHandler extends AbstractElementHandler {
    private Read m_read;
    
    @Override
    public Element endElement() {
        return m_read;
    }

    @Override
    public void startElement(Attributes attr) {
        m_read = new Read(
                attr.getStringValue("source"),
                attr.getStringValue("target"),
                attr.getStringValue("connection"),
                attr.getStringValue("type"),
                attr.getStringValue("overwrite"),
                attr.getStringValue("delimiter"),
                attr.getStringValue("quote"),
                attr.getStringValue("encoding"),
                attr.getStringValue("escape"),
                attr.getStringValue("errors"),
                attr.getStringValue("header"),
                attr.getStringValue("skip"),
                attr.getStringValue("batch"));
    }
}
