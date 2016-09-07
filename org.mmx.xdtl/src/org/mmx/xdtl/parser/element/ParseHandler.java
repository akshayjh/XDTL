package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Parse;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ParseHandler extends AbstractElementHandler {
    private Parse m_elem;
    
    /**
     * @see org.mmx.xdtl.parser.ElementHandler#startElement(org.mmx.xdtl.parser.Attributes)
     */
    @Override
    public void startElement(Attributes attrs) {
        m_elem = new Parse(
                attrs.getStringValue("source"),
                attrs.getStringValue("rowset"),
                attrs.getStringValue("target"),
                attrs.getStringValue("grammar"),
                attrs.getStringValue("type"),
                attrs.getStringValue("template"));
    }

    /**
     * @see org.mmx.xdtl.parser.ElementHandler#endElement()
     */
    @Override
    public Element endElement() {
        return m_elem;
    }
}
