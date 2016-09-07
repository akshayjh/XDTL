/**
 *
 */
package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Log;
import org.mmx.xdtl.model.command.Log.Level;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.RuntimeCommandClassMap;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

/**
 * @author vsi
 */
public class LogCmdBuilder implements CommandBuilder {

    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConv;

    @Inject
    public LogCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        m_exprEval = exprEvaluator;
        m_typeConv = typeConverter;
    }

    /**
     * @see org.mmx.xdtl.runtime.CommandBuilder#build(org.mmx.xdtl.runtime.Context, java.lang.Class, org.mmx.xdtl.model.Command)
     */
    @Override
    public RuntimeCommand build(Context ctx,
            RuntimeCommandClassMap rtCmdClassMap, Command cmd) throws Exception {

        Log log = (Log) cmd;
        String strLevel = m_typeConv.toString(m_exprEval.evaluate(ctx, log.getLevel()));
        Level level = toLogLevel(strLevel);

        String msg = m_typeConv.toString(m_exprEval.evaluate(ctx, log.getMsg()));

        Class<? extends RuntimeCommand> rtCmdClass = rtCmdClassMap.getCommandClass(null);
        Constructor<? extends RuntimeCommand> ctor = rtCmdClass.getConstructor(Level.class, String.class);
        return ctor.newInstance(level, msg);
    }

    private Level toLogLevel(String strLevel) {
        if (strLevel == null || strLevel.length() == 0) {
            return Level.INFO;
        }

        try {
            return Level.valueOf(strLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new XdtlException("Invalid log level: '" + strLevel + "'");
        }
    }
}
