package org.mmx.xdtl.runtime.command;

public class Source {
    private final int m_mapId;
    private final String m_source;
    private final String m_alias;
    private final boolean m_isQuery;
    
    public Source(int mapId, String source, String alias, boolean isQuery) {
        m_mapId = mapId;
        m_source = source;
        m_alias = alias;
        m_isQuery = isQuery;
    }

    public int getMapId() {
        return m_mapId;
    }

    public String getSource() {
        return m_source;
    }

    public String getAlias() {
        return m_alias;
    }

    public boolean isQuery() {
        return m_isQuery;
    }
}
