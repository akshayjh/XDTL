package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class StepsHandler extends AbstractElementHandler {
    private CommandList m_commandList;
    
    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Command) {
            m_commandList.add((Command) child);
        }
    }

    @Override
    public Element endElement() {
        return m_commandList;
    }

    @Override
    public void startElement(Attributes attr) {
        m_commandList = new CommandList();
    }
}
