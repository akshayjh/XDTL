package org.mmx.xdtl.runtime.impl;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class CommandMapping {
    private final Class<? extends Command> m_modelClass;
    private final Class<? extends RuntimeCommand> m_runtimeClass;    
    private final Class<? extends CommandBuilder> m_builderClass;

    public CommandMapping(Class<? extends Command> modelClass,
            Class<? extends CommandBuilder> builderClass,
            Class<? extends RuntimeCommand> runtimeClass) {
        super();
        m_modelClass = modelClass;
        m_builderClass = builderClass;
        m_runtimeClass = runtimeClass;
    }

    public Class<? extends Command> getModelClass() {
        return m_modelClass;
    }

    public Class<? extends CommandBuilder> getBuilderClass() {
        return m_builderClass;
    }

    public Class<? extends RuntimeCommand> getRuntimeClass() {
        return m_runtimeClass;
    }
}
