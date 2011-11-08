package org.mmx.xdtl.model;

public class Annotation extends AbstractElement {
    private String m_text;
    
    public Annotation(String text) {
        m_text = text;
    }
    
    public String getText() {
        return m_text;
    }
}
