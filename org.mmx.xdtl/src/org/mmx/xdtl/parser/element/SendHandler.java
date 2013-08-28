package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Send;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class SendHandler extends AbstractElementHandler {
    private Send m_elem;
    
    @Override
    public Element endElement() {
        return m_elem;
    }
    
    @Override
    public void startElement(Attributes attr) {
        m_elem = new Send(
                attr.getStringValue("source"),
                attr.getStringValue("target"),
                attr.getStringValue("overwrite"),
                attr.getStringValue("encoding"));
    }
}
