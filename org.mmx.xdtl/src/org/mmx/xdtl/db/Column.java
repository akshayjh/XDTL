package org.mmx.xdtl.db;

import java.sql.Types;

public class Column {
    private final int m_type;
    private final String m_typeName;
	private final String m_name;

	public Column(String name) {
	    this(name, Types.VARCHAR, "VARCHAR");
	}

    public Column(String name, int type, String typeName) {
        super();
        m_type = type;
        m_typeName = typeName;
		m_name = name;
    }

    public int getType() {
        return m_type;
    }

    public String getTypeName() {
        return m_typeName;
    }

	public String getName() {
		return m_name;
	}
}
