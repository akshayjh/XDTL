package org.mmx.xdtl.model;

public class TextFileProperties {
    public static final char DEFAULT_DELIMITER = ';';
    public static final char DEFAULT_QUOTE = '"';
    public static final char DEFAULT_ESCAPE = '\\';

    private final String m_type;
    private final String m_delimiter;
    private final String m_quote;
    private final String m_null;
    private final String m_escape;
    private final String m_encoding;

    public TextFileProperties(String type, String delimiter, String quote,
            String nul, String escape, String encoding) {
        super();
        m_type = type;
        m_delimiter = delimiter;
        m_quote = quote;
        m_null = nul;
        m_escape = escape;
        m_encoding = encoding;
    }

    public String getType() {
        return m_type;
    }

    public String getDelimiter() {
        return m_delimiter;
    }

    public String getQuote() {
        return m_quote;
    }

    public String getNull() {
        return m_null;
    }

    public String getEscape() {
        return m_escape;
    }

    public String getEncoding() {
        return m_encoding;
    }
}
