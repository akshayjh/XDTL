package org.mmx.xdtl.runtime.util;

public class StringShortener {
    private final int m_maxLength;

    public StringShortener() {
        this(100);
    }

    public StringShortener(int maxLength) {
        m_maxLength = maxLength;
    }

    public String shorten(String src) {
        return shorten(src, m_maxLength);
    }

    public String shorten(String src, int maxLength) {
        if (src != null && src.length() <= maxLength) {
            return src;
        }

        return src.substring(0, maxLength - 3) + "...";
    }
}
