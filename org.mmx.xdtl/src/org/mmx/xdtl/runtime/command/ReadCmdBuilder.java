/**
 * 
 */
package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.command.Read;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

/**
 * @author vsi
 */
public class ReadCmdBuilder extends ReadWriteCmdBuilder {
    private String m_errors;
    private boolean m_header;
    private int m_rowOffset;
    private int m_batch;
    
    @Inject
    public ReadCmdBuilder(ExpressionEvaluator exprEval,
            TypeConverter typeConverter) {
        super(exprEval, typeConverter);
    }

    /**
     * @see org.mmx.xdtl.runtime.command.AbstractCmdBuilder#createInstance()
     */
    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor =
            getRuntimeClass().getConstructor(String.class, String.class,
                    String.class, boolean.class, String.class, String.class,
                    String.class, Connection.class, String.class, boolean.class,
                    int.class, int.class);

        RuntimeCommand rtCmd = ctor.newInstance(getSource(), getTarget(),
                getType(), isOverwrite(), getDelimiter(), getQuote(),
                getEncoding(), getConnection(), m_errors, m_header, m_rowOffset,
                m_batch);
    
        return rtCmd;
    }

    @Override
    protected void evaluate(Command obj) throws Exception {
        super.evaluate(obj);
        
        Context ctx = getContext();
        Read cmd = (Read) obj;
        
        TypeConverter typeConv = getTypeConv();
        ExpressionEvaluator exprEval = getExprEval();
        
        m_errors = typeConv.toString(exprEval.evaluate(ctx, cmd.getErrors()));
        m_header = typeConv.toBoolean(exprEval.evaluate(ctx, cmd.getHeader()));
        m_rowOffset = typeConv.toInteger(exprEval.evaluate(ctx, cmd.getSkip()));
        m_batch = typeConv.toInteger(exprEval.evaluate(ctx, cmd.getBatch()));
    }
}
