package org.mmx.xdtl.runtime.impl;

/**
 * This exception exits current task. Execution of (possibly) nested command
 * lists must be stopped, an exception is one way to do this.
 *
 * @author vsi
 */
class XdtlExitException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public XdtlExitException() {
        super();
    }
}
