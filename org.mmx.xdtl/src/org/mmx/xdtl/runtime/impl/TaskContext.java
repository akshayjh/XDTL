package org.mmx.xdtl.runtime.impl;

import org.mmx.xdtl.model.SourceLocator;
import org.mmx.xdtl.model.Task;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.runtime.ConnectionManager;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.TypeConverter;

public class TaskContext extends Context {
    private final Task    m_task;
    private final String  m_onErrorRef;
    private TypeConverter m_typeConverter;

    public TaskContext(Context upperContext,
            ConnectionManager connectionManager, Object scriptingGlobal,
            TypeConverter typeConverter, Task task, String onErrorRef,
            boolean resumeOnErrorEnabled) {

        super(upperContext, connectionManager, scriptingGlobal);
        m_task = task;
        m_onErrorRef = onErrorRef;
        m_typeConverter = typeConverter;
        addVariable(new Variable(VARNAME_XDTL_RESUME, resumeOnErrorEnabled));
    }

    public Task getTask() {
        return m_task;
    }

    public String getOnErrorRef() {
        return m_onErrorRef;
    }

    public boolean isOnErrorResumeEnabled() {
        Variable var = getVariable(VARNAME_XDTL_RESUME);
        if (var == null || var.getValue() == null) {
            return false;
        }

        return m_typeConverter.toBoolean(var.getValue());
    }

    @Override
    public String getTraceLine() {
        SourceLocator loc = m_task.getSourceLocator();
        return "task: " + m_task.getName() + "@" + loc.getDocumentUrl() + ":"
                + loc.getLineNumber();
    }
}
