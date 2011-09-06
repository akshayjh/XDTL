package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ConnectionHandler extends AbstractElementHandler {
    private String m_name;
    private String m_type;
    private boolean m_exists;
    private String m_onOpen;
    
    @Override
    public Element endElement() {
        return new Connection(m_name, m_type, m_exists, getText(), m_onOpen);
    }

    @Override
    public void startElement(Attributes attr) {
        m_name   = attr.getStringValue("name");
        m_type   = attr.getStringValue("type");
        m_exists = attr.getBooleanValue("exists", false);
        m_onOpen = attr.getStringValue("onopen");
    }
}
