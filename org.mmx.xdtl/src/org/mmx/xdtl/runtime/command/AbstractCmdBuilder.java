package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.RuntimeCommandClassMap;

public abstract class AbstractCmdBuilder implements CommandBuilder {
    private Context m_context;
    private RuntimeCommandClassMap m_rtCmdClassMap;

    @Override
    public RuntimeCommand build(Context context,
            RuntimeCommandClassMap rtCmdClassMap, Command cmd) throws Exception {

        m_context = context;
        m_rtCmdClassMap = rtCmdClassMap;

        evaluate(cmd);
        return createInstance();
    }

    protected abstract void evaluate(Command cmd) throws Exception;
    protected abstract RuntimeCommand createInstance() throws Exception;

    protected Context getContext() {
        return m_context;
    }

    protected Class<? extends RuntimeCommand> getRuntimeClass() {
        return m_rtCmdClassMap.getCommandClass(null);
    }

    protected Class<? extends RuntimeCommand> getRuntimeClass(String tag) {
        Class<? extends RuntimeCommand> result;

        if (tag != null && tag.length() != 0) {
            result = m_rtCmdClassMap.getCommandClass(tag);
            if (result != null) {
                return result;
            }
        }

        return getRuntimeClass();
    }
}
