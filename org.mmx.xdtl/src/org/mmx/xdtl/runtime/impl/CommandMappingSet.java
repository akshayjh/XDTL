package org.mmx.xdtl.runtime.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.mmx.xdtl.model.Command;

public class CommandMappingSet {
    private final HashMap<Class<? extends Command>, CommandMapping> m_map =
        new HashMap<Class<? extends Command>, CommandMapping>();
    
    public CommandMappingSet() {
    }
    
    public void putMapping(CommandMapping mapping) {
        m_map.put(mapping.getModelClass(), mapping);
    }
    
    public CommandMapping findByModelClass(Class<? extends Command> modelClass) {
        return m_map.get(modelClass);
    }
    
    public Collection<CommandMapping> getMappings() {
        return Collections.unmodifiableCollection(m_map.values());
    }
}
