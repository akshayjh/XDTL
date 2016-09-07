package org.mmx.xdtl.runtime.command;

import java.util.List;

public interface OsArgListBuilder {
    void addVariable(String name, Object value);
    void addVariableEscaped(String name, String value);
    String escape(String str);
    List<String> build(String cmdline, boolean resolveVariables);
    String toCmdline(List<String> args);
}
