package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Source;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class SourceHandler extends AbstractElementHandler {
    private Source m_source;

    @Override
    public Element endElement() {
        return m_source;
    }

    @Override
    public void startElement(Attributes attr) {
        m_source = new Source(
                attr.getValue("mapid"),
                attr.getValue("source"),
                attr.getValue("alias"),
                attr.getValue("isquery"));
    }
}
