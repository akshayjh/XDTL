package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class ExitCmdBuilder implements CommandBuilder {

    @Override
    public <T extends RuntimeCommand> RuntimeCommand build(Context context,
            Class<T> runtimeClass, Command cmd) throws Exception {
        Constructor<T> ctor = runtimeClass.getConstructor();
        return ctor.newInstance();
    }
}
