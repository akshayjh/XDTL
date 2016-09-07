package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.command.Extension;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;

import com.google.inject.Inject;

public class ExtensionCmdBuilder extends AbstractCmdBuilder {
    private final ExpressionEvaluator m_exprEvaluator;

    private Map<String, Object> m_params = new HashMap<String, Object>();
    private String m_name;
    private String m_nsUri;

    @Inject
    public ExtensionCmdBuilder(ExpressionEvaluator exprEvaluator) {
        m_exprEvaluator = exprEvaluator;
    }

    @Override
    protected void evaluate(Command cmd) throws Exception {
        Extension ext = (Extension) cmd;

        for (Parameter param : ext.getParameters()) {
            Object value = m_exprEvaluator.evaluate(getContext(), param.getValue());
            m_params.put(param.getName(), value);
        }
        
        m_name = ext.getName();
        m_nsUri = ext.getNsUri();
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor = getRuntimeClass()
                .getConstructor(String.class, String.class, Map.class);
        return ctor.newInstance(m_nsUri, m_name, m_params);
    }
}
