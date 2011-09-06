package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Script;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ScriptHandler extends AbstractElementHandler {
    private String m_target;
    
    @Override
    public Element endElement() {
        return new Script(getText(), m_target);
    }

    @Override
    public void startElement(Attributes attr) {
        m_target = attr.getStringValue("target");
    }
}
