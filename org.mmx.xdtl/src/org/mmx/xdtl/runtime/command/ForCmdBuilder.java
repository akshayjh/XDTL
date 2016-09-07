/**
 *
 */
package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.script.Bindings;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.For;
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
public class ForCmdBuilder implements CommandBuilder {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.for");
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
    public RuntimeCommand build(Context context,
            RuntimeCommandClassMap rtCmdClassMap, Command cmd) throws Exception {
        For elem = (For) cmd;
        String itemVarName = getVarName(context, elem.getItemVarName(), null);
        String indexVarName = getVarName(context, elem.getIndexVarName(), Context.VARNAME_XDTL_ROWID);
        String countVarName = getVarName(context, elem.getCountVarName(), Context.VARNAME_XDTL_ROWCOUNT);

        Object obj = m_exprEval.evaluate(context, elem.getIterable());
        if (obj != null) {
            logger.debug("Iterable is of class=" + obj.getClass().getName());

            if (obj instanceof Bindings) {
                try {
                    obj = castBindingsObject((Bindings)obj);
                } catch (Throwable e) {
                    logger.debug("Failed to convert Bindings object", e);
                }
            }

            if (obj instanceof Object[]) {
                obj = new ArrayList<Object>(Arrays.asList((Object[])obj));
            }

            if (!(obj instanceof Iterable)) {
                throw new XdtlException(
                        String.format("'%s' must resolve to iterable object",
                                elem.getIterable()), elem.getSourceLocator());
            }
        }

        Class<? extends RuntimeCommand> rtCmdClass = rtCmdClassMap.getCommandClass(null);
        Constructor<? extends RuntimeCommand> ctor = rtCmdClass.getConstructor(String.class,
                String.class, String.class, Iterable.class, CommandList.class);
        return ctor.newInstance(itemVarName, indexVarName, countVarName,
                obj, elem.getCommandList());
    }

    /**
     * Safe way to get collection from ScriptObjectMirror (i.e. it will also work with Java 1.7)
     */
    private Object castBindingsObject(Bindings obj) throws Throwable {
        Class<?> cls = Class.forName("jdk.nashorn.api.scripting.ScriptObjectMirror");
        if (!cls.isAssignableFrom(obj.getClass())) return obj;

        Object isArray = cls.getMethod("isArray").invoke(obj);
        if (isArray == null || isArray.equals(false)) return obj;

        Object values = cls.getMethod("values").invoke(obj);
        if (!(values instanceof Collection<?>)) return obj;
        return values;
    }

    private String getVarName(Context context, String name, String defltName) {
        String result = m_typeConv.toString(m_exprEval.evaluate(context, name));
        if (result == null || result.length() == 0) {
            result = defltName;
        }

        return result;
    }


}
