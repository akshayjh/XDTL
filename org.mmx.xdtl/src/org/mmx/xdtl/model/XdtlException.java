/**
 * 
 */
package org.mmx.xdtl.model;

/**
 * @author vsi
 */
public class XdtlException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private SourceLocator m_sourceLocator;
    private boolean m_logged;

    /**
     * 
     */
    public XdtlException() {
        m_sourceLocator = SourceLocator.NULL;
    }

    /**
     * @param arg0
     */
    public XdtlException(String arg0) {
        super(arg0);
        m_sourceLocator = SourceLocator.NULL;
    }

    /**
     * @param arg0
     */
    public XdtlException(Throwable arg0) {
        super(arg0);
        m_sourceLocator = SourceLocator.NULL;
    }

    /**
     * @param arg0
     * @param arg1
     */
    public XdtlException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        m_sourceLocator = SourceLocator.NULL;
    }

    /**
     * @param arg0
     * @param arg1
     */
    public XdtlException(String arg0, SourceLocator sourceLocator) {
        super(arg0);        
        setSourceLocator(sourceLocator);
    }
    
    /**
     * @param arg0
     * @param arg1
     */
    public XdtlException(String arg0, SourceLocator sourceLocator, Throwable arg1) {
        super(arg0, arg1);
        m_sourceLocator = sourceLocator;
    }
    
    public SourceLocator getSourceLocator() {
        return m_sourceLocator;
    }

    public void setSourceLocator(SourceLocator sourceLocator) {
        if (sourceLocator == null) {
            throw new NullPointerException("Sourcelocator cannot be null");
        }
        m_sourceLocator = sourceLocator;
    }

    public boolean isLogged() {
        return m_logged;
    }

    public void setLogged(boolean logged) {
        m_logged = logged;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(m_sourceLocator).append(' ').append(super.toString());
        return builder.toString();
    }
}
