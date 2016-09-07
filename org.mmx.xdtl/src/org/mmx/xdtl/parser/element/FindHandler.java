package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Find;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class FindHandler extends AbstractElementHandler {
    private Find m_find;

    @Override
    public Element endElement() {
        return m_find;
    }

    @Override
    protected void startElement(Attributes attr) {
        m_find = new Find(attr.getStringValue("source"),
                attr.getStringValue("match"),
                attr.getStringValue("recursive"),
                attr.getStringValue("rowset"));
    }
}
