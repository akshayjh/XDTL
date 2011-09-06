package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.Task;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class TaskHandler extends AbstractElementHandler {
    private Task m_task;
    
    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Parameter) {
            m_task.addParameter((Parameter) child);
        } else if (child instanceof CommandList) {
            m_task.setCommandList((CommandList) child);
        }
    }

    @Override
    public Element endElement() {
        return m_task;
    }

    @Override
    public void startElement(Attributes attr) {
        m_task = new Task(attr.getStringValue("name"),
                attr.getStringValue("connection"),
                attr.getStringValue("onerror"),
                attr.getStringValue("resume"));
    }
}
