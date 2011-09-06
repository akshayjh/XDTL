package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public abstract class AbstractCmdBuilder implements CommandBuilder {
    private Context m_context;
    private Class<? extends RuntimeCommand> m_runtimeClass;
    
    @Override
    public <T extends RuntimeCommand> RuntimeCommand build(Context context,
            Class<T> runtimeClass, Command cmd) throws Exception {
        
        m_context = context;
        m_runtimeClass = runtimeClass;

        evaluate(cmd);
        return createInstance();
    }

    protected abstract void evaluate(Command cmd) throws Exception;
    protected abstract RuntimeCommand createInstance() throws Exception;

    protected Context getContext() {
        return m_context;
    }

    protected Class<? extends RuntimeCommand> getRuntimeClass() {
        return m_runtimeClass;
    }
}
