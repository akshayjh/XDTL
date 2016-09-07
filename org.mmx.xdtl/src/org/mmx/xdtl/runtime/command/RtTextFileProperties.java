package org.mmx.xdtl.runtime.command;

public class RtTextFileProperties<T extends Enum<T>> {
    private final T m_type;
    private final char m_delimiter;
    private final char m_quote;
    private final String m_null;
    private final char m_escape;
    private final String m_encoding;

    public RtTextFileProperties(T type, char delimiter, char quote,
            String nul, char escape, String encoding) {
        super();
        m_type = type;
        m_delimiter = delimiter;
        m_quote = quote;
        m_null = nul;
        m_escape = escape;
        m_encoding = encoding;
    }

    public T getType() {
        return m_type;
    }

    public char getDelimiter() {
        return m_delimiter;
    }

    public char getQuote() {
        return m_quote;
    }

    public String getNull() {
        return m_null;
    }

    public char getEscape() {
        return m_escape;
    }

    public String getEncoding() {
        return m_encoding;
    }

    @Override
    public String toString() {
        return String.format("type=%s, delimiter=%s, quote=%s, null=%s, escape=%s, encoding=%s",
                m_type, m_delimiter, m_quote, m_null, m_escape, m_encoding);
    }
}
