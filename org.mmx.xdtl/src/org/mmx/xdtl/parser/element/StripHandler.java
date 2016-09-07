package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Strip;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class StripHandler extends AbstractElementHandler {
    private Strip m_strip;
    
    @Override
    public Element endElement() {
        return m_strip;
    }

    @Override
    public void startElement(Attributes attr) {
        m_strip = new Strip(
                attr.getStringValue("cmd"),
                attr.getStringValue("source"),
                attr.getStringValue("target"),
                attr.getStringValue("overwrite"),
                attr.getStringValue("expr"));
    }
}
