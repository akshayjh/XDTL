/**
 * 
 */
package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.model.command.If;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

/**
 * @author vsi
 *
 */
public class IfCmdBuilder implements CommandBuilder {
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConv;

    @Inject
    public IfCmdBuilder(ExpressionEvaluator exprEval, TypeConverter typeConv) {
        super();
        m_exprEval = exprEval;
        m_typeConv = typeConv;
    }

    /**
     * @see org.mmx.xdtl.runtime.CommandBuilder#build(org.mmx.xdtl.runtime.Context, java.lang.Class, org.mmx.xdtl.model.Command)
     */
    @Override
    public <T extends RuntimeCommand> RuntimeCommand build(Context context,
            Class<T> runtimeClass, Command cmd) throws Exception {
        
        If elem = (If) cmd;
        Boolean condition = m_typeConv.toBoolean(m_exprEval.evaluate(context, elem.getExpr()));
        if (condition == null) {
            condition = Boolean.FALSE;
        }
        
        Constructor<T> ctor = runtimeClass.getConstructor(boolean.class, CommandList.class);
        return ctor.newInstance(condition, elem.getCommandList());
    }
}
