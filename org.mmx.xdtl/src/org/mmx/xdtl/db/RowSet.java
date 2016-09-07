package org.mmx.xdtl.db;

import java.io.StringWriter;
import java.io.Writer;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.mmx.xdtl.db.RowSet.Row;
import org.mmx.xdtl.model.XdtlException;

import com.opencsv.CSVWriter;

public class RowSet implements List<Row> {
    private ArrayList<Row> m_rows = new ArrayList<>();
    private RowSetColumns m_columns;
    private RowSetColumnList m_columnList = new RowSetColumnList(); // List view over m_columns

    private IdentifierConverter m_identifierConverter;

    // =========================================================================
    // RowSet
    // =========================================================================

    public RowSet(DatabaseMetaData dbMetaData, ResultSetMetaData rsMetaData) throws SQLException {
        super();
        m_identifierConverter = new IdentifierConverter(dbMetaData);

        int columnCount = rsMetaData.getColumnCount();
        initColumns(columnCount);

        for (int i = 1; i <= columnCount; i++) {
            Column column = new Column(rsMetaData.getColumnName(i),
                    rsMetaData.getColumnType(i), rsMetaData.getColumnTypeName(i));

            m_columns.add(column);
        }
    }

    public RowSet(int columnCount) {
        super();
        m_identifierConverter = IdentifierConverter.nullConverter();
        initColumns(columnCount);

        for (int i = 0; i < columnCount; i++) {
            m_columns.add(new Column("COL" + i));
        }
    }

    public RowSet(int columnCount, List<?> data) {
        this(columnCount);

        if (data != null) {
            append(data);
        }
    }

    public RowSet(List<?> columns) {
        super();
        m_identifierConverter = IdentifierConverter.nullConverter();
        initColumns(columns.size());

        if (columns.size() > 0) {
            Object firstColumn = columns.get(0);
            if (firstColumn instanceof Column) {
                for (Object column: columns) {
                    m_columns.add((Column) column);
                }
            } else if (firstColumn instanceof String) {
                for (Object columnName: columns) {
                    m_columns.add(new Column((String) columnName));
                }
            } else {
                throw new XdtlException("Cannot create columns from " + firstColumn.getClass().getName());
            }
        }
    }

    public RowSet(Map<?, ?> map) {
        m_identifierConverter = IdentifierConverter.nullConverter();
        initColumns(2);
        m_columns.add(new Column("KEY"));
        m_columns.add(new Column("VALUE"));

        for (Map.Entry<?, ?> entry: map.entrySet()) {
            Row row = newRow();
            row.set(0, entry.getKey());
            row.set(1, entry.getValue());
            add(row);
        }
    }

    public RowSet(List<?> columns, List<?> data) {
        this(columns);

        if (data != null) {
            append(data);
        }
    }

    private void initColumns(int size) {
        m_columns = new RowSetColumns(size + 10, m_identifierConverter, 10);
    }

    public List<Column> getColumns() {
        return m_columnList;
    }

    public int getColumnCount() {
        return m_columns.size();
    }

    public String[] getColumnNamesArray() {
        return m_columns.newColumnNamesArray();
    }

    public void toCsv(Writer writer, boolean header, char delimiter, char quote, char escape) {
        try {
            try {
                CSVWriter csvWriter = new CSVWriter(writer, delimiter, quote, escape);
                try {
                    if (header) {
                        csvWriter.writeNext(getColumnNamesArray());
                    }

                    String[] arr = new String[getColumnCount()];
                    for (Row row: m_rows) {
                        row.copyTo(arr);
                        csvWriter.writeNext(arr);
                    }
                } finally {
                    csvWriter.close();
                }
            } finally {
                writer.close();
            }
        } catch (Exception e) {
            throw new XdtlException(e);
        }
    }

    public String toCsv() {
        return toCsv(false);
    }

    public String toCsv(boolean header) {
        return toCsv(header, ';', '"');
    }

    public String toCsv(boolean header, char delimiter, char quote) {
        return toCsv(header, delimiter, quote, '\\');
    }

    public String toCsv(boolean header, char delimiter, char quote, char escape) {
        StringWriter writer = new StringWriter();
        toCsv(writer, header, delimiter, quote, escape);
        return writer.toString();
    }

    // -------------------------------------------------------------------------
    // List interface
    // -------------------------------------------------------------------------

    @Override
    public int size() {
        return m_rows.size();
    }

    @Override
    public boolean isEmpty() {
        return m_rows.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return m_rows.contains(o);
    }

    @Override
    public Iterator<Row> iterator() {
        return m_rows.iterator();
    }

    @Override
    public Object[] toArray() {
        return m_rows.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return m_rows.toArray(a);
    }

    @Override
    public boolean add(Row e) {
        return m_rows.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return m_rows.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return m_rows.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Row> c) {
        return m_rows.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Row> c) {
        return m_rows.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return m_rows.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return m_rows.retainAll(c);
    }

    @Override
    public void clear() {
        m_rows.clear();
    }

    @Override
    public Row get(int index) {
        return m_rows.get(index);
    }

    @Override
    public Row set(int index, Row element) {
        return m_rows.set(index, element);
    }

