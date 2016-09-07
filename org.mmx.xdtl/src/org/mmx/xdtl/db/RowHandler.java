package org.mmx.xdtl.db;

import java.util.List;

public interface RowHandler {
    void handleRow(Object[] data, List<String> columnNames) throws Exception;
}
