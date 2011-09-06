package org.mmx.xdtl.runtime;

import java.net.URL;
import java.util.Map;

import org.mmx.xdtl.model.Package;

public interface Engine {
    void run(URL url, Map<String, Object> args, Map<String, Object> globals);

    void run(Package pkg, String taskname, Map<String, Object> args,
            Map<String, Object> globals);
}
