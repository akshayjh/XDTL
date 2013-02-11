package org.mmx.xdtl.runtime.command;

import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.OsProcessException;
import org.mmx.xdtl.runtime.RuntimeCommand;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class WriteCmd implements RuntimeCommand {
    private static final Logger logger = Logger.getLogger(WriteCmd.class);
    
    private final String m_source;
    private final String m_target;
    private final String m_type;
    private final String m_delimiter;
    private final String m_quote;
    private final String m_encoding;
    private Connection m_connection;

    private OsProcessRunner m_osProcessRunner;
    private OsArgListBuilder m_argListBuilder;
    private boolean m_silentNonZeroExitCode;

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
    public void run(Context context) throws Throwable {
        logger.info(String.format(
                "write: source='%s', target='%s', " +
                "type='%s', delimiter='%s', quote='%s', connection='%s'",
                m_source, m_target,
                m_type, m_delimiter, m_quote, m_connection));
        
        m_argListBuilder.addVariableEscaped("source", m_source);
        m_argListBuilder.addVariableEscaped("target", m_target);
        m_argListBuilder.addVariableEscaped("type", m_type);
        m_argListBuilder.addVariableEscaped("delimiter", m_delimiter);
        m_argListBuilder.addVariableEscaped("quote", m_quote);
        m_argListBuilder.addVariableEscaped("encoding", m_encoding);
        m_argListBuilder.addVariableEscaped("connection", m_connection.getValue());

        String cmd = (String) context.getVariableValue("write");
        List<String> args = m_argListBuilder.build(cmd, true);

        int exitValue = m_osProcessRunner.run(args).getExitCode();
        context.assignVariable(Context.VARNAME_XDTL_EXITCODE, exitValue);
        
        if (exitValue != 0 && !m_silentNonZeroExitCode) {
            throw new OsProcessException("'write' failed with exit value " + exitValue, exitValue);
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
    
    @Inject
    protected void setSilentNonZeroExitCode(@Named("errors.silentexitcode") boolean value) {
    	m_silentNonZeroExitCode = value;
    }
}
