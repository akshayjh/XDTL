package org.mmx.xdtl.runtime.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.runtime.Context;

public class ContextToBindingsAdapter implements Bindings {
    private final Context m_context;

    public ContextToBindingsAdapter(Context context) {
        m_context = context;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return m_context.getVariable((String) key) != null;
        }

        return false;
    }

    @Override
    public Object get(Object key) {
        if (key instanceof String) {
            return m_context.getVariableValue((String) key);
        }

        return null;
    }

    @Override
    public Object put(String name, Object value) {
        m_context.assignVariable(name, value);
        return value;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> toMerge) {
    }

    @Override
    public Object remove(Object key) {
        Variable var = m_context.undefineVariable((String) key);
        if (var == null) {
            return null;
        }
        
        return var.getValue();
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Collection<Object> values() {
        return null;
    }
}
