package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Fetch;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class FetchCmdBuilder extends AbstractCmdBuilder {
    private static char DEFAULT_DELIMITER = ',';
    private static char DEFAULT_QUOTE = '"';
    
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConverter;
    private String m_source;
    private String m_target;
    private char m_delimiter;
    private char m_quote;
    private Fetch.Type m_type;
    private JdbcConnection m_connection;
    private JdbcConnection m_destination;
    private boolean m_overwrite;
    private String m_rowset;
    private String m_encoding;
    
    @Inject
    public FetchCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        super();
        m_exprEval = exprEvaluator;
        m_typeConverter = typeConverter;
    }

    @Override
    protected void evaluate(Command cmd) throws Exception {
        Fetch fetch = (Fetch) cmd;
        Context ctx = getContext();
        m_source    = m_typeConverter.toString(m_exprEval.evaluate(ctx, fetch.getSource()));
        m_target    = m_typeConverter.toString(m_exprEval.evaluate(ctx, fetch.getTarget()));
        m_encoding  = m_typeConverter.toString(m_exprEval.evaluate(ctx, fetch.getEncoding()));
    
        Character delimiter = m_typeConverter.toChar(m_exprEval.evaluate(ctx, fetch.getDelimiter()));
        m_delimiter = (delimiter == null) ? DEFAULT_DELIMITER : delimiter;
        
        Character quote = m_typeConverter.toChar(m_exprEval.evaluate(ctx, fetch.getQuote()));
        m_quote = (quote == null) ? DEFAULT_QUOTE : quote;
        
        m_type      = m_typeConverter.toEnumMember(Fetch.Type.class, m_exprEval.evaluate(ctx, fetch.getType()));
        m_overwrite = m_typeConverter.toBoolean(m_exprEval.evaluate(ctx, fetch.getOverwrite()));
        m_rowset    = m_typeConverter.toString(m_exprEval.evaluate(ctx, fetch.getRowset()));

        if (m_target != null && m_target.length() == 0) {
            m_target = null;
        }
                
        if (m_rowset != null && m_rowset.length() == 0) {
            m_rowset = null; 
        }
        
        if (m_target == null && m_rowset == null) {
            throw new XdtlException("Either 'target' or 'rowset' must be specified");
        }
        
        Object cnnObj = m_exprEval.evaluate(ctx, fetch.getConnection());
        if (cnnObj != null && !(cnnObj instanceof Connection)) {
            throw new XdtlException("Invalid connection type: '" + cnnObj.getClass() + "'");
        }

        m_connection = ctx.getConnectionManager().getJdbcConnection((Connection) cnnObj);

        cnnObj = m_exprEval.evaluate(ctx, fetch.getDestination());
        if (cnnObj != null) {
            if (!(cnnObj instanceof Connection)) {
                throw new XdtlException("Invalid destination connection type: '" + cnnObj.getClass() + "'");
            }
            
            m_destination = ctx.getConnectionManager().getJdbcConnection((Connection) cnnObj);
        }
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor = getRuntimeClass()
                .getConstructor(String.class, JdbcConnection.class,
                        Fetch.Type.class, boolean.class, char.class,
                        char.class, String.class, String.class, String.class,
                        JdbcConnection.class);
        
        return ctor.newInstance(m_source, m_connection, m_type, m_overwrite,
                m_delimiter, m_quote, m_target, m_rowset, m_encoding,
                m_destination);
    }
}
