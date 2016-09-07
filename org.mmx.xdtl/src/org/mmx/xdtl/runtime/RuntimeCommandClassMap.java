package org.mmx.xdtl.runtime;

public interface RuntimeCommandClassMap {
    Class<? extends RuntimeCommand> getCommandClass(String tag);
}
