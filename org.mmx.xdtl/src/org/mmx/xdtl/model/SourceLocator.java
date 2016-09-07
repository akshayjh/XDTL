package org.mmx.xdtl.model;

public class SourceLocator {
    public static final SourceLocator NULL = new SourceLocator(null, 0, null);
    
    private final int m_lineNumber;
    private final String m_documentUrl;
    private final String m_tagName;

    public SourceLocator(String documentUrl, int lineNumber, String tagName) {
        m_documentUrl = documentUrl;
        m_lineNumber = lineNumber;
        m_tagName = tagName;
    }
    
    public int getLineNumber() {
        return m_lineNumber;
    }

    public String getDocumentUrl() {
        return m_documentUrl;
    }
    
    public String getTagName() {
        return m_tagName;
    }

    public String toString() {
        if (this == NULL) {
            return "[unknown]"; 
        }
        
        return m_tagName + '@' + m_documentUrl + ':' + m_lineNumber;
    }
    
    public boolean isNull() {
        return this == NULL;
    }
}
