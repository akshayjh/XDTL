package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.TextFileProperties;
import org.mmx.xdtl.model.command.Send;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class SendHandler extends AbstractElementHandler {
    private String m_source;
    private String m_target;
    private String m_overwrite;
    private TextFileProperties m_textFileProperties;
    private String m_header;
    private String m_skip;
    private String m_rowset;

    @Override
    public Element endElement() {
        if (m_source == null) {
            m_source = getText();
            m_source = m_source != null ? m_source.trim() : "";
        }

        return new Send(m_source, m_target, m_overwrite, m_textFileProperties,
                m_header, m_skip, m_rowset);
    }

    @Override
    public void startElement(Attributes attr) {
        m_source = attr.getValue("source");
        m_target = attr.getStringValue("target");
        m_overwrite = attr.getStringValue("overwrite");
        m_textFileProperties = getTextFileProperties(attr);
        m_header = attr.getStringValue("header");
        m_skip = attr.getStringValue("skip");
        m_rowset = attr.getStringValue("rowset");
    }
}
