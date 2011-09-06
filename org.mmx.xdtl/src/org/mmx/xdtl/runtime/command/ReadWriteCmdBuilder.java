package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.ReadWrite;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public abstract class ReadWriteCmdBuilder extends AbstractCmdBuilder {
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConv;
    
    private String m_source;
    private String m_target;
    private String m_type;
    private boolean m_overwrite;
    private String m_delimiter;
    private String m_quote;
    private String m_encoding;
    private Connection m_connection;

    @Inject
    public ReadWriteCmdBuilder(ExpressionEvaluator exprEval,
            TypeConverter typeConverter) {
        m_exprEval = exprEval;
        m_typeConv = typeConverter;
    }
    
    @Override
    protected void evaluate(Command obj) throws Exception {
        ReadWrite cmd = (ReadWrite) obj;
        Context ctx = getContext();

        m_connection = null;
        
        if (cmd.getConnection().length() > 0) {
            Object cnnObj = m_exprEval.evaluate(ctx, cmd.getConnection());
            if (cnnObj != null && !(cnnObj instanceof Connection)) {
                throw new XdtlException("Invalid connection type: '" + cnnObj.getClass() + "'");
            }

            m_connection = (Connection) cnnObj;
        }
        
        m_source = m_typeConv.toString(m_exprEval.evaluate(ctx, cmd.getSource()));
        m_target = m_typeConv.toString(m_exprEval.evaluate(ctx, cmd.getTarget()));
        m_type = m_typeConv.toString(m_exprEval.evaluate(ctx, cmd.getType()));
        m_overwrite = m_typeConv.toBoolean(m_exprEval.evaluate(ctx, cmd.getOverwrite()));
        m_delimiter = m_typeConv.toString(m_exprEval.evaluate(ctx, cmd.getDelimiter()));
        m_quote = m_typeConv.toString(m_exprEval.evaluate(ctx, cmd.getQuote()));
        m_encoding = m_typeConv.toString(m_exprEval.evaluate(ctx, cmd.getEncoding()));
    }

    protected String getSource() {
        return m_source;
    }

    protected String getTarget() {
        return m_target;
    }

    protected String getType() {
        return m_type;
    }

    protected boolean isOverwrite() {
        return m_overwrite;
    }

    protected String getDelimiter() {
        return m_delimiter;
    }

    protected String getQuote() {
        return m_quote;
    }

    protected Connection getConnection() {
        return m_connection;
    }

    protected ExpressionEvaluator getExprEval() {
        return m_exprEval;
    }

    protected TypeConverter getTypeConv() {
        return m_typeConv;
    }

    protected String getEncoding() {
        return m_encoding;
    }
}
