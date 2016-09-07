package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.model.Connection;

public class OracleReadCmd extends JdbcReadCmd {

    public OracleReadCmd(Object source, String target, String type,
            boolean overwrite, String delimiter, String quote, String encoding, String escape,
            Connection cnn, String errors, boolean header, int skipRows,
            int batch) {
        super(source, target, type, overwrite, delimiter, quote, encoding, escape, cnn,
                errors, header, skipRows, batch);
    }
	
    @Override
    protected String getTruncateSql(String target) {
        return "truncate table " + target;
    }
}
