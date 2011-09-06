package org.mmx.xdtl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class TaskList extends AbstractElement implements Iterable<Task> {
    private final HashMap<String, Task> m_map = new HashMap<String, Task>();
    private final ArrayList<Task> m_list = new ArrayList<Task>();

    public TaskList() {
    }

    public Task add(Task task) {
        if (m_map.containsKey(task.getName())) {
            throw new XdtlException("Duplicate task '" + task.getName() + "'");
        }

        m_map.put(task.getName(), task);
        m_list.add(task);
        return task;
    }
    
    public Task get(String name) {
        return m_map.get(name);
    }

    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(m_list).iterator();
    }
}