    @Override
    public void add(int index, Row element) {
        m_rows.add(index, element);
    }

    @Override
    public Row remove(int index) {
        return m_rows.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return m_rows.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return m_rows.lastIndexOf(o);
    }

    @Override
    public ListIterator<Row> listIterator() {
        return m_rows.listIterator();
    }

    @Override
    public ListIterator<Row> listIterator(int index) {
        return m_rows.listIterator(index);
    }

    @Override
    public List<Row> subList(int fromIndex, int toIndex) {
        return m_rows.subList(fromIndex, toIndex);
    }

    // -------------------------------------------------------------------------
    // Rows (append/remove)
    // -------------------------------------------------------------------------

    public Row newRow() {
        return new Row();
    }

    private Row newRow(Object[] data) {
        return newRow(data, Math.min(data.length, m_columns.size()));
    }

    private Row newRow(Object[] data, int count) {
        Row row = newRow();

        for (int i = 0; i < count; i++) {
            row.set(i, data[i]);
        }

        return row;
    }

    public Row newRow(List<?> data) {
        return newRow(data, Math.min(data.size(), m_columns.size()));
    }

    public Row newRow(List<?> data, int count) {
        Row row = newRow();
        int i = 0;

        for (Object obj: data) {
            if (i >= count) break;
            row.set(i++, obj);
        }

        return row;
    }

    public RowSet append() {
        add(newRow());
        return this;
    }

    public RowSet append(Object[] data) {
        return append(data, false);
    }

    public RowSet append(Object[] data, boolean strict) {
        if (strict) {
            checkStrictAppendRequirements(data.length);
        }

        add(newRow(data));
        return this;
    }

    public RowSet append(List<?> data) {
        return append(data, false);
    }

    @SuppressWarnings("unchecked")
    public RowSet append(List<?> data, boolean strict) {
        if (strict) {
            checkStrictAppendRequirements(data.size());
        }

        if (data.isEmpty()) {
            return this;
        }

        Object firstElement = data.get(0);
        if (firstElement instanceof List) {
            appendListOfObjects((List<? extends List<?>>) data);
        } else if (firstElement instanceof Object[]) {
            appendListOfObjectArrays((List<Object[]>) data);
        } else {
            add(newRow(data));
        }

        return this;
    }

    private void appendListOfObjectArrays(List<Object[]> data) {
        for (Object[] arr: data) {
            add(newRow(arr));
        }
    }

    private void appendListOfObjects(List<? extends List<?>> data) {
        for (List<?> list: data) {
            add(newRow(list));
        }
    }

    private void checkStrictAppendRequirements(int dataSize) {
        if (dataSize != m_columns.size()) {
            throw new XdtlException(String.format("Column count does not match: data=%s, rowset=%s",
                    dataSize, m_columns.size()));
        }
    }

    public Row remove() {
        return m_rows.remove(m_rows.size() - 1);
    }

    private void expandRows(int newCapacity) {
        for (Row row: m_rows) {
            row.setCapacity(newCapacity);
        }
    }

    // -------------------------------------------------------------------------
    // Rowset append
    // -------------------------------------------------------------------------

    public RowSet append(RowSet rowSet) {
        return append(rowSet, false);
    }

    public RowSet append(RowSet rowSet, boolean strict) {
        if (strict) {
            checkStrictAppendRequirements(rowSet.getColumnCount());
        }

        int columnCount = Math.min(m_columns.size(), rowSet.getColumnCount());
        for (Row row: rowSet) {
            Row newRow = newRow();
            for (int i = 0; i < columnCount; i++) {
                newRow.set(i, row.get(i));
            }
            add(newRow);
        }

        return this;
    }

    // -------------------------------------------------------------------------
    // Sorting
    // -------------------------------------------------------------------------

    public void sort(int columnIndex, boolean ascending) {
        m_rows.sort(new Comparator<Row>() {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public int compare(Row o1, Row o2) {
                Object c1 = o1.get(columnIndex);
                Object c2 = o2.get(columnIndex);

                if (c1 == c2) {
                    return 0;
                }

                int result;

                if (c1 == null) {
                    result = -1;
                } else if (c2 == null) {
                    result = 1;
                } else if (c1 instanceof Comparable){
                    result = ((Comparable) c1).compareTo(c2);
                } else {
                    throw new RuntimeException(c1.getClass()
                            + " does not implement Comparable interface");
                }

                return ascending ? result : -result;
            }
        });
    }

    // -------------------------------------------------------------------------
    // Columns (add/delete)
    // -------------------------------------------------------------------------

    public RowSet add(Column column) {
        add(column, null);
        return this;
    }

    public RowSet add() {
        add(new Column("COL" + m_columns.size()), null);
        return this;
    }

    public RowSet add(Column column, Object defaultValue) {
        int prevCapacity = m_columns.getCapacity();
        boolean columnReused = m_columns.add(column);
        int newCapacity = m_columns.getCapacity();

        if (prevCapacity != newCapacity) {
            expandRows(newCapacity);
        }

        if (columnReused || defaultValue != null) {
            setColumnValues(m_columns.size() - 1, defaultValue);
        }

        return this;
    }

