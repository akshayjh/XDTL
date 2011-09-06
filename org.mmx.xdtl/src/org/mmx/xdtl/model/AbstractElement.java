package org.mmx.xdtl.model;

public abstract class AbstractElement implements Element {
    private String m_id = "";
    private SourceLocator m_sourceLocator;
    
    protected AbstractElement() {
        m_sourceLocator = SourceLocator.NULL;
    }
    
    protected AbstractElement(SourceLocator sourceLocator) {
        if (sourceLocator == null) {
            throw new XdtlException("sourceLocator cannot be null");
        }
        
        m_sourceLocator = sourceLocator;
    }

    /* (non-Javadoc)
     * @see org.mmx.xdtl.model.Element#getSourceLocator()
     */
    public SourceLocator getSourceLocator() {
        return m_sourceLocator;
    }

    /* (non-Javadoc)
     * @see org.mmx.xdtl.model.Element#setSourceLocator(org.mmx.xdtl.model.SourceLocator)
     */
    public void setSourceLocator(SourceLocator sourceLocator) {
        m_sourceLocator = sourceLocator;
    }

    @Override
    public String getId() {
        return m_id;
    }

    @Override
    public void setId(String id) {
        m_id = id;
    }        
}
