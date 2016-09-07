package org.mmx.xdtl.model;

public class Config extends AbstractElement {
    private String m_href;
    private String m_encoding;

    public Config(String href, String encoding) {
        m_href = href;
        m_encoding = encoding;
    }

    public String getHref() {
        return m_href;
    }

    public String getEncoding() {
        return m_encoding;
    }
}
