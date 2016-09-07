package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Sleep;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class SleepHandler extends AbstractElementHandler {
    private Sleep m_elem;

    @Override
    public Element endElement() {
        return m_elem;
    }

    @Override
    protected void startElement(Attributes attr) {
        m_elem = new Sleep(attr.getStringValue("seconds"));
    }
}
