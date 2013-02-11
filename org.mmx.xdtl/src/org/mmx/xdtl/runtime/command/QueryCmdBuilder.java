package org.mmx.xdtl.runtime.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Query;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;
import org.mmx.xdtl.services.UriSchemeParser;

import com.google.inject.Inject;

public class QueryCmdBuilder extends AbstractCmdBuilder {
    private static final int STREAM_BUF_SIZE = 4096;
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    
    private static final Logger logger = Logger.getLogger("xdtl.cmd.query");

    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConv;
    private final UriSchemeParser m_uriSchemeParser;
    private final List<Object> m_params = new ArrayList<Object>();
    
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
                        JdbcConnection.class, String.class, String.class, List.class);
        
        return ctor.newInstance(m_jdbcConnection, m_sqlStatement, m_target, m_params);
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
        
        evaluateParams(qry, ctx);
        
        m_jdbcConnection = ctx.getConnectionManager().getJdbcConnection(
                m_connection); 
    }

    private void evaluateParams(Query qry, Context ctx) {
        for (Parameter param: qry.getParameterList()) {
            String strType = m_typeConv.toString(m_exprEval.evaluate(ctx, param.getType()));
            ParameterType type;
            if (strType == null || strType.length() == 0) {
                type = ParameterType.STRING;
            } else {
                try {
                    type = ParameterType.valueOf(strType.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new XdtlException("Unsupported parameter type: " + strType + ", parameter name=" + param.getName());                
                }
            }

            String strValue = m_typeConv.toString(m_exprEval.evaluate(ctx, param.getValue()));
            Object value = null;
            if (strValue != null) {
                value = convertValue(ctx, param, type, strValue);
            }

            m_params.add(value);
        }
    }


    private Object convertValue(Context ctx, Parameter param, ParameterType type, String strValue) {
        switch (type) {
        case BOOLEAN:
            strValue = strValue.toLowerCase();
            if ("1".equals(strValue) || "Y".equals(strValue) || "T".equals(strValue)) {
                return Boolean.TRUE;
            } else if ("0".equals(strValue) || "N".equals(strValue) || "F".equals(strValue)) {
                return Boolean.FALSE;
            } else {
                throw new XdtlException("Invalid boolean parameter value: " + strValue + ", parameter name=" + param.getName());
            }
        case BYTE:
            return Byte.parseByte(strValue);
        case SHORT:
            return Short.parseShort(strValue);
        case INT:
            return Integer.parseInt(strValue);
        case LONG:
            return Long.parseLong(strValue);
        case DOUBLE:
            return Double.parseDouble(strValue);
        case FLOAT:
            return Float.parseFloat(strValue);
        case DATE:
            String pattern = m_typeConv.toString(m_exprEval.evaluate(ctx, param.getFormat()));
            if (pattern == null || pattern.length() == 0) pattern = DEFAULT_DATE_PATTERN;
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            try {
                return new java.sql.Date(format.parse(strValue).getTime());
            } catch (ParseException e) {
                throw new XdtlException("Unable to convert string to date: date=" + strValue + ", format=" + pattern);
            }
        default:
            return strValue;
        }
    }

    private String loadQuery(String source) {
        logger.trace("Loading query from '" + source + "'");
        
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
            logger.warn("Failed to close input stream");
        }
    }
    
    private enum ParameterType {
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        DOUBLE,
        FLOAT,
        DATE,
        STRING
    };
}
