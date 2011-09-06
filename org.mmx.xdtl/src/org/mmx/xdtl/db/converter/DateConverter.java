package org.mmx.xdtl.db.converter;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.mmx.xdtl.db.Column;

public class DateConverter implements IConverter<Date> {

    @Override
    public Object convert(Date date, Column column) throws Exception {
        if (date == null) {
            return null;
        }
        
        switch (column.getType()) {
        case Types.CHAR:
        case Types.CLOB:
        case Types.LONGNVARCHAR:
        case Types.LONGVARCHAR:
        case Types.NCHAR:
        case Types.NCLOB:
        case Types.NVARCHAR:
        case Types.VARCHAR:
            return new java.sql.Date(date.getTime()).toString();
        
        case Types.DATE:
            return new java.sql.Date(date.getTime());

        case Types.TIME:
            return new java.sql.Time(date.getTime());
            
        case Types.TIMESTAMP:
            return new java.sql.Timestamp(date.getTime());
        }
        
        throw new SQLException("Unsupported data type: " + column.getTypeName());
    }
}
