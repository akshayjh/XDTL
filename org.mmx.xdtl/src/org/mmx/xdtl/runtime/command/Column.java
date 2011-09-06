package org.mmx.xdtl.runtime.command;

public class Column {
    private final int m_mapId;
    private final String m_target;
    private final String m_source;
    private final String m_function;
    private final String m_dataType;
    private final boolean m_isJoinKey;
    private final boolean m_isUpdatable;
    private final boolean m_isDistinct;
    private final boolean m_isAggregate;

    public Column(int mapId, String target, String source, String function,
            String dataType, boolean isJoinKey, boolean isUpdatable,
            boolean isDistinct, boolean isAggregate) {
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

    public int getMapId() {
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

    public boolean isJoinKey() {
        return m_isJoinKey;
    }

    public boolean isUpdatable() {
        return m_isUpdatable;
    }

    public boolean isDistinct() {
        return m_isDistinct;
    }

    public boolean isAggregate() {
        return m_isAggregate;
    }
}
