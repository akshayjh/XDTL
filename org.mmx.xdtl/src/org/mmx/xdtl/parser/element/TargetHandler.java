package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Target;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class TargetHandler extends AbstractElementHandler {
    private Target m_target;
    
    @Override
    public Element endElement() {
        return m_target;
    }

    @Override
    public void startElement(Attributes attr) {
        m_target = new Target(attr.getValue("mapid"),
                attr.getValue("target"),
                attr.getValue("alias"),
                attr.getValue("isvirtual"));
    }
}
