package org.mmx.xdtl.model;

public class Connection extends AbstractElement {
    public static final String TYPE_DB = "DB"; 
    public static final String TYPE_FILE = "FILE"; 
    public static final String TYPE_UDL = "UDL"; 
    public static final String TYPE_URI = "URI"; 
    
    private String m_name;
    private String m_type;
    private boolean m_exists;
    private String m_value;
    private String m_onOpen;

    public Connection(Connection cnn) {
        m_name = cnn.getName();
        m_type = cnn.getType();
        m_exists = cnn.isExists();
        m_value = cnn.getValue();
        m_onOpen = cnn.getOnOpen();
    }
    
    public Connection(String name, String type, boolean exists, String value, String onOpen) {
        m_name = name;
        m_type = type;
        m_exists = exists;
        m_value = value;
        m_onOpen = onOpen;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getType() {
        return m_type;
    }

    public void setType(String type) {
        m_type = type;
    }

    public boolean isExists() {
        return m_exists;
    }

    public void setExists(boolean exists) {
        m_exists = exists;
    }

    public String getValue() {
        return m_value;
    }

    public void setValue(String value) {
        m_value = value;
    }

    public String getOnOpen() {
        return m_onOpen;
    }
    
    public void setOnOpen(String onOpen) {
        m_onOpen = onOpen;
    }

    public boolean isFile() {
        return TYPE_FILE.equals(m_type);
    }

    public boolean isUri() {
        return TYPE_URI.equals(m_type);
    }

    public boolean isUdl() {
        return TYPE_UDL.equals(m_type);
    }

    public boolean isDB() {
        return TYPE_DB.equals(m_type);
    }
}
