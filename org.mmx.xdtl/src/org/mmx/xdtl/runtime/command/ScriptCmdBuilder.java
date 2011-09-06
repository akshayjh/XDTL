package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.command.Script;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class ScriptCmdBuilder implements CommandBuilder {

    @Override
    public <T extends RuntimeCommand> RuntimeCommand build(Context context,
            Class<T> runtimeClass, Command cmd) throws Exception {

        Script script = (Script) cmd;
        Constructor<T> ctor = runtimeClass.getConstructor(String.class, String.class);
        return ctor.newInstance(script.getScript(), script.getTarget());
    }
}
