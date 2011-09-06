package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Package;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.TaskList;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class PackageHandler extends AbstractElementHandler {
    private Package m_package;
    
    @Override
    public Element endElement() {
        return m_package;
    }

    @Override
    public void startElement(Attributes attr) {
        m_package = new Package(
                attr.getStringValue("name"),
                attr.getStringValue("onerror"),
                attr.getStringValue("resume"));
    }
    
    public void childElementComplete(Object child) {
        if (child instanceof Parameter) {
            m_package.addParameter((Parameter) child);
        } else if (child instanceof Variable) {
            m_package.addVariable((Variable) child);
        } else if (child instanceof Connection) {
            m_package.addConnection((Connection) child);
        } else if (child instanceof TaskList) {
            m_package.setTasks((TaskList) child);
        }
    }
}
