package org.mmx.xdtl.model;

public class Target extends AbstractElement {
    private final String m_mapId;
    private final String m_target;
    private final String m_alias;
    private final String m_isVirtual;
    
    public Target(String mapId, String target, String alias, String isVirtual) {
        m_mapId = mapId;
        m_target = target;
        m_alias = alias;
        m_isVirtual = isVirtual;
    }

    public String getMapId() {
        return m_mapId;
    }

    public String getTarget() {
        return m_target;
    }

    public String getAlias() {
        return m_alias;
    }

    public String getIsVirtual() {
        return m_isVirtual;
    }
}
