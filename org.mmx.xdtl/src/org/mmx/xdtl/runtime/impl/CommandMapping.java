package org.mmx.xdtl.runtime.impl;

import java.util.HashMap;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.RuntimeCommandClassMap;

public class CommandMapping implements RuntimeCommandClassMap {
    private final Class<? extends Command> m_modelClass;
    private final Class<? extends CommandBuilder> m_builderClass;
    private Class<? extends RuntimeCommand> m_commandClass;
    private HashMap<String, Class<? extends RuntimeCommand>> m_commandClassMap;

    public CommandMapping(Class<? extends Command> modelClass,
            Class<? extends CommandBuilder> builderClass,
            Class<? extends RuntimeCommand> commandClass) {
        super();
        m_modelClass = modelClass;
        m_builderClass = builderClass;
        m_commandClass = commandClass;
    }

    public Class<? extends Command> getModelClass() {
        return m_modelClass;
    }

    public Class<? extends CommandBuilder> getBuilderClass() {
        return m_builderClass;
    }

    public Class<? extends RuntimeCommand> getRuntimeClass() {
        return m_commandClass;
    }

    @Override
    public Class<? extends RuntimeCommand> getCommandClass(String tag) {
        if (tag == null || tag.length() == 0) {
            return m_commandClass;
        }

        return m_commandClassMap != null ? m_commandClassMap.get(tag) : null;
    }

    public CommandMapping putCommandClass(String tag, Class<? extends RuntimeCommand> commandClass) {
        if (tag == null || tag.length() == 0) {
            m_commandClass = commandClass;
        } else {
            if (m_commandClassMap == null) {
                m_commandClassMap = new HashMap<String, Class<? extends RuntimeCommand>>(5);
            }

            m_commandClassMap.put(tag, commandClass);
        }

        return this;
    }
}
