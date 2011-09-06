/**
 * 
 */
package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.If;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

/**
 * @author vsi
 */
public class IfHandler extends AbstractElementHandler {
    private If m_elem;

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
        m_elem = new If(attr.getStringValue("expr"));
    }

    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Command) {
            m_elem.getCommandList().add((Command) child);
        }
    }
}
