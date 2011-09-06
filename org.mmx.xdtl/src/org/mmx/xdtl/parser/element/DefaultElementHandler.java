package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.SourceLocator;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Extension;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attribute;
import org.mmx.xdtl.parser.Attributes;

public class DefaultElementHandler extends AbstractElementHandler {
    private Extension m_elem;

    @Override
    public Element endElement() {
        return m_elem;
    }

    @Override
    public void startElement(String name, Attributes attr) {
        m_elem = new Extension(name);

        for (int i = 0; i < attr.getLength(); i++) {
            Attribute a = attr.get(i);
            if (!"id".equals(a.getName())) {
                m_elem.addParameter(new Parameter(a.getName(), a.getValue()));                
            }
        }
    }

    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Parameter) {
            m_elem.addParameter((Parameter) child);
            return;
        }
        
        SourceLocator srcLocator = (child instanceof Element)
                ? ((Element) child).getSourceLocator()
                : null;
        
        throw new XdtlException("Only 'parameter' elements are allowed inside "
                + "unknown elements", srcLocator);
    }
}
