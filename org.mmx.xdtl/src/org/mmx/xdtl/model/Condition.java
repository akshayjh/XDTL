package org.mmx.xdtl.model;

public class Condition extends AbstractElement {
    private final String m_mapId;
    private final String m_alias;
    private final String m_condition;
    private final String m_condType;
    private final String m_joinType;

    public Condition(String mapId, String alias, String condition,
            String condType, String joinType) {
        m_mapId = mapId;
        m_alias = alias;
        m_condition = condition;
        m_condType = condType;
        m_joinType = joinType;
    }

    public String getMapId() {
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
