package org.mmx.xdtl.db;

import java.text.SimpleDateFormat;

import org.xBaseJ.DBF;
import org.xBaseJ.fields.DateField;
import org.xBaseJ.fields.Field;

public class DbfSource implements Source {
    private DBF m_dbf;
    private SimpleDateFormat m_dateFormat = new SimpleDateFormat("yyyyMMdd");
    private Object[] m_fields;
    
    public DbfSource(String path, String encoding, int skip) throws Exception {
        m_dbf = new DBF(path, DBF.READ_ONLY, encoding);
        m_fields = new Object[m_dbf.getFieldCount()];
        skipRows(skip);
    }
    
    private void skipRows(int rowCount) throws Exception {
        for (int i = 0; i < rowCount; i++) {
            if (m_dbf.getCurrentRecordNumber() >= m_dbf.getRecordCount()) return;
            m_dbf.read();
        }
    }

    @Override
    public Object[] readNext() throws Exception {
        if (m_dbf.getCurrentRecordNumber() >= m_dbf.getRecordCount()) return null;
        
        m_dbf.read();
        
        for (int i = 0; i < m_dbf.getFieldCount(); i++) {
            Field field = m_dbf.getField(i + 1);
            
            if (field instanceof DateField) {
                m_fields[i] = m_dateFormat.parse(field.get());
            } else {
                m_fields[i] = rtrim(field.get());
            }
        }
        
        return m_fields;
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
}
