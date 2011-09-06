package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Error;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ErrorHandler extends AbstractElementHandler {
    private Error m_error;

    @Override
    public Element endElement() {
        return m_error;
    }

    @Override
    protected void startElement(Attributes attr) {
        m_error = new Error(
                attr.getStringValue("code"),
                attr.getStringValue("msg"));
    }
}
