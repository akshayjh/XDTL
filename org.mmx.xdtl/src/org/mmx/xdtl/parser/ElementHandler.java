package org.mmx.xdtl.parser;

import org.mmx.xdtl.model.Element;

public interface ElementHandler {
    void startElement(String name, Attributes attr);
    Element endElement();
    void characters(char[] ch, int start, int length);
    void childElementComplete(Object child);
}
