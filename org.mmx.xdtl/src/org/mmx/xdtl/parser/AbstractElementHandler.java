package org.mmx.xdtl.parser;

import org.mmx.xdtl.model.TextFileProperties;

public abstract class AbstractElementHandler implements ElementHandler {
    private StringBuilder m_textBuffer;

    @Override
    public void startElement(String nsUri, String name, Attributes attr) {
        startElement(attr);
    }

    @Override
    public void childElementComplete(Object child) {
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (m_textBuffer == null) {
            m_textBuffer = new StringBuilder();
        }

        m_textBuffer.append(ch, start, length);
    }

    protected void startElement(Attributes attr) {
    }

    protected String getText() {
        return m_textBuffer != null ? m_textBuffer.toString() : null;
    }

    protected TextFileProperties getTextFileProperties(Attributes attr) {
        return new TextFileProperties(attr.getStringValue("type"),
                attr.getStringValue("delimiter"),
                attr.getStringValue("quote"),
                attr.getStringValue("null"),
                attr.getStringValue("escape"),
                attr.getStringValue("encoding"));
    }
}
