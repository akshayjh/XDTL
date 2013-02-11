package org.mmx.xdtl.runtime.command;

import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.runtime.Context;

public class WriteCmd extends ExecBasedCmd {
    private static final Logger logger = Logger.getLogger("xdtl.cmd.write");
    
    private final String m_source;
    private final String m_target;
    private final String m_type;
    private final String m_delimiter;
    private final String m_quote;
    private final String m_encoding;
    private Connection m_connection;

    public WriteCmd(String source, String target, String type,
            boolean overwrite, String delimiter, String quote, String encoding,
            Connection cnn) {

        m_source = source;
        m_target = target;
        m_type = type;
        m_delimiter = delimiter;
        m_quote = quote;
        m_encoding = encoding;
        m_connection = cnn;
    }

    @Override
    protected List<String> getArgs(Context context, OsArgListBuilder argListBuilder) {
        argListBuilder.addVariableEscaped("source", m_source);
        argListBuilder.addVariableEscaped("target", m_target);
        argListBuilder.addVariableEscaped("type", m_type);
        argListBuilder.addVariableEscaped("delimiter", m_delimiter);
        argListBuilder.addVariableEscaped("quote", m_quote);
        argListBuilder.addVariableEscaped("encoding", m_encoding);
        argListBuilder.addVariableEscaped("connection", m_connection.getValue());

        String cmd = (String) context.getVariableValue("write");
        return argListBuilder.build(cmd, true);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
