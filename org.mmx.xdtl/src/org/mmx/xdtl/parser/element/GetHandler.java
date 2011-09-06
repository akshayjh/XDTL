package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Get;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class GetHandler extends AbstractElementHandler {
    private Get m_get;
    
    @Override
    public Element endElement() {
        return m_get;
    }

    @Override
    public void startElement(Attributes attr) {
        m_get = new Get(
                attr.getStringValue("cmd"),
                attr.getStringValue("source"),
                attr.getStringValue("target"),
                attr.getStringValue("overwrite"));
    }
}
