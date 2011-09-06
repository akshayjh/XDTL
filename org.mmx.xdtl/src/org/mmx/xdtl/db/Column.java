package org.mmx.xdtl.db;

public class Column {
    private final int m_type;
    private final String m_typeName;
	private final String m_name;
    
    public Column(int type, String typeName, String name) {
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
