package org.mmx.xdtl.model;

public class Script extends AbstractElement {
    private final String m_href;
    private final String m_script;
    private final String m_encoding;

    public Script(String href, String encoding, String script) {
        super();
        m_href = href;
        m_encoding = encoding;
        m_script = script;
    }

    public String getHref() {
        return m_href;
    }

    public String getScript() {
        return m_script;
    }

    public String getEncoding() {
        return m_encoding;
    }
}
