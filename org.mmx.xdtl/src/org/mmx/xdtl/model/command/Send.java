package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public class Send extends AbstractElement implements Command {
    private final String m_source;
    private final String m_target;
    private final String m_overwrite;
    private final String m_encoding;
    
    public Send(String source, String target, String overwrite, String encoding) {
        super();
        m_source = source;
        m_target = target;
        m_overwrite = overwrite;
        m_encoding = encoding;
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
    
    public String getEncoding() {
        return m_encoding;
    }    
}
