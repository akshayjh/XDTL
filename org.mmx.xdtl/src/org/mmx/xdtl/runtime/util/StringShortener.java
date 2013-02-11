package org.mmx.xdtl.runtime.util;

public class StringShortener {
    private static final int DEFAULT_MAX_LENGTH = 100;
    private final int m_maxLength;
    
    public StringShortener() {
        this(DEFAULT_MAX_LENGTH);
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
