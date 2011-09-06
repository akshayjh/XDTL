package org.mmx.xdtl.services;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class GuiceInjector implements Injector {
    private final com.google.inject.Injector m_injector;
    
    @Inject
    public GuiceInjector(com.google.inject.Injector injector) {
        m_injector = injector;
    }
    
    @Override
    public void injectMembers(Object obj) {
        m_injector.injectMembers(obj);
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return m_injector.getInstance(type);
    }

    @Override
    public <T> T getInstance(Class<T> type, String name) {
        return m_injector.getInstance(Key.get(type, Names.named(name)));
    }
}
