/**
 * 
 */
package org.mmx.xdtl.runtime.command;

import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.OsProcessException;
import org.mmx.xdtl.runtime.RuntimeCommand;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author vsi
 *
 */
public class ClearCmd implements RuntimeCommand {
    private static final Logger logger = Logger.getLogger(ClearCmd.class);

    private final String m_cmd;
    private final String m_target;
    
    private OsProcessRunner m_osProcessRunner;
    private OsArgListBuilder m_argListBuilder;
    private boolean m_silentNonZeroExitCode;
    
    public ClearCmd(String cmd, String target) {
        super();
        m_cmd = cmd;
        m_target = target;
    }

    /**
     * @see org.mmx.xdtl.runtime.RuntimeCommand#run(org.mmx.xdtl.runtime.Context)
     */
    @Override
    public void run(Context context) throws Throwable {
        logger.debug(String.format("clear: cmd='%s', target='%s'",
                m_cmd, m_target));
        
        m_argListBuilder.addVariableEscaped("target", m_target);

        List<String> args = m_argListBuilder.build(m_cmd, true);

        int exitValue = m_osProcessRunner.run(args).getExitCode();
        context.assignVariable(Context.VARNAME_XDTL_EXITCODE, exitValue);
        
        if (exitValue != 0 && !m_silentNonZeroExitCode) {
            throw new OsProcessException("'clear' failed with exit value " + exitValue, exitValue);
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
