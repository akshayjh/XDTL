package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Put;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class PutHandler extends AbstractElementHandler {
    private Put m_put;
    
    @Override
    public Element endElement() {
        return m_put;
    }

    @Override
    public void startElement(Attributes attr) {
        m_put = new Put(
                attr.getStringValue("cmd"),
                attr.getStringValue("source"),
                attr.getStringValue("target"),
                attr.getStringValue("overwrite"));
    }
}
