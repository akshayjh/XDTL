package org.mmx.xdtl.runtime.util;

import org.mmx.xdtl.log.XdtlLogger;

public class VelocityLogger extends LogChuteToSlf4jAdapter {

    public VelocityLogger() {
        super(XdtlLogger.getLogger("velocity"));
    }
}
