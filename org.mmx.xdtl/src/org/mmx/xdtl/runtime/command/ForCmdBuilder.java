/**
 * 
 */
package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.For;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

/**
 * @author vsi
 */
public class ForCmdBuilder implements CommandBuilder {
    private static final Logger logger = Logger.getLogger(ForCmdBuilder.class);
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConv;

    @Inject
    public ForCmdBuilder(ExpressionEvaluator exprEval, TypeConverter typeConv) {
        super();
        m_exprEval = exprEval;
        m_typeConv = typeConv;
    }

    /**
     * @see org.mmx.xdtl.runtime.CommandBuilder#build(org.mmx.xdtl.runtime.Context,
     *      java.lang.Class, org.mmx.xdtl.model.Command)
     */
    @Override
    public <T extends RuntimeCommand> RuntimeCommand build(Context context,
            Class<T> runtimeClass, Command cmd) throws Exception {
        For elem = (For) cmd;
        String itemVarName = getVarName(context, elem.getItemVarName(), null);
        String indexVarName = getVarName(context, elem.getIndexVarName(), Context.VARNAME_XDTL_ROWID);
        String countVarName = getVarName(context, elem.getCountVarName(), Context.VARNAME_XDTL_ROWCOUNT);

        Object obj = m_exprEval.evaluate(context, elem.getIterable());
        if (obj != null) {
            logger.debug("Iterable is of class=" + obj.getClass().getName());
        }

        if (!(obj instanceof Iterable)) {
            throw new XdtlException(
                    String.format("'%s' must resolve to iterable object",
                            elem.getIterable()), elem.getSourceLocator());
        }

        Constructor<T> ctor = runtimeClass.getConstructor(String.class,
                String.class, String.class, Iterable.class, CommandList.class);
        return ctor.newInstance(itemVarName, indexVarName, countVarName,
                (Iterable<?>) obj, elem.getCommandList());
    }
    
    private String getVarName(Context context, String name, String defltName) {
        String result = m_typeConv.toString(m_exprEval.evaluate(context, name));
        if (result == null || result.length() == 0) {
            result = defltName;
        }
        
        return result;
    }
}
