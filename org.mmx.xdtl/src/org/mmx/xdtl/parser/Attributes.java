package org.mmx.xdtl.parser;

public interface Attributes {
    boolean getBooleanValue(String name, boolean deflt);
    int getIntValue(String name, int deflt);
    String getStringValue(String name);
    String getStringValue(String name, String deflt);
    String getValue(String name);
    int getLength();
    Attribute get(int index);
}
