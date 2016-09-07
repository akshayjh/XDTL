package org.mmx.xdtl.runtime.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.XdtlException;

import com.google.inject.Inject;

public class PropertiesLoader {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.rt.util.propertiesLoader");

    @Inject
    public PropertiesLoader() {
    }

    public Properties loadProperties(URL baseUrl, String urlSpec, String encoding) {
        Properties props = new Properties();
        try {
            URL url = new URL(baseUrl, urlSpec);
            if (logger.isTraceEnabled()) {
                logger.trace(String.format("Loading '%s'", url));
            }

            InputStream is = url.openStream();
            try {
                InputStreamReader reader = new InputStreamReader(is, encoding);
                try {
                    props.load(reader);
                } finally {
                    reader.close();
                }
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new XdtlException(e);
        }

        return props;
    }
}
