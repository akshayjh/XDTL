package org.mmx.xdtl.runtime.impl;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.runtime.Context;

public interface CommandInvoker {

    public abstract void invoke(Command cmd, Context context);

}