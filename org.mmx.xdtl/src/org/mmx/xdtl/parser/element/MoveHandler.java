/**
 * 
 */
package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Move;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

/**
 * @author vsi
 */
public class MoveHandler extends AbstractElementHandler {
    private Move m_move;
    
    /**
     * @see org.mmx.xdtl.parser.ElementHandler#endElement()
     */
    @Override
    public Element endElement() {
        return m_move;
    }

    /**
     * @see org.mmx.xdtl.parser.ElementHandler#startElement(org.mmx.xdtl.parser.Attributes)
     */
    @Override
    public void startElement(Attributes attr) {
        m_move = new Move(attr.getStringValue("cmd"),
                attr.getStringValue("source"),
                attr.getStringValue("target"),
                attr.getStringValue("overwrite"));
    }
}
