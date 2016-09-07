package org.mmx.xdtl.model.command;

public class Write extends ReadWrite {

    public Write(String source, String target, String connection, String type,
            String overwrite, String delimiter, String quote, String encoding, String escape) {
        super(source, target, connection, type, overwrite, delimiter, quote, encoding, escape);
    }
}
