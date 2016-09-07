package org.mmx.xdtl.db;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.mmx.xdtl.model.XdtlException;

final class RowSetColumns implements Iterable<Column> {
    private HashMap<String, Integer> m_keyToIndexMap = new HashMap<>();
    private int[] m_lookupArray;       // Indices into m_columns table in real columns order
    private Object[] m_columns;
    private int m_capacity;
    private int m_size;
    private int m_firstReusableColumn = 0; // Reusable column indices start with 1, 0 means no reusable columns
    private IdentifierConverter m_identifierConverter;
    private int m_spareColumnCount;

    public RowSetColumns(int initialCapacity, IdentifierConverter identifierConverter, int spareColumnCount) {
        m_capacity = initialCapacity;
        m_columns = new Object[m_capacity];
        m_lookupArray = new int[m_capacity];
        m_identifierConverter = identifierConverter;
        m_spareColumnCount = spareColumnCount;
    }

    public Column get(int index) {
        return (Column) m_columns[m_lookupArray[index]];
    }

    public int size() {
        return m_size;
    }

    public boolean add(Column column) {
        String key = m_identifierConverter.toDbIdentifier(column.getName());

        if (m_keyToIndexMap.containsKey(key)) {
            throw new XdtlException("Duplicate column '" + column.getName() + "'");
        }

        ensureCapacity(m_size + 1);

        boolean columnReused;
        int freeColumnIndex = m_firstReusableColumn;
        if (freeColumnIndex == 0) {
            // no free columns
            freeColumnIndex = m_size;
            columnReused = false;
        } else {
            freeColumnIndex -= 1;
            m_firstReusableColumn = (Integer) m_columns[freeColumnIndex];
            columnReused = true;
        }

        m_lookupArray[m_size] = freeColumnIndex;
        m_columns[freeColumnIndex] = column;
        m_keyToIndexMap.put(key, m_size);
        m_size++;
        return columnReused;
    }

    public void remove(int index) {
        validateIndex(index);

        int columnIndex = m_lookupArray[index];
        Column column = (Column) m_columns[columnIndex];
        m_keyToIndexMap.remove(m_identifierConverter.toDbIdentifier(column.getName()));

        m_columns[columnIndex] = m_firstReusableColumn;
        m_firstReusableColumn = columnIndex + 1; // Reusable column indices start with 1
        m_size--;

        if (index != m_size) {
            // Move indices down one step
            System.arraycopy(m_lookupArray, index + 1, m_lookupArray, index, m_size - index);
            updateKeyToIndexMap(index);
        }
    }

    public void remove(String name) {
        remove(getColumnIndex(name));
    }

    public boolean containsColumn(Object key) {
        if (key instanceof Number) {
            return isValidIndex(((Number) key).intValue());
        }

        if (key instanceof String) {
            key = m_identifierConverter.toDbIdentifier((String) key);
        }

        return m_keyToIndexMap.containsKey(key);
    }

    public int getCapacity() {
        return m_capacity;
    }

    public final int getInternalColumnIndex(int index) {
        return m_lookupArray[index];
    }

    public int getColumnIndex(String name) {
        String key = m_identifierConverter.toDbIdentifier((String) name);

        Integer pos = m_keyToIndexMap.get(key);
        if (pos == null) {
            throw new XdtlException("Column '" + key + "' not found");
        }

        return pos;
    }

    public int getInternalColumnIndex(Object key) {
        if (key instanceof Number) {
            int index = ((Number) key).intValue();
            validateIndex(index);
            return m_lookupArray[index];
        }

        if (key instanceof String) {
            key = m_identifierConverter.toDbIdentifier((String) key);
        }

        Integer pos = m_keyToIndexMap.get(key);
        if (pos == null) {
            throw new XdtlException("Column '" + key + "' not found");
        }


        return m_lookupArray[pos];
    }

    private void updateKeyToIndexMap(int offset) {
        for (int i = offset; i < m_size; i++) {
            Column column = (Column) m_columns[m_lookupArray[i]];
            String key = m_identifierConverter.toDbIdentifier(column.getName());
            m_keyToIndexMap.put(key, i);
        }
    }

    private void validateIndex(int index) {
        if (!isValidIndex(index)) {
            throw new IndexOutOfBoundsException();
        }
    }

    private boolean isValidIndex(int index) {
        return (index >= 0) && (index < m_size);
    }

    private void ensureCapacity(int capacity) {
        if (capacity <= m_capacity) {
            return;
        }

        m_capacity = capacity + m_spareColumnCount;
        m_columns = Arrays.copyOf(m_columns, m_capacity);
        m_lookupArray = Arrays.copyOf(m_lookupArray, m_capacity);
    }

    public String[] newColumnNamesArray() {
        String[] result = new String[m_size];

        for (int i = 0; i < m_size; i++) {
            Column column = (Column) m_columns[m_lookupArray[i]];
            result[i] = column.getName();
        }

        return result;
    }

    @Override
    public Iterator<Column> iterator() {
        return new Iterator<Column>() {
            int index;

            @Override
            public boolean hasNext() {
                return index < m_size;
            }

            @Override
            public Column next() {
                return (Column) m_columns[m_lookupArray[index++]];
            }
        };
    }

    public void swap(String name1, String name2) {
        swap(getColumnIndex(name1), getColumnIndex(name2));
    }

    public void swap(int index1, int index2) {
        validateIndex(index1);
        validateIndex(index2);

        int internalIndex1 = m_lookupArray[index1];
        int internalIndex2 = m_lookupArray[index2];
        m_lookupArray[index1] = internalIndex2;
        m_lookupArray[index2] = internalIndex1;

        Column column = (Column) m_columns[internalIndex1];
        String key = m_identifierConverter.toDbIdentifier(column.getName());
        m_keyToIndexMap.put(key, index2);

        column = (Column) m_columns[internalIndex2];
        key = m_identifierConverter.toDbIdentifier(column.getName());
        m_keyToIndexMap.put(key, index1);
    }
}
