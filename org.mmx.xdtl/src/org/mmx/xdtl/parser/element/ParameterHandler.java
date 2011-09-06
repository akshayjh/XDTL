package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ParameterHandler extends AbstractElementHandler {
    private Parameter m_param;
    
    @Override
    public Element endElement() {
        m_param.setValue(getText());
        return m_param;
    }

    @Override
    public void startElement(Attributes attr) {
        m_param = new Parameter();
        m_param.setName(attr.getStringValue("name"));
        m_param.setDefault(attr.getStringValue("default"));
        m_param.setRequired(attr.getBooleanValue("required", false));
    }
}
