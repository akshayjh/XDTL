package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.command.Error;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.RuntimeCommandClassMap;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class ErrorCmdBuilder implements CommandBuilder {
    private final ExpressionEvaluator m_exprEvaluator;
    private final TypeConverter m_typeConverter;

    @Inject
    public ErrorCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        m_exprEvaluator = exprEvaluator;
        m_typeConverter = typeConverter;
    }

    @Override
    public RuntimeCommand build(Context context,
            RuntimeCommandClassMap rtCmdClassMap, Command cmd) throws Exception {

        Error elem = (Error) cmd;
        String code = m_typeConverter.toString(m_exprEvaluator.evaluate(context, elem.getCode()));
        String msg = elem.getMsg() != null ? m_typeConverter.toString(m_exprEvaluator.evaluate(context, elem.getMsg())) : "";
        Class<? extends RuntimeCommand> rtCmdClass = rtCmdClassMap.getCommandClass(null);
        Constructor<? extends RuntimeCommand> ctor = rtCmdClass.getConstructor(String.class, String.class);
        return ctor.newInstance(code, msg);
    }
}
