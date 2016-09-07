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
     * @param runtimeClassMap
     *            A map of classes for the runtime command.
     * @param cmd
     *            A command from XDTL model.
     * @return new instance of some class from runtimeClassMap.
     */
    RuntimeCommand build(Context context,
            RuntimeCommandClassMap runtimeClassMap, Command cmd) throws Exception;
}
