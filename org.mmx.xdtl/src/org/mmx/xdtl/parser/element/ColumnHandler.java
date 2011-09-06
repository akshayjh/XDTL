package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Column;
import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ColumnHandler extends AbstractElementHandler {
    private Column m_column;
    
    @Override
    public Element endElement() {
        return m_column;
    }

    @Override
    public void startElement(Attributes attr) {
        m_column = new Column(attr.getValue("mapid"), 
                attr.getValue("target"),
                attr.getValue("source"),
                attr.getValue("function"),
                attr.getValue("datatype"),
                attr.getValue("isjoinkey"),
                attr.getValue("isupdatable"),
                attr.getValue("isdistinct"),
                attr.getValue("isaggregate"));
    }
}
