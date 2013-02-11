package org.mmx.xdtl.runtime.util;

import org.apache.log4j.Logger;

public class VelocityLogger extends LogChuteToSlf4jAdapter {

    public VelocityLogger() {
        super(Logger.getLogger("velocity"));
    }
}
