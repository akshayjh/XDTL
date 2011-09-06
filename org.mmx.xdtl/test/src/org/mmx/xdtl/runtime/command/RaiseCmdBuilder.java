package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.command.Raise;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class RaiseCmdBuilder implements CommandBuilder {
    private final ExpressionEvaluator m_exprEvaluator;

    public RaiseCmdBuilder(ExpressionEvaluator exprEvaluator) {
        m_exprEvaluator = exprEvaluator;
    }

    @Override
    public <T extends RuntimeCommand> RuntimeCommand build(Context context,
            Class<T> rtClass, Command cmd) throws Exception {

        Raise raise = (Raise) cmd;

        String exception = (String) m_exprEvaluator.evaluate(context,
                raise.getException());
        Constructor<T> ctor = rtClass.getConstructor(String.class);
        return (RuntimeCommand) ctor.newInstance(exception);
    }
}
