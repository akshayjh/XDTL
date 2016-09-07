package org.mmx.xdtl.runtime.command.sleep;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.command.Sleep;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;
import org.mmx.xdtl.runtime.command.CmdBuilderBase;

import com.google.inject.Inject;

public class SleepCmdBuilder extends CmdBuilderBase {
    private int m_seconds;

    @Inject
    public SleepCmdBuilder(ExpressionEvaluator exprEval,
            TypeConverter typeConv) {
        super(exprEval, typeConv);
    }

    @Override
    protected void evaluate(Command elem) throws Exception {
        Sleep cmd = (Sleep) elem;
        m_seconds = evalToInteger(cmd.getSeconds());
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor =
                getRuntimeClass().getConstructor(int.class);

        return ctor.newInstance(m_seconds);
    }
}
