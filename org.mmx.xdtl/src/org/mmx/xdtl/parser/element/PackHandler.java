package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Pack;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class PackHandler extends AbstractElementHandler {
    private Pack m_pack;
    
    @Override
    public Element endElement() {
        return m_pack;
    }

    @Override
    public void startElement(Attributes attr) {
        m_pack = new Pack(
                attr.getStringValue("cmd"),
                attr.getStringValue("source"),
                attr.getStringValue("target"),
                attr.getStringValue("overwrite"));
    }
}
