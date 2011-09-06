package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Column;
import org.mmx.xdtl.model.Condition;
import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Source;
import org.mmx.xdtl.model.Target;
import org.mmx.xdtl.model.command.Mappings;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class MappingsHandler extends AbstractElementHandler {
    private Mappings m_mappings;
    
    @Override
    public Element endElement() {
        return m_mappings;
    }

    @Override
    public void startElement(Attributes attr) {
        m_mappings = new Mappings(attr.getStringValue("target"));
    }

    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Source) {
            m_mappings.addSource((Source) child);
        } else if (child instanceof Target) {
            m_mappings.addTarget((Target) child);
        } else if (child instanceof Column) {
            m_mappings.addColumn((Column) child);
        } else if (child instanceof Condition) {
            m_mappings.addCondition((Condition) child);
        }
    }
}
