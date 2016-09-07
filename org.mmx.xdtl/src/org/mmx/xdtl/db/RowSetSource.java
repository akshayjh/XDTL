package org.mmx.xdtl.db;

import java.util.Iterator;
import java.util.List;

import org.mmx.xdtl.db.RowSet.Row;


public class RowSetSource implements Source {
    private RowSet m_rowSet;
    private Iterator<Row> m_iterator;
    private List<Column> m_columnList;

    public RowSetSource(RowSet rowSet, int skip) {
        m_rowSet = rowSet;
        m_iterator = rowSet.listIterator(skip);
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void fetchRows(RowHandler rowHandler) throws Exception {
        Object[] arr = new Object[m_rowSet.getColumnCount()];

        while (m_iterator.hasNext()) {
            m_iterator.next().valuesToArray(arr);
            rowHandler.handleRow(arr, null);
        }
    }

    @Override
    public List<Column> getColumns() throws Exception {
        if (m_columnList == null) {
            m_columnList = m_rowSet.getColumns();
        }

        return m_columnList;
    }
}
