package org.mmx.xdtl.services;

public class UriSchemeParser {
    // Maximum number of characters to scan at the beginning of string to search
    // for valid scheme. Specification (RFC2396) does not limit the number of
    // characters in scheme part, but for performance reasons, in case of
    // strings which start with long runs of valid scheme characters, some kind
    // of limit is reasonable.
    private static final int  NUM_CHARS_TO_SCAN = 32;
    private static final char SCHEME_DELIMITER = ':';
    
    public String getScheme(String text) {
        if (text == null) {
            return "";
        }

        int schemeLength = getSchemeLength(text);
        return text.substring(0, schemeLength);
    }

    private int getSchemeLength(String text) {
        int numCharsToScan = Math.min(text.length(), NUM_CHARS_TO_SCAN);
        if (numCharsToScan == 0) {
            return 0;
        }
        
        char c = text.charAt(0);
        if (!isAlpha(c)) {
            return 0;
        }
        
        int i;
        for (i = 1; i < numCharsToScan; i++) {
            c = text.charAt(i);
            if (!isValidSchemeChar(c)) {
                break;
            }
        }
        
        return c == SCHEME_DELIMITER ? i : 0;
    }

    private boolean isValidSchemeChar(char c) {
        return isAlpha(c) || isDigit(c) || c == '+' || c == '-' || c == '.';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
}
