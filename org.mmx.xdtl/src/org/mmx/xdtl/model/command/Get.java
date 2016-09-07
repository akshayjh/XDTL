package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.Command;

public class Get extends FileTransfer implements Command {

    public Get(String cmd, String source, String target, String overwrite, String options) {
        super(cmd, source, target, overwrite, options);
    }
}
