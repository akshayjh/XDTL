package org.mmx.xdtl.model.command;

public class Put extends FileTransfer {

    public Put(String cmd, String source, String target, String overwrite, String options) {
        super(cmd, source, target, overwrite, options);
    }
}
