package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.XdtlException;

public abstract class ReadWrite extends AbstractElement implements Command {
    private final String m_source;
    private final String m_target;
    private final String m_connection;
    private final String m_type;
    private final String m_overwrite;
    private final String m_delimiter;
    private final String m_quote;
    private final String m_encoding;
    private final String m_escape;

    public ReadWrite(String source, String target, String connection, String type,
            String overwrite, String delimiter, String quote, String encoding, String escape) {
        super();
        m_source = source;
        m_target = target;
        m_connection = connection;
        m_type = type;
        m_overwrite = overwrite;
        m_delimiter = delimiter;
        m_quote = quote;
        m_encoding = encoding;
        m_escape = escape;
        
        if (m_connection == null) {
            throw new XdtlException("connection cannot be null");
        }
    }

    public String getSource() {
        return m_source;
    }

    public String getTarget() {
        return m_target;
    }

    public String getType() {
        return m_type;
    }

    public String getConnection() {
        return m_connection;
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

    public String getEncoding() {
        return m_encoding;
    }

    public String getEscape() {
        return m_escape;
    }
}

