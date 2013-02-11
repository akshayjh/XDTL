package org.mmx.xdtl.runtime.command;

import org.apache.log4j.Logger;
import org.mmx.xdtl.model.Connection;

public class PostgresqlWriteCmd extends PostgresqlReadWriteCmd {
    private static final Logger logger = Logger.getLogger("xdtl.cmd.write");

    public PostgresqlWriteCmd(String source, String target, String type,
            boolean overwrite, String delimiter, String quote, String encoding,
            Connection cnn) {
        
        super(source, target, type, overwrite, delimiter, quote, encoding, cnn, 
                false, false);
    }

    @Override
    protected void logCmdStart() {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("source=%s, type=%s, target=%s," +
            		" overwrite=%s, delimiter=%s, quote=%s, encoding=%s",
            		getSource(), getType(), getTarget(), isOverwrite(),
            		getDelimiter(), getQuote(), getEncoding()));
        } else {
            logger.info("target=" + getTarget());
        }
    }
}
