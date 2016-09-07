package org.mmx.xdtl.runtime;

import java.util.Map;

import org.mmx.xdtl.model.Package;

public interface Engine {
    void run(String url, Map<String, Object> args, Map<String, Object> globals);

    void run(Package pkg, String taskname, Map<String, Object> args,
            Map<String, Object> globals);
}
