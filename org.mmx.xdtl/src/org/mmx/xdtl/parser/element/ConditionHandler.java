package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Condition;
import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ConditionHandler extends AbstractElementHandler {
    private Condition m_condition;
    
    @Override
    public Element endElement() {
        return m_condition;
    }

    @Override
    public void startElement(Attributes attr) {
        m_condition = new Condition(attr.getValue("mapid"),
                attr.getValue("alias"),
                attr.getValue("condition"),
                attr.getValue("condtype"),
                attr.getValue("jointype"));
    }
}
