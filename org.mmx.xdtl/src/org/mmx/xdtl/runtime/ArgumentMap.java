package org.mmx.xdtl.runtime;

import java.util.HashMap;
import java.util.Map.Entry;

public class ArgumentMap {
    private HashMap<String, Argument> m_map = new HashMap<String, Argument>();

    public ArgumentMap() {
    }

    public Argument get(String name) {
        return m_map.get(name);
    }

    public void put(String name, Argument argument) {
        m_map.put(name, argument);
    }

    public HashMap<String, Object> toValueMap() {
        HashMap<String, Object> result = new HashMap<String, Object>();

        for (Entry<String, Argument> e: m_map.entrySet()) {
            result.put(e.getKey(), e.getValue().getValue());
        }

        return result;
    }

    public int size() {
        return m_map.size();
    }

    public String toLogString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{");

        for (Entry<String, Argument> e: m_map.entrySet()) {
            Argument arg = e.getValue();
            if (!arg.isLoggingDisabled()) {
                buf.append(e.getKey()).append("=");
                buf.append(arg.getValue()).append(",");
            }
        }

        if (buf.length() > 1) {
            buf.setLength(buf.length() - 1);
        }

        buf.append("}");
        return buf.toString();
    }
}
