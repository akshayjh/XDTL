package org.mmx.xdtl.db;

public interface Source {
    Object[] readNext() throws Exception;
    void close() throws Exception;
}
