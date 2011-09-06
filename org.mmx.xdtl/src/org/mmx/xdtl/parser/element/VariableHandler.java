package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class VariableHandler extends AbstractElementHandler {
    private Variable m_var;
    
    @Override
    public Element endElement() {
        m_var.setValue(getText());
        return m_var;
    }

    @Override
    public void startElement(Attributes attr) {
        m_var = new Variable(attr.getStringValue("name"), null);
    }
}
