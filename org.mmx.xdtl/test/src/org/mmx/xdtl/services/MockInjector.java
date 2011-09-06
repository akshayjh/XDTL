package org.mmx.xdtl.services;

public class MockInjector implements Injector {
    private boolean m_injectMembersCalled;
    private Object m_instance;
    
    public MockInjector(Object instance) {
        m_instance = instance;
    }
    
    @Override
    public void injectMembers(Object obj) {
        m_injectMembersCalled = true;
    }

    public boolean isInjectMembersCalled() {
        return m_injectMembersCalled;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(Class<T> type) {
        return (T) m_instance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(Class<T> type, String name) {
        return (T) m_instance;
    }
}
