package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.command.Send;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class SendCmdBuilder implements CommandBuilder {
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConv;

    @Inject
    public SendCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        m_exprEval = exprEvaluator;
        m_typeConv = typeConverter;
    }

    @Override
    public <T extends RuntimeCommand> RuntimeCommand build(Context context,
            Class<T> runtimeClass, Command cmd) throws Exception {
        
        Send elem = (Send) cmd;
        String source = m_typeConv.toString(m_exprEval.evaluate(context, elem.getSource()));
        String target = m_typeConv.toString(m_exprEval.evaluate(context, elem.getTarget()));
        Boolean overwrite = m_typeConv.toBoolean(m_exprEval.evaluate(context, elem.getOverwrite()));
        
        Constructor<T> ctor = runtimeClass.getConstructor(String.class, String.class, Boolean.class);
        return ctor.newInstance(source, target, overwrite);
    }
}
