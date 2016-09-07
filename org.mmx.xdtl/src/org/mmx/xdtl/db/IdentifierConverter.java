package org.mmx.xdtl.db;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class IdentifierConverter {
    private static interface Converter {
        String convert(String str);
    }

    private final Converter m_converter;

    public IdentifierConverter(DatabaseMetaData dbMetaData) throws SQLException {
        if (dbMetaData.storesLowerCaseIdentifiers()) {
            m_converter = new ToLowerCaseConverter();
        } else if (dbMetaData.storesUpperCaseIdentifiers()) {
            m_converter = new ToUpperCaseConverter();
        } else {
            m_converter = new NullConverter();
        }
    }

    private IdentifierConverter(Converter converter) {
        m_converter = converter;
    }

    public static IdentifierConverter nullConverter() {
        return new IdentifierConverter(new NullConverter());
    }

    public String toDbIdentifier(String str) {
        return str != null ? m_converter.convert(str) : null;
    }

    private static class ToUpperCaseConverter implements Converter {
        @Override
        public String convert(String str) {
            return str.toUpperCase();
        }
    }

    private static class ToLowerCaseConverter implements Converter {
        @Override
        public String convert(String str) {
            return str.toLowerCase();
        }
    }

    private static class NullConverter implements Converter {

        @Override
        public String convert(String str) {
            return str;
        }
    }
}
