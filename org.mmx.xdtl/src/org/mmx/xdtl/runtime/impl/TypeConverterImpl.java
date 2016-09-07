package org.mmx.xdtl.runtime.impl;

import java.util.Arrays;

import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.TypeConverter;

public class TypeConverterImpl implements TypeConverter {

    @Override
    public Boolean toBoolean(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }

        if (obj instanceof String) {
            String s = (String) obj;
            if (Boolean.parseBoolean(s)) {
                return true;
            }

            // try to convert to int
            try {
                obj = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        if (obj instanceof Integer) {
            return ((Integer) obj) != 0;
        }

        throw new XdtlException("Type '" + obj.getClass().getName() +
                "' cannot be converted to boolean");
    }

    @Override
    public Integer toInteger(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof Integer) {
            return (Integer) obj;
        }

        if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue() ? 1 : 0;
        }

        if (obj instanceof String) {
            String s = (String) obj;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new XdtlException("'" + s + "' cannot be converted to integer");
            }
        }

        throw new XdtlException("Type '" + obj.getClass().getName() +
            "' cannot be converted to integer");
    }

    @Override
    public String toString(Object obj) {
        if (obj == null) {
            return null;
        }

        return obj.toString();
    }

    @Override
    public <T extends Enum<T>> T toEnumMember(Class<T> enumClass, Object obj) {
        String memberName = toString(obj);
        if (memberName == null || memberName.length() == 0) {
            return null;
        }

        try {
            return T.valueOf(enumClass, memberName);
        } catch (IllegalArgumentException e) {
            T[] values = enumClass.getEnumConstants();
            throw new XdtlException("Invalid value '" + memberName + "' for '"
                    + enumClass.getSimpleName()
                    + "'. Allowed values are: " + Arrays.toString(values));
        }
    }

    @Override
    public Character toChar(Object obj) {
        if (obj instanceof Character) {
            return (Character) obj;
        }

        String str = toString(obj);
        if (str == null || str.length() == 0) {
            return null;
        }

        char c0 = str.charAt(0);
        if (str.length() == 1) return c0;

        if (c0 == '\\') {
            if (str.length() == 2) {
                switch(str.charAt(1)) {
                    case 't': return '\t';
                    case 'b': return '\b';
                    case 'r': return '\r';
                    case 'n': return '\n';
                }
            }
        }

        throw new XdtlException("'" + str + "' cannot be converted to char");
    }
}
