package org.mmx.xdtl.runtime.command;

import java.util.HashMap;
import java.util.Map;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.command.Call;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;

import com.google.inject.Inject;

public class CallCmdBuilder extends AbstractCmdBuilder {
    private final ExpressionEvaluator m_exprEvaluator;

    private Map<String, Object> m_args = new HashMap<String, Object>();
    private String m_ref;
    
    @Inject
    public CallCmdBuilder(ExpressionEvaluator exprEvaluator) {
        m_exprEvaluator = exprEvaluator;
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        return getRuntimeClass().getConstructor(String.class, Map.class).
                newInstance(m_ref, m_args);
    }

    @Override
    protected void evaluate(Command cmd) {
        Call call = (Call) cmd;

        for (Parameter param : call.getParameters()) {
            Object value = m_exprEvaluator.evaluate(getContext(), param.getValue());
            m_args.put(param.getName(), value);
        }
        
        m_ref = (String) m_exprEvaluator.evaluate(getContext(), call.getRef());
    }    
}
