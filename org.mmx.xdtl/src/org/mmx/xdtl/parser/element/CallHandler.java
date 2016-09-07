package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.command.Call;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class CallHandler extends AbstractElementHandler {
    private Call m_call;
    
    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Parameter) {
            m_call.addParameter((Parameter) child);
        }
    }

    @Override
    public Element endElement() {
        return m_call;
    }

    @Override
    public void startElement(Attributes attr) {
        m_call = new Call(attr.getStringValue("href"));
    }
}
