package org.mmx.xdtl.runtime.command;

import java.util.List;

public interface OsArgListBuilder {
    public abstract void addVariable(String name, Object value);
    public abstract void addVariableEscaped(String name, String value);
    public abstract String escape(String str);
    public abstract List<String> build(String cmdline, boolean resolveVariables);
}
