package org.mmx.xdtl.model.command;

public class Unpack extends FileTransfer {

    public Unpack(String cmd, String source, String target, String overwrite, String options) {
        super(cmd, source, target, overwrite, options);
    }
}
