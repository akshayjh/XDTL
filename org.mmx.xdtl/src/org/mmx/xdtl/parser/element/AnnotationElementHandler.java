package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Annotation;
import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.parser.AbstractElementHandler;

public class AnnotationElementHandler extends AbstractElementHandler {

    @Override
    public Element endElement() {
        return new Annotation(getText());
    }
}
