package org.mmx.xdtl.db;

import java.text.SimpleDateFormat;
import java.util.List;

import org.xBaseJ.DBF;
import org.xBaseJ.fields.DateField;
import org.xBaseJ.fields.Field;

public class DbfSource implements Source {
    private DBF m_dbf;
    private SimpleDateFormat m_dateFormat = new SimpleDateFormat("yyyyMMdd");

    public DbfSource(String path, String encoding, int skip) throws Exception {
        m_dbf = new DBF(path, DBF.READ_ONLY, encoding);
        skipRows(skip);
    }

    private void skipRows(int rowCount) throws Exception {
        for (int i = 0; i < rowCount; i++) {
            if (m_dbf.getCurrentRecordNumber() >= m_dbf.getRecordCount()) return;
            m_dbf.read();
        }
    }

    @Override
    public void fetchRows(RowHandler rowHandler) throws Exception {
        Object[] data = new Object[m_dbf.getFieldCount()];
        while (m_dbf.getCurrentRecordNumber() < m_dbf.getRecordCount()) {
            fetchRow(data);
            rowHandler.handleRow(data, null);
        }
    }

    private Object[] fetchRow(Object[] data) throws Exception {
        m_dbf.read();

        for (int i = 0; i < data.length; i++) {
            Field field = m_dbf.getField(i + 1);

            if (field instanceof DateField) {
                data[i] = m_dateFormat.parse(field.get());
            } else {
                data[i] = rtrim(field.get());
            }
        }

        return data;
    }

    private Object rtrim(String value) {
        for (int i = value.length() - 1; i >= 0; i--) {
            if (value.charAt(i) != ' ') {
                return value.substring(0, i + 1);
            }
        }

        return "";
    }

    @Override
    public void close() throws Exception {
        m_dbf.close();
    }

    @Override
    public List<Column> getColumns() throws Exception {
        throw new Exception("getColumns() is not implemented");
    }
}
