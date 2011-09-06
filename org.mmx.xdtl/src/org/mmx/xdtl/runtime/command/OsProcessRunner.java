package org.mmx.xdtl.runtime.command;

import java.util.List;

public interface OsProcessRunner {
    public OsRunnerResult run(List<String> args) throws Exception;
}
