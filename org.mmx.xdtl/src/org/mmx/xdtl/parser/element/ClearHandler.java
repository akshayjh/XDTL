/**
 * 
 */
package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Clear;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

/**
 * @author vsi
 *
 */
public class ClearHandler extends AbstractElementHandler {
    private Clear m_clear;
    
    /**
     * @see org.mmx.xdtl.parser.ElementHandler#endElement()
     */
    @Override
    public Element endElement() {
        return m_clear;
    }

    /**
     * @see org.mmx.xdtl.parser.ElementHandler#startElement(org.mmx.xdtl.parser.Attributes)
     */
    @Override
    public void startElement(Attributes attr) {
        m_clear = new Clear(attr.getStringValue("target"));
    }
}
