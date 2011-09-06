package org.mmx.xdtl.runtime.command;

public class Condition {
    private final int m_mapId;
    private final String m_alias;
    private final String m_condition;
    private final String m_condType;
    private final String m_joinType;

    public Condition(int mapId, String alias, String condition,
            String condType, String joinType) {
        m_mapId = mapId;
        m_alias = alias;
        m_condition = condition;
        m_condType = condType;
        m_joinType = joinType;
    }

    public int getMapId() {
        return m_mapId;
    }

    public String getAlias() {
        return m_alias;
    }

    public String getCondition() {
        return m_condition;
    }

    public String getCondType() {
        return m_condType;
    }

    public String getJoinType() {
        return m_joinType;
    }
}
