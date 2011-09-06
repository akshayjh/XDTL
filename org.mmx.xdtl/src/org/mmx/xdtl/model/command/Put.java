package org.mmx.xdtl.model.command;

public class Put extends FileTransfer {

    public Put(String cmd, String source, String target, String overwrite) {
        super(cmd, source, target, overwrite);
    }
}
