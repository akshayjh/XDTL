/**
 *
 */
package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.TextFileProperties;
import org.mmx.xdtl.model.command.Fetch;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

/**
 * Handler for <i>fetch</i> element.
 * @author vsi
 */
public class FetchHandler extends AbstractElementHandler {
    private String m_source;
    private String m_connection;
    private String m_overwrite;
    private TextFileProperties m_textFileProperties;
    private String m_header;
    private String m_target;
    private String m_rowset;
    private String m_destination;

    /**
     * @see org.mmx.xdtl.parser.ElementHandler#startElement(org.mmx.xdtl.parser.Attributes)
     */
    @Override
    public void startElement(Attributes attrs) {
        m_source = attrs.getValue("source");
        m_connection = attrs.getStringValue("connection", null);
        m_overwrite = attrs.getStringValue("overwrite");
        m_textFileProperties = getTextFileProperties(attrs);
        m_header = attrs.getStringValue("header");
        m_target = attrs.getStringValue("target");
        m_rowset = attrs.getStringValue("rowset");
        m_destination = attrs.getValue("destination");
    }

    /**
     * @see org.mmx.xdtl.parser.ElementHandler#endElement()
     */
    @Override
    public Element endElement() {
        if (m_source == null) {
            m_source = getText();
            m_source = m_source != null ? m_source.trim() : "";
        }

        return new Fetch(m_source, m_connection, m_overwrite, m_textFileProperties,
                m_header, m_target, m_rowset, m_destination);
    }
}
