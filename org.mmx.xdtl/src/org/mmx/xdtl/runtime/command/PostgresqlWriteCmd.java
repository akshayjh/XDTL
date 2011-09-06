package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.model.Connection;

public class PostgresqlWriteCmd extends PostgresqlReadWriteCmd {

    public PostgresqlWriteCmd(String source, String target, String type,
            boolean overwrite, String delimiter, String quote, String encoding,
            Connection cnn) {
        
        super(source, target, type, overwrite, delimiter, quote, encoding, cnn, 
                false);
    }
}
