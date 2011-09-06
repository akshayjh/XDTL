package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public abstract class FileTransfer extends AbstractElement implements Command {
    protected final String m_cmd;
    protected final String m_source;
    protected final String m_target;
    protected final String m_overwrite;

    public FileTransfer(String cmd, String source, String target, String overwrite) {
        m_cmd = cmd;
        m_source = source;
        m_target = target;
        m_overwrite = overwrite;
    }

    public String getCmd() {
        return m_cmd;
    }

    public String getSource() {
        return m_source;
    }

    public String getTarget() {
        return m_target;
    }

    public String getOverwrite() {
        return m_overwrite;
    }

}