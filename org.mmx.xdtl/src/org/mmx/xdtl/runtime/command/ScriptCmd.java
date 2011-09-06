package org.mmx.xdtl.runtime.command;

import javax.script.ScriptEngine;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.util.ContextToBindingsAdapter;

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
        Object result = m_scriptEngine.eval(m_script,
                new ContextToBindingsAdapter(context));
        
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
