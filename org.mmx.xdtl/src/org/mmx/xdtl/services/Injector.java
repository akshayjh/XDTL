package org.mmx.xdtl.services;

public interface Injector {
    public void injectMembers(Object obj);
    public <T> T getInstance(Class<T> type);
    public <T> T getInstance(Class<T> type, String name);
}
