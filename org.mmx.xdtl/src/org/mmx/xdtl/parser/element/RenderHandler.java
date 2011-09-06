package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.command.Render;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class RenderHandler extends AbstractElementHandler {
    private Render m_elem;
    
    @Override
    public Element endElement() {
        return m_elem;
    }

    @Override
    public void startElement(Attributes attr) {
        m_elem = new Render(
                attr.getStringValue("template"),
                attr.getStringValue("source"),
                attr.getStringValue("target"),
                attr.getStringValue("rowset"));
    }

    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Parameter) {
            m_elem.addParameter((Parameter) child);
        }
    }
}
