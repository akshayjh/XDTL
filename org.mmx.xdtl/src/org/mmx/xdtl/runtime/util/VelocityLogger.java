package org.mmx.xdtl.runtime.util;

import org.slf4j.LoggerFactory;

public class VelocityLogger extends LogChuteToSlf4jAdapter {

    public VelocityLogger() {
        super(LoggerFactory.getLogger("velocity"));
    }
}
