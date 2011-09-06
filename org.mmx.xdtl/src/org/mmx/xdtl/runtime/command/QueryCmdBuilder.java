package org.mmx.xdtl.runtime.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;

import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Query;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;
import org.mmx.xdtl.services.UriSchemeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class QueryCmdBuilder extends AbstractCmdBuilder {
    private static final int STREAM_BUF_SIZE = 4096;
    
    private final Logger m_logger = LoggerFactory.getLogger(QueryCmdBuilder.class);
    
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConv;
    private final UriSchemeParser m_uriSchemeParser;
    
    private String m_sqlStatement;
    private String m_queryType;
    private String m_target;
    private Connection m_connection;
    private JdbcConnection m_jdbcConnection;
    
    @Inject
    public QueryCmdBuilder(ExpressionEvaluator exprEval,
            TypeConverter typeConverter, UriSchemeParser uriSchemeParser) {
        m_exprEval = exprEval;
        m_typeConv = typeConverter;
        m_uriSchemeParser = uriSchemeParser;
    }
    
    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor =
                getRuntimeClass().getConstructor(
                        JdbcConnection.class, String.class, String.class);
        
        return ctor.newInstance(m_jdbcConnection, m_sqlStatement, m_target);
    }

    @Override
    protected void evaluate(Command cmd) throws Exception {
        Query qry = (Query) cmd;
        Context ctx = getContext();

        Object obj = m_exprEval.evaluate(ctx, qry.getConnection());
        if (obj != null && !(obj instanceof Connection)) {
            throw new XdtlException("Invalid connection type: '" +
                    obj.getClass().getName() + "'", cmd.getSourceLocator());
        }

        m_connection = (Connection) obj;
        
        String source = m_typeConv.toString(m_exprEval.evaluate(ctx, qry.getSource()));
        if (source == null || source.length() == 0) {
            throw new XdtlException("'source' is mandatory", cmd.getSourceLocator());
        }
        
        String sourceScheme = m_uriSchemeParser.getScheme(source);        
        m_sqlStatement = sourceScheme.isEmpty() ? source : loadQuery(source);
        
        m_queryType = m_typeConv.toString(m_exprEval.evaluate(ctx,
                qry.getQueryType()));

        m_target = m_typeConv.toString(m_exprEval.evaluate(ctx,
                qry.getTarget()));
        
        if (!Query.QUERYTYPE_SQL.equalsIgnoreCase(m_queryType)) {
            throw new XdtlException("Query type '" + m_queryType +
                    "' is not supported", cmd.getSourceLocator());
        }
        
        m_jdbcConnection = ctx.getConnectionManager().getJdbcConnection(
                m_connection); 
    }

    private String loadQuery(String source) {
        m_logger.debug("Loading query from '" + source + "'");
        
        try {
            URL url = new URL(source);
            InputStream is = url.openStream();
            try {
                InputStreamReader reader = new InputStreamReader(is);
                
                StringBuilder builder = new StringBuilder();
                char[] buf = new char[STREAM_BUF_SIZE];
                int numRead;
                
                while ((numRead = reader.read(buf)) != -1) {
                    builder.append(buf, 0, numRead);
                }
                
                return builder.toString();
            } finally {
                close(is);
            }
        } catch (Exception e) {
            throw new XdtlException("Failed to load '" + source + "'", e);
        }
    }

    private void close(InputStream is) {
        try {
            is.close();
        } catch (IOException e) {
            m_logger.warn("Failed to close input stream");
        }
    }
}
