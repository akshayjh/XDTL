/**
 * 
 */
package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Log;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

/**
 * @author vsi
 */
public class LogHandler extends AbstractElementHandler {
    private Log m_elem;

    /**
     * @see org.mmx.xdtl.parser.ElementHandler#endElement()
     */
    @Override
    public Element endElement() {
        return m_elem;
    }

    /**
     * @see org.mmx.xdtl.parser.ElementHandler#startElement(org.mmx.xdtl.parser.Attributes)
     */
    @Override
    public void startElement(Attributes attr) {
        m_elem = new Log(attr.getStringValue("level"),
                attr.getStringValue("msg"));
    }
}
