package org.mmx.xdtl.log;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class XdtlLoggerFactory implements LoggerFactory {

    public XdtlLoggerFactory() {
        super();
    }

    @Override
    public Logger makeNewLoggerInstance(String name) {
        return new XdtlLogger(name);
    }
}
