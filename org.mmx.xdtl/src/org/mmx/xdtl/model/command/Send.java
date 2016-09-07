package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.TextFileProperties;

public class Send extends AbstractElement implements Command {
    private final String m_source;
    private final String m_target;
    private final String m_overwrite;
    private final TextFileProperties m_textFileProperties;
    private final String m_header;
    private final String m_skip;
    private final String m_rowset;

    public enum Type {
        CSV,
        EXCEL,
        DBF;
    }

    public Send(String source, String target, String overwrite,
            TextFileProperties textFileProperties, String header, String skip,
            String rowset) {
        super();
        m_source = source;
        m_target = target;
        m_overwrite = overwrite;
        m_textFileProperties = textFileProperties;
        m_header = header;
        m_skip = skip;
        m_rowset = rowset;
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

    public TextFileProperties getTextFileProperties() {
        return m_textFileProperties;
    }

    public String getHeader() {
        return m_header;
    }

    public String getSkip() {
        return m_skip;
    }

    public String getRowset() {
        return m_rowset;
    }
}
