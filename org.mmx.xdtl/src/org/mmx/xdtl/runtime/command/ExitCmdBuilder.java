package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.command.Exit;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.RuntimeCommandClassMap;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class ExitCmdBuilder implements CommandBuilder {

    private final ExpressionEvaluator m_exprEvaluator;
    private final TypeConverter m_typeConverter;

    @Inject
    public ExitCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        m_exprEvaluator = exprEvaluator;
        m_typeConverter = typeConverter;
    }

    @Override
    public RuntimeCommand build(Context context,
            RuntimeCommandClassMap rtCmdClassMap, Command cmd) throws Exception {

    	Exit elem = (Exit) cmd;

        int code = elem.getCode() != null ? m_typeConverter.toInteger(m_exprEvaluator.evaluate(context, elem.getCode())) : 0;
        boolean global = elem.getGlobal() != null ? m_typeConverter.toBoolean(m_exprEvaluator.evaluate(context, elem.getGlobal())) : false;
        Constructor<? extends RuntimeCommand> ctor = rtCmdClassMap.getCommandClass(null).getConstructor(int.class, boolean.class);
        return ctor.newInstance(code, global);
    }
}
