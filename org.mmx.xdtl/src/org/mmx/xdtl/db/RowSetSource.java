package org.mmx.xdtl.db;

import java.util.Iterator;
import java.util.List;


public class RowSetSource implements Source {
    private Iterator<Object[]> m_iterator;

    public RowSetSource(List<Object[]> rowSet, int skip) {
        m_iterator = rowSet.listIterator(skip);
    }
    
    @Override
    public void close() throws Exception {
    }

    @Override
    public void fetchRows(RowHandler rowHandler) throws Exception {
        while (m_iterator.hasNext()) {
            rowHandler.handleRow(m_iterator.next(), null);
        }
    }
}
