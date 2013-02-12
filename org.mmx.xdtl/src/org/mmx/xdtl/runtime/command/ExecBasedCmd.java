package org.mmx.xdtl.runtime.command;

import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.OsProcessException;
import org.mmx.xdtl.runtime.RuntimeCommand;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Abstract base class for 'exec' based commands. 
 * @author vsi
 */
public abstract class ExecBasedCmd implements RuntimeCommand {
    private OsProcessRunner m_osProcessRunner;
    private OsArgListBuilder m_argListBuilder;
    private boolean m_silentNonZeroExitCode;

    @Override
    public void run(Context context) throws Throwable {
        List<String> args = getArgs(context, m_argListBuilder);
        Logger logger = getLogger();
        logger.info(m_argListBuilder.toCmdline(args));

        int exitValue = m_osProcessRunner.run(args).getExitCode();
        context.assignVariable(Context.VARNAME_XDTL_EXITCODE, exitValue);        
        logger.info("exit code=" + exitValue);

        if (exitValue != 0 && !m_silentNonZeroExitCode) {
            throw new OsProcessException("exit value " + exitValue, exitValue);
        }
    }

    protected abstract List<String> getArgs(Context context, OsArgListBuilder argListBuilder);
    protected abstract Logger getLogger();

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
