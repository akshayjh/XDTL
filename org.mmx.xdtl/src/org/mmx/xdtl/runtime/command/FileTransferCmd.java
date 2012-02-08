package org.mmx.xdtl.runtime.command;

import java.util.List;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.OsProcessException;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class FileTransferCmd implements RuntimeCommand {
    private final Logger m_logger = LoggerFactory.getLogger(FileTransferCmd.class);
    private final String m_cmd;
    private final String m_source;
    private final String m_target;
    private final boolean m_overwrite;
    private final String m_cmdName;
    private OsProcessRunner m_osProcessRunner;
    private OsArgListBuilder m_argListBuilder;
    
    public FileTransferCmd(String cmd, String source, String target, boolean overwrite, String cmdName) {
        super();
        m_cmd = cmd;
        m_source = source;
        m_target = target;
        m_overwrite = overwrite;
        m_cmdName = cmdName;
    }

    @Override
    public void run(Context context) throws Exception {
        m_logger.debug(m_cmdName + ": cmd='" + m_cmd +
                "', source='" + m_source +
                "', target='" + m_target +
                "', overwrite='" + m_overwrite + "'");
        
        m_argListBuilder.addVariableEscaped("source", m_source);
        m_argListBuilder.addVariableEscaped("target", m_target);
        m_argListBuilder.addVariable("overwrite", m_overwrite);

        List<String> args = m_argListBuilder.build(m_cmd, true);

        int exitValue = m_osProcessRunner.run(args).getExitCode();
        context.assignVariable(Context.VARNAME_XDTL_EXITCODE, exitValue);
        
        if (exitValue != 0) {
            throw new OsProcessException("'" + m_cmdName + "' failed with exit value " + exitValue, exitValue);
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
