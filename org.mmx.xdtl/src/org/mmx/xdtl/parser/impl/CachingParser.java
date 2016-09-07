package org.mmx.xdtl.parser.impl;

import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.Package;
import org.mmx.xdtl.parser.Parser;
import org.mmx.xdtl.services.PackageCache;

import com.google.inject.Inject;

public class CachingParser implements Parser {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.rt.parser.cachingParser");
    private final PackageCache m_cache;
    private final Parser m_parser;

    @Inject
    public CachingParser(@NonCaching Parser parser, PackageCache cache) {
        m_parser = parser;
        m_cache = cache;
    }

    @Override
    public Package parse(URLConnection cnn) {
        URL url = cnn.getURL();
        Package pkg = m_cache.get(url);

        if (pkg == null) {
            pkg = m_parser.parse(cnn);
            m_cache.put(pkg);
            return pkg;
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Returning from cache: '" + url + "'");
            }
        }

        return pkg;
    }
}
