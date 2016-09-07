package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Script;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ScriptHandler extends AbstractElementHandler {
    private String m_href;
    private String m_encoding;

    @Override
    public Element endElement() {
        return new Script(m_href, m_encoding, getText());
    }

    @Override
    public void startElement(Attributes attr) {
        m_href = attr.getStringValue("href");
        m_encoding = attr.getStringValue("encoding");
    }
}
