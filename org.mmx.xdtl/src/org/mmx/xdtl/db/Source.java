package org.mmx.xdtl.db;

import java.util.List;

public interface Source {
    //Object[] readNext() throws Exception;
    List<Column> getColumns() throws Exception;
    void fetchRows(RowHandler rowhandler) throws Exception;
    void close() throws Exception;
}
