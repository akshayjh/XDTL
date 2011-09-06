package org.mmx.xdtl.model;

public class Column extends AbstractElement {
    private final String m_mapId;
    private final String m_target;
    private final String m_source;
    private final String m_function;
    private final String m_dataType;
    private final String m_isJoinKey;
    private final String m_isUpdatable;
    private final String m_isDistinct;
    private final String m_isAggregate;

    public Column(String mapId, String target, String source, String function,
            String dataType, String isJoinKey, String isUpdatable,
            String isDistinct, String isAggregate) {
        super();
        m_mapId = mapId;
        m_target = target;
        m_source = source;
        m_function = function;
        m_dataType = dataType;
        m_isJoinKey = isJoinKey;
        m_isUpdatable = isUpdatable;
        m_isDistinct = isDistinct;
        m_isAggregate = isAggregate;
    }

    public String getMapId() {
        return m_mapId;
    }

    public String getTarget() {
        return m_target;
    }

    public String getSource() {
        return m_source;
    }

    public String getFunction() {
        return m_function;
    }

    public String getDataType() {
        return m_dataType;
    }

    public String getIsJoinKey() {
        return m_isJoinKey;
    }

    public String getIsUpdatable() {
        return m_isUpdatable;
    }

    public String getIsDistinct() {
        return m_isDistinct;
    }

    public String getIsAggregate() {
        return m_isAggregate;
    }
}
