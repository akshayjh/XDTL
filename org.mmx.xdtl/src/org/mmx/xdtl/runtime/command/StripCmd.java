/**
 * 
 */
package org.mmx.xdtl.runtime.command;

import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.runtime.Context;

/**
 * Cleanse a text file, stripping excess data eg. with stream utility.
 * 
 * @author vsi
 */
public class StripCmd extends ExecBasedCmd {
    private static final Logger logger = Logger.getLogger("xdtl.cmd.strip");
    
    private final String  m_cmd;
    private final String  m_source;
    private final String  m_target;
    private final boolean m_overwrite;
    private final String  m_expr;

    public StripCmd(String cmd, String source, String target,
            boolean overwrite, String expr) {
        super();
        m_cmd = cmd;
        m_source = source;
        m_target = target;
        m_overwrite = overwrite;
        m_expr = expr;
    }

    @Override
    protected List<String> getArgs(Context context, OsArgListBuilder argListBuilder) {
        argListBuilder.addVariableEscaped("source", m_source);
        argListBuilder.addVariableEscaped("target", m_target);
        argListBuilder.addVariable("overwrite", m_overwrite);
        argListBuilder.addVariableEscaped("expr", m_expr);
        return argListBuilder.build(m_cmd, true);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
