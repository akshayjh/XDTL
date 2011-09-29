package org.mmx.xdtl.parser.impl;

import java.net.URL;
import java.net.URLConnection;

import org.mmx.xdtl.model.Package;
import org.mmx.xdtl.parser.Parser;
import org.mmx.xdtl.services.PackageCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class CachingParser implements Parser {
    private final Logger m_logger = LoggerFactory.getLogger(CachingParser.class);
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
            m_logger.debug("Returning from cache: '{}'", url);
        }

        return pkg;
    }
}
