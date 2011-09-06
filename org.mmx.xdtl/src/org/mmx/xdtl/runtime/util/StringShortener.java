package org.mmx.xdtl.runtime.util;

public class StringShortener {
    private static final int MAX_LENGTH = 100; 
    
    public String shorten(String src) {
        if (src != null && src.length() <= MAX_LENGTH) {
            return src;
        }
        
        return src.substring(0, MAX_LENGTH - 3) + "...";
    }
}
