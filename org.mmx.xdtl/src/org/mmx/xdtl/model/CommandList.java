package org.mmx.xdtl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class CommandList extends AbstractElement implements Iterable<Command> {
    private final ArrayList<Command> m_commands = new ArrayList<Command>();

    public CommandList() {
    }
    
    public void add(Command cmd) {
        m_commands.add(cmd);
    }

    @Override
    public Iterator<Command> iterator() {
        return Collections.unmodifiableList(m_commands).iterator();
    }
}
