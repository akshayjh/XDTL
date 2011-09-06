package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public class Fetch extends AbstractElement implements Command {
    private final String m_source;
    private final String m_connection;
    private final String m_type;
    private final String m_overwrite;
    private final String m_delimiter;
    private final String m_quote;
    private final String m_target;
    private final String m_rowset;
    private final String m_encoding;

    public enum Type {
        CSV,
        FIXED,
        XML
    }
    
    public Fetch(String source, String connection, String type,
            String overwrite, String delimiter, String quote, String target,
            String rowset, String encoding) {
        super();
        m_source = source;
        m_connection = connection;
        m_type = type;
        m_overwrite = overwrite;
        m_delimiter = delimiter;
        m_quote = quote;
        m_target = target;
        m_rowset = rowset;
        m_encoding = encoding;
    }

    public String getSource() {
        return m_source;
    }

    public String getConnection() {
        return m_connection;
    }

    public String getType() {
        return m_type;
    }

    public String getOverwrite() {
        return m_overwrite;
    }

    public String getDelimiter() {
        return m_delimiter;
    }

    public String getQuote() {
        return m_quote;
    }

    public String getTarget() {
        return m_target;
    }

    public String getRowset() {
        return m_rowset;
    }

    public String getEncoding() {
        return m_encoding;
    }
}
