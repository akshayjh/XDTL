package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.command.Strip;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class StripCmdBuilder extends AbstractCmdBuilder {
    private static final String CMD_NAME = "strip";
    
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConv;

    private String  m_cmd;
    private String  m_source;
    private String  m_target;
    private boolean m_overwrite;
    private String  m_expr;
    
    @Inject
    public StripCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        m_exprEval = exprEvaluator;
        m_typeConv = typeConverter;
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor =
            getRuntimeClass().getConstructor(String.class, String.class,
                    String.class, boolean.class, String.class);
        
        return ctor.newInstance(m_cmd, m_source, m_target, m_overwrite,
                m_expr);
    }

    @Override
    protected void evaluate(Command elem) throws Exception {
        Strip cmd = (Strip) elem;
        Context ctx = getContext();
        
        m_cmd = cmd.getCmd();
        if (m_cmd == null || m_cmd.length() == 0) {
            m_cmd = m_typeConv.toString(ctx.getVariableValue(CMD_NAME));
        } else {
            m_cmd = m_typeConv.toString(m_exprEval.evaluate(ctx, m_cmd));
        }

        m_source = m_typeConv.toString(m_exprEval.evaluate(ctx, cmd.getSource()));
        m_target = m_typeConv.toString(m_exprEval.evaluate(ctx, cmd.getTarget()));
        m_overwrite = m_typeConv.toBoolean(m_exprEval.evaluate(ctx, cmd.getOverwrite()));        
        m_expr = m_typeConv.toString(m_exprEval.evaluate(ctx, cmd.getExpr()));
    }
}
