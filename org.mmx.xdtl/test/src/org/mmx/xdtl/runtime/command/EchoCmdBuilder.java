package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.command.Echo;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class EchoCmdBuilder implements CommandBuilder {
    private final ExpressionEvaluator m_exprEvaluator;

    public EchoCmdBuilder(ExpressionEvaluator exprEvaluator) {
        m_exprEvaluator = exprEvaluator;
    }

    @Override
    public <T extends RuntimeCommand> RuntimeCommand build(Context context,
            Class<T> runtimeClass, Command cmd) throws Exception {

        Echo echo = (Echo) cmd;

        Constructor<T> ctor = runtimeClass.getConstructor(String.class);

        Object message = m_exprEvaluator.evaluate(context, echo.getMessage());
        return (RuntimeCommand) ctor.newInstance(message.toString());
    }
}
