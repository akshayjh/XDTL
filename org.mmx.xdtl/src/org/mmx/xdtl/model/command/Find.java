package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public class Find extends AbstractElement implements Command {
    private final String m_source;
    private final String m_match;
    private final String m_recursive;
    private final String m_rowset;

    public Find(String source, String match, String recursive, String rowset) {
        super();
        m_source = source;
        m_match = match;
        m_recursive = recursive;
        m_rowset = rowset;
    }

    public String getSource() {
        return m_source;
    }

    public String getMatch() {
        return m_match;
    }

    public String getRecursive() {
        return m_recursive;
    }

    public String getRowset() {
        return m_rowset;
    }
}
