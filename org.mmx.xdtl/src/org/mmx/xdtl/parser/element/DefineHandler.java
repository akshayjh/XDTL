package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class DefineHandler extends AbstractElementHandler {
    private String m_name;

    @Override
    public Element endElement() {
        return new Variable(m_name, getText(), true);
    }

    @Override
    public void startElement(Attributes attr) {
        m_name = attr.getStringValue("name");
    }
}
