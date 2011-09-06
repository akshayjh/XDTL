package org.mmx.xdtl.model;

public class Source extends AbstractElement {
    private final String m_mapId;
    private final String m_source;
    private final String m_alias;
    private final String m_isQuery;
    
    public Source(String mapId, String source, String alias, String isQuery) {
        m_mapId = mapId;
        m_source = source;
        m_alias = alias;
        m_isQuery = isQuery;
    }

    public String getMapId() {
        return m_mapId;
    }

    public String getSource() {
        return m_source;
    }

    public String getAlias() {
        return m_alias;
    }

    public String getIsQuery() {
        return m_isQuery;
    }
}
