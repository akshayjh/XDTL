package org.mmx.xdtl.runtime.command;

import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.runtime.Context;

public class FileTransferCmd extends ExecBasedCmd {
    private final String m_cmd;
    private final String m_source;
    private final String m_target;
    private final boolean m_overwrite;
    private final String m_options;
    private final String m_cmdName;

    public FileTransferCmd(String cmd, String source, String target, boolean overwrite, String options, String cmdName) {
        super();
        m_cmd = cmd;
        m_source = source;
        m_target = target;
        m_overwrite = overwrite;
        m_options = options;
        m_cmdName = cmdName;
    }

    @Override
    protected List<String> getArgs(Context context, OsArgListBuilder argListBuilder) {
        argListBuilder.addVariableEscaped("source", m_source);
        argListBuilder.addVariableEscaped("target", m_target);
        argListBuilder.addVariable("overwrite", m_overwrite);
        argListBuilder.addVariableEscaped("options", m_options);
        return argListBuilder.build(m_cmd, true);
    }

    @Override
    protected Logger getLogger() {
        return XdtlLogger.getLogger("xdtl.cmd." + m_cmdName);
    }
}
