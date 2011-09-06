package org.mmx.xdtl.runtime;

public interface TypeConverter {
    public abstract String toString(Object obj);
    public abstract Character toChar(Object obj);
    public abstract Boolean toBoolean(Object obj);
    public abstract Integer toInteger(Object obj);
    public abstract <T extends Enum<T>> T toEnumMember(Class<T> enumClass, Object obj);
}
