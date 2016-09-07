package org.mmx.xdtl.runtime.command;

import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.runtime.Context;

public class ReadCmd extends ExecBasedCmd {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.read");

    private final Object m_source;
    private final String m_target;
    private final String m_type;
    private final String m_delimiter;
    private final String m_quote;
    private final Connection m_connection;
    private final String m_errors;
    private final String m_encoding;
    private final String m_escape;
    private final boolean m_header;
    private final int m_skip;
    private final int m_batch;
    private final boolean m_overwrite;

    public ReadCmd(Object source, String target, String type,
            boolean overwrite, String delimiter, String quote, String encoding, String escape,
            Connection cnn, String errors, boolean header, int skip,
            int batch) {

        m_source = source;
        m_target = target;
        m_type = type;
        m_delimiter = delimiter;
        m_quote = quote;
        m_encoding = encoding;
        m_escape = escape;
        m_connection = cnn;
        m_errors = errors;
        m_header = header;
        m_skip = skip;
        m_batch = batch;
        m_overwrite = overwrite;
    }

    @Override
    protected List<String> getArgs(Context context, OsArgListBuilder argListBuilder) {
        argListBuilder.addVariableEscaped("source", (String) m_source);
        argListBuilder.addVariableEscaped("target", m_target);
        argListBuilder.addVariableEscaped("type", m_type);
        argListBuilder.addVariableEscaped("delimiter", m_delimiter);
        argListBuilder.addVariableEscaped("quote", m_quote);
        argListBuilder.addVariableEscaped("encoding", m_encoding);
        argListBuilder.addVariableEscaped("escape", m_escape);
        argListBuilder.addVariableEscaped("connection", m_connection.getValue());
        argListBuilder.addVariableEscaped("errors", m_errors);
        argListBuilder.addVariable("header", m_header);
        argListBuilder.addVariable("skip", m_skip);
        argListBuilder.addVariable("batch", m_batch);
        argListBuilder.addVariable("overwrite", m_overwrite);

        String cmd = (String) context.getVariableValue("read");
        return argListBuilder.build(cmd, true);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
