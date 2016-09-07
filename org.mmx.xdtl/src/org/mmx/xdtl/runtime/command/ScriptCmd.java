package org.mmx.xdtl.runtime.command;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

import com.google.inject.Inject;

public class ScriptCmd implements RuntimeCommand {
    private final String m_script;
    private final String m_target;

    private ScriptEngine m_scriptEngine;

    public ScriptCmd(String script, String target) {
        super();
        m_script = script;
        m_target = target;
    }

    @Override
    public void run(Context context) throws Throwable {
        m_scriptEngine.setBindings(context.getBindings(), ScriptContext.ENGINE_SCOPE);
        Object result = m_scriptEngine.eval(m_script);

        if (m_target != null && m_target.length() != 0) {
            context.assignVariable(m_target, result);
        }
    }

    public ScriptEngine getScriptEngine() {
        return m_scriptEngine;
    }

    @Inject
    public void setScriptEngine(ScriptEngine scriptEngine) {
        m_scriptEngine = scriptEngine;
    }
}