    public RowSet add(List<?> columns) {

        if (columns.size() == 0) {
            return this;
        }

        Object firstColumn = columns.get(0);

        if (firstColumn instanceof Column) {
            for (Object column: columns) {
                add((Column) column);
            }
        } else if (firstColumn instanceof String) {
            for (Object columnName: columns) {
                add(new Column((String) columnName));
            }
        }

        return this;
    }

    public RowSet add(String columnName) {
        return add(new Column(columnName));
    }

    public RowSet add(String columnName, Object defaultValue) {
        return add(new Column(columnName), defaultValue);
    }

    public void delete(int index) {
        m_columns.remove(index);
    }

    public void delete(String name) {
        m_columns.remove(name);
    }

    public void delete() {
        m_columns.remove(m_columns.size() - 1);
    }

    public void swapColumns(int index1, int index2) {
        m_columns.swap(index1, index2);
    }

    public void swapColumns(String name1, String name2) {
        m_columns.swap(name1, name2);
    }

    private void setColumnValues(int index, Object value) {
        for (Row row: m_rows) {
            row.set(index, value);
        }
    }

    // =========================================================================
    // Row
    // =========================================================================

    public class Row extends AbstractMap<String, Object> implements Iterable<Object> {
        private Object[] m_columnValues;

        public Row() {
            m_columnValues = new Object[m_columns.getCapacity()];
        }

        public void set(int index, Object value) {
            if (!isValidIndex(index)) {
                throw new IndexOutOfBoundsException();
            }

            m_columnValues[m_columns.getInternalColumnIndex(index)] = value;
        }

        public Object get(int index) {
            if (!isValidIndex(index)) {
                throw new IndexOutOfBoundsException();
            }

            return m_columnValues[m_columns.getInternalColumnIndex(index)];
        }

        private Object internalGet(int index) {
            return m_columnValues[m_columns.getInternalColumnIndex(index)];
        }

        private boolean isValidIndex(int index) {
            return (index >= 0) && (index < m_columns.size());
        }

        public void valuesToArray(Object[] arr) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = m_columnValues[m_columns.getInternalColumnIndex(i)];
            }
        }

        public void copyTo(String[] arr) {
            for (int i = 0; i < arr.length; i++) {
                Object value = m_columnValues[m_columns.getInternalColumnIndex(i)];
                arr[i] = value != null ? value.toString() : null;
            }
        }

        protected void setCapacity(int newCapacity) {
            m_columnValues = Arrays.copyOf(m_columnValues, newCapacity);
        }

        public int getLength() {
            return size();
        }

        // ---------------------------------------------------------------------
        // Map interface
        // ---------------------------------------------------------------------

        @Override
        public int size() {
            return RowSet.this.m_columns.size();
        }

        @Override
        public boolean containsKey(Object key) {
            return RowSet.this.m_columns.containsColumn(key);
        }

        @Override
        public Object get(Object key) {
            int valueIndex = m_columns.getInternalColumnIndex(key);
            return m_columnValues[valueIndex];
        }

        @Override
        public Object put(String key, Object value) {
            int valueIndex = m_columns.getInternalColumnIndex(key);
            Object oldValue = m_columnValues[valueIndex];
            m_columnValues[valueIndex] = value;
            return oldValue;
        }

        @Override
        public Set<java.util.Map.Entry<String, Object>> entrySet() {
            return new AbstractSet<Map.Entry<String,Object>>() {

                @Override
                public Iterator<java.util.Map.Entry<String, Object>> iterator() {
                    return newEntrySetIterator();
                }

                @Override
                public int size() {
                    return RowSet.this.m_columns.size();
                }
            };
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public String toString() {
            return getClass().getName() + '@' + Integer.toHexString(hashCode());
        }

        private Iterator<java.util.Map.Entry<String, Object>> newEntrySetIterator() {
            return new Iterator<java.util.Map.Entry<String, Object>>() {
                int m_index;

                @Override
                public boolean hasNext() {
                    return m_index < m_columns.size();
                }

                @Override
                public java.util.Map.Entry<String, Object> next() {
                    Column col = m_columns.get(m_index);
                    Object value = internalGet(m_index);
                    m_index++;
                    return new AbstractMap.SimpleEntry<String, Object>(col.getName(), value);
                }
            };
        }

        // ---------------------------------------------------------------------
        // Iterable interface
        // ---------------------------------------------------------------------
        @Override
        public Iterator<Object> iterator() {
            return new Iterator<Object>() {
                int m_index;

                @Override
                public boolean hasNext() {
                    return m_index < m_columns.size();
                }

                @Override
                public Object next() {
                    return internalGet(m_index++);
                }
            };
        }
    }

    // =========================================================================
    // RowSetColumnList
    // =========================================================================

    private class RowSetColumnList extends AbstractList<Column> {
        @Override
        public Column get(int index) {
            return m_columns.get(index);
        }

        @Override
        public int size() {
            return m_columns.size();
        }

        @Override
        public Iterator<Column> iterator() {
            return m_columns.iterator();
        }
    }
}
