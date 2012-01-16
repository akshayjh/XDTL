package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Decode;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class DecodeHandler extends AbstractElementHandler {
    private Decode m_elem;
    
    /**
     * @see org.mmx.xdtl.parser.ElementHandler#startElement(org.mmx.xdtl.parser.Attributes)
     */
    @Override
    public void startElement(Attributes attrs) {
        m_elem = new Decode(
                attrs.getStringValue("source"),
                attrs.getStringValue("target"),
                attrs.getStringValue("type"));
    }

    /**
     * @see org.mmx.xdtl.parser.ElementHandler#endElement()
     */
    @Override
    public Element endElement() {
        return m_elem;
    }
}
