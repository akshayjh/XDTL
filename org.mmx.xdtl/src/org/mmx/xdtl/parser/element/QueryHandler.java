package org.mmx.xdtl.parser.element;

import java.util.ArrayList;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.command.Query;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class QueryHandler extends AbstractElementHandler {
    private String m_source;
    private String m_connection;
    private String m_queryType;
    private String m_target;
    private ArrayList<Parameter> m_parameters;

    @Override
    public Element endElement() {
        if (m_source == null) {
            m_source = getText();
            m_source = m_source != null ? m_source.trim() : "";
        }

        return new Query(m_source, m_connection, m_queryType, m_target, m_parameters);
    }

    @Override
    public void startElement(Attributes attr) {
        m_source = attr.getValue("source");
        m_connection = attr.getStringValue("connection", null);
        m_queryType = attr.getStringValue("querytype");
        m_target = attr.getStringValue("target");
    }

    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Parameter) {
            if (m_parameters == null) {
                m_parameters = new ArrayList<Parameter>();
            }

            m_parameters.add((Parameter) child);
        }
    }
}
