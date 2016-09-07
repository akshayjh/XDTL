/**
 *
 */
package org.mmx.xdtl.runtime.command;

import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.runtime.Context;

/**
 * @author vsi
 */
public class ClearCmd extends ExecBasedCmd {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.clear");

    private final String m_cmd;
    private final String m_target;

    public ClearCmd(String cmd, String target) {
        super();
        m_cmd = cmd;
        m_target = target;
    }

    @Override
    protected List<String> getArgs(Context context, OsArgListBuilder argListBuilder) {
        argListBuilder.addVariableEscaped("target", m_target);
        return argListBuilder.build(m_cmd, true);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
