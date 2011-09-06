package org.mmx.xdtl.runtime;

import org.mmx.xdtl.model.Command;

/**
 * Interface for builder classes, which create executable commands based on
 * objects from xdtl model.
 * 
 * @author vsi
 */
public interface CommandBuilder {

    /**
     * @param context
     *            Runtime context
     * @param runtimeClass
     *            A class of the runtime command.
     * @param cmd
     *            A command from XDTL model.
     * @return new instance of class runtimeClass.
     */
    <T extends RuntimeCommand> RuntimeCommand build(Context context,
            Class<T> runtimeClass, Command cmd) throws Exception;
}
