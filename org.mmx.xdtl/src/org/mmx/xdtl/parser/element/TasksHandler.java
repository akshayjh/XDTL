package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Task;
import org.mmx.xdtl.model.TaskList;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class TasksHandler extends AbstractElementHandler {
    private TaskList m_taskList;
    
    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Task) {
            m_taskList.add((Task) child);
        }
    }

    @Override
    public Element endElement() {
        return m_taskList;
    }

    @Override
    public void startElement(Attributes attr) {
        m_taskList = new TaskList();
    }
}
