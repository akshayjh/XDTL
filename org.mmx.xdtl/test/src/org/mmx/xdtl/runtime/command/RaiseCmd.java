package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class RaiseCmd implements RuntimeCommand {
    private final Class<?> m_exceptionClass;
    
    public RaiseCmd(String exceptionClassName) throws ClassNotFoundException {
        m_exceptionClass = Class.forName(exceptionClassName);
    }
    
    @Override
    public void run(Context context) throws Throwable {
        throw (Throwable) m_exceptionClass.newInstance();
    }
}
