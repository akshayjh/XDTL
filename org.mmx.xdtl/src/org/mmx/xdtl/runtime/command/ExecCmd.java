package org.mmx.xdtl.runtime.command;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.OsProcessException;
import org.mmx.xdtl.runtime.RuntimeCommand;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ExecCmd implements RuntimeCommand {
    private static final Logger logger = Logger.getLogger("xdtl.cmd.exec");
    private final String m_cmd;
    private final String m_targetVarName;
    
    private OsProcessRunner m_processRunner;
    private OsArgListBuilder m_argumentListBuilder;
    private boolean m_silentNonZeroExitCode;
    
    public ExecCmd(String shell, String cmd) {
    	this(shell, cmd, null);
    }
    
    public ExecCmd(String shell, String cmd, String targetVarName)
    {
        m_cmd = cmd;
    	m_targetVarName = targetVarName;
    }
    
    @Override
    public void run(Context context) throws Exception {
        List<String> args = m_argumentListBuilder.build(m_cmd, false);
        if (logger.isDebugEnabled()) {
            logger.debug(m_argumentListBuilder.toCmdline(args));
        } else if (m_targetVarName == null){
            logger.info(m_argumentListBuilder.toCmdline(args));
        }

        OsRunnerResult result = m_processRunner.run(args);
        
        if (m_targetVarName != null) {
        	context.assignVariable(m_targetVarName, result.getOutput());
        } else {
            logger.log(logger.isDebugEnabled() ? Level.DEBUG : Level.INFO,
                    "exitCode=" + result.getExitCode());
        }

        context.assignVariable(Context.VARNAME_XDTL_EXITCODE, result.getExitCode());
        
        if (result.getExitCode() != 0 && !m_silentNonZeroExitCode) {
       		throw new OsProcessException("Process " + args.get(0) + " failed with exit code=" + result.getExitCode(), result.getExitCode());
        }
    }

    public OsProcessRunner getProcessRunner() {
        return m_processRunner;
    }

    @Inject
    public void setProcessRunner(OsProcessRunner processRunner) {
        m_processRunner = processRunner;
    }

    public OsArgListBuilder getArgumentListBuilder() {
        return m_argumentListBuilder;
    }

    @Inject
    public void setArgumentListBuilder(OsArgListBuilder cmdLineBuilder) {
        m_argumentListBuilder = cmdLineBuilder;
    }
    
    @Inject
    protected void setSilentNonZeroExitCode(@Named("errors.silentexitcode") boolean value) {
    	m_silentNonZeroExitCode = value;
    }
}
