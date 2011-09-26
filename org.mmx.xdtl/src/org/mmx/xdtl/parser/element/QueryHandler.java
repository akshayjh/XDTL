package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.command.Query;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class QueryHandler extends AbstractElementHandler {
    private Query m_query;
    
    @Override
    public Element endElement() {
        return m_query;
    }

    @Override
    public void startElement(Attributes attr) {
        m_query = new Query(
                attr.getStringValue("source"),
                attr.getStringValue("connection", null),
                attr.getStringValue("querytype"),
                attr.getStringValue("target"));
    }

    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Parameter) {
            m_query.addParameter((Parameter) child);
        }
    }
}
