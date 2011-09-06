package org.mmx.xdtl.db.converter;

import java.sql.SQLException;
import java.sql.Types;

import org.mmx.xdtl.db.Column;

public class DoubleConverter implements IConverter<Double> {

    @Override
    public Object convert(Double dbl, Column column) throws SQLException {
        if (dbl == null) {
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
            return dbl.toString();
        
        case Types.BIGINT:
        case Types.BOOLEAN:
        case Types.DECIMAL:
        case Types.DOUBLE:
        case Types.FLOAT:
        case Types.INTEGER:
        case Types.NUMERIC:
        case Types.REAL:
        case Types.ROWID:
        case Types.SMALLINT:
        case Types.TINYINT:
            return dbl;
        }
        
        throw new SQLException("Unsupported data type: " + column.getTypeName());
    }
}
