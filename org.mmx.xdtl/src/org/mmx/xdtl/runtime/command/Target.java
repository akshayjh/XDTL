package org.mmx.xdtl.runtime.command;

public class Target {
    private final int m_mapId;
    private final String m_target;
    private final String m_alias;
    private final boolean m_isVirtual;
    
    public Target(int mapId, String target, String alias, boolean isVirtual) {
        m_mapId = mapId;
        m_target = target;
        m_alias = alias;
        m_isVirtual = isVirtual;
    }

    public int getMapId() {
        return m_mapId;
    }

    public String getTarget() {
        return m_target;
    }

    public String getAlias() {
        return m_alias;
    }

    public boolean isVirtual() {
        return m_isVirtual;
    }
}
