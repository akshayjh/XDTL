package org.mmx.xdtl.db.converter;

import org.mmx.xdtl.db.Column;

public interface IConverter<T> {
    Object convert(T object, Column column) throws Exception;
}
