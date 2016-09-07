package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Call;
import org.mmx.xdtl.runtime.Argument;
import org.mmx.xdtl.runtime.ArgumentMap;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;
import org.mmx.xdtl.runtime.util.VariableNameValidator;

import com.google.inject.Inject;

public class CallCmdBuilder extends CmdBuilderBase {
    private ArgumentMap m_args = new ArgumentMap();
    private String m_ref;
    private VariableNameValidator m_varNameValidator;

    @Inject
    public CallCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter, VariableNameValidator varNameValidator) {
        super(exprEvaluator, typeConverter);
        m_varNameValidator = varNameValidator;
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        return getRuntimeClass().getConstructor(String.class, ArgumentMap.class).
                newInstance(m_ref, m_args);
    }

    @Override
    protected void evaluate(Command cmd) {
        Call call = (Call) cmd;

        for (Parameter param : call.getParameters()) {
            String name = evalToString(param.getName());
            if (!m_varNameValidator.isValidVariableName(name)) {
                throw new XdtlException("Invalid parameter name: '" + name + "'",
                        param.getSourceLocator());
            }

            Object value = eval(param.getValue());
            boolean nolog = evalToBoolean(param.getNoLog(), false);
            m_args.put(name, new Argument(value, nolog));
        }

        m_ref = (String) eval(call.getRef());
    }
}
