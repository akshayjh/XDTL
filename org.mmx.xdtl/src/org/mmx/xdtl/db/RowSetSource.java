package org.mmx.xdtl.db;

import java.util.Iterator;
import java.util.List;


public class RowSetSource implements Source {
    private Iterator<Object[]> m_iterator;

    public RowSetSource(List<Object[]> rowSet, int skip) {
        m_iterator = rowSet.listIterator(skip);
    }
    
    @Override
    public Object[] readNext() throws Exception {
        return m_iterator.hasNext() ? m_iterator.next() : null;
    }

    @Override
    public void close() throws Exception {
    }
}
