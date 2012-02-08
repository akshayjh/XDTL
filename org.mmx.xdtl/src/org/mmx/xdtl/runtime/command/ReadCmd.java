package org.mmx.xdtl.runtime.command;

import java.util.List;

import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.OsProcessException;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ReadCmd implements RuntimeCommand {
    private final Logger m_logger = LoggerFactory.getLogger(ReadCmd.class);
    
    private final Object m_source;
    private final String m_target;
    private final String m_type;
    private final String m_delimiter;
    private final String m_quote;
    private final Connection m_connection;
    private final String m_errors;
    private final String m_encoding;
    private final boolean m_header;
    private final int m_rowOffset;
    private final int m_batch;

    private OsProcessRunner m_osProcessRunner;
    private OsArgListBuilder m_argListBuilder;

    public ReadCmd(Object source, String target, String type,
            boolean overwrite, String delimiter, String quote, String encoding,
            Connection cnn, String errors, boolean header, int rowOffset,
            int batch) {

        m_source = source;
        m_target = target;
        m_type = type;
        m_delimiter = delimiter;
        m_quote = quote;
        m_encoding = encoding;
        m_connection = cnn;
        m_errors = errors;
        m_header = header;
        m_rowOffset = rowOffset;
        m_batch = batch;
    }

    @Override
    public void run(Context context) throws Throwable {
        m_logger.info(String.format(
                    "read: source='%s', target='%s', " +
                    "type='%s', delimiter='%s', quote='%s', encoding='%s', " +
                    "connection='%s', errors='%s', header='%s', skip=%d, batch=%d",
                    m_source, m_target,
                    m_type, m_delimiter, m_quote, m_encoding, m_connection,
                    m_errors, m_header, m_rowOffset, m_batch));
        
        m_argListBuilder.addVariableEscaped("source", (String) m_source);
        m_argListBuilder.addVariableEscaped("target", m_target);
        m_argListBuilder.addVariableEscaped("type", m_type);
        m_argListBuilder.addVariableEscaped("delimiter", m_delimiter);
        m_argListBuilder.addVariableEscaped("quote", m_quote);
        m_argListBuilder.addVariableEscaped("encoding", m_encoding);
        m_argListBuilder.addVariableEscaped("connection", m_connection.getValue());
        m_argListBuilder.addVariableEscaped("errors", m_errors);
        m_argListBuilder.addVariable("header", m_header);
        m_argListBuilder.addVariable("skip", m_rowOffset);
        m_argListBuilder.addVariable("batch", m_batch);

        String cmd = (String) context.getVariableValue("read");
        List<String> args = m_argListBuilder.build(cmd, true);

        int exitValue = m_osProcessRunner.run(args).getExitCode();
        context.assignVariable(Context.VARNAME_XDTL_EXITCODE, exitValue);
        
        if (exitValue != 0) {
            throw new OsProcessException("'read' failed with exit value " +
                    exitValue, exitValue);
        }
    }

    public OsProcessRunner getOsProcessRunner() {
        return m_osProcessRunner;
    }

    @Inject
    public void setOsProcessRunner(OsProcessRunner osProcessRunner) {
        m_osProcessRunner = osProcessRunner;
    }

    public OsArgListBuilder getArgListBuilder() {
        return m_argListBuilder;
    }

    @Inject
    public void setArgListBuilder(OsArgListBuilder argListBuilder) {
        m_argListBuilder = argListBuilder;
    }
}
