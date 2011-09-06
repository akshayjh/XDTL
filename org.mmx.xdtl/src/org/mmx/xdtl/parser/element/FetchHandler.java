/**
 * 
 */
package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Fetch;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

/**
 * Handler for <i>fetch</i> element.
 * @author vsi
 */
public class FetchHandler extends AbstractElementHandler {
    private Fetch m_elem;
    
    /**
     * @see org.mmx.xdtl.parser.ElementHandler#startElement(org.mmx.xdtl.parser.Attributes)
     */
    @Override
    public void startElement(Attributes attrs) {
        m_elem = new Fetch(
                attrs.getStringValue("source"),
                attrs.getStringValue("connection", null),
                attrs.getStringValue("type"),
                attrs.getStringValue("overwrite"),
                attrs.getStringValue("delimiter"),
                attrs.getStringValue("quote"),
                attrs.getStringValue("target"),
                attrs.getStringValue("rowset"),
                attrs.getStringValue("encoding"));
    }

    /**
     * @see org.mmx.xdtl.parser.ElementHandler#endElement()
     */
    @Override
    public Element endElement() {
        return m_elem;
    }
}
