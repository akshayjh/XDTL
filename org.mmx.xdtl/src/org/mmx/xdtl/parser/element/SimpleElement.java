package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.AbstractElement;

public class SimpleElement extends AbstractElement {
    private String m_value;

    public SimpleElement(String value) {
        m_value = value;
    }

    public String getValue() {
        return m_value;
    }

    public void setValue(String value) {
        m_value = value;
    }
    
    // TODO: Attributes
}
