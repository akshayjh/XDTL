package org.mmx.xdtl.model.command;

public class Unpack extends FileTransfer {

    public Unpack(String cmd, String source, String target, String overwrite) {
        super(cmd, source, target, overwrite);
    }
}
