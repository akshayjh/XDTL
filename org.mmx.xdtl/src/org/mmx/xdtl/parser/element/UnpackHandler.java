package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Unpack;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class UnpackHandler extends AbstractElementHandler {
    private Unpack m_unpack;
    
    @Override
    public Element endElement() {
        return m_unpack;
    }

    @Override
    public void startElement(Attributes attr) {
        m_unpack = new Unpack(
                attr.getStringValue("cmd"),
                attr.getStringValue("source"),
                attr.getStringValue("target"),
                attr.getStringValue("overwrite"),
                attr.getStringValue("options"));
    }
}
