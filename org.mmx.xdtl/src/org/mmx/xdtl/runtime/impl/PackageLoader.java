package org.mmx.xdtl.runtime.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.mmx.xdtl.model.Package;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.parser.Parser;
import org.mmx.xdtl.services.PathList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class PackageLoader {
    private static final Logger logger = LoggerFactory.getLogger(PackageLoader.class);

    private HashMap<URL, Package> m_cache = new HashMap<URL, Package>();
    private PathList m_libraryPath;
    private Parser m_parser;
    
    @Inject
    public PackageLoader(Parser parser, @Named("library.path") PathList libraryPath) {
        m_parser = parser;
        m_libraryPath = libraryPath;
    }
    
    public Package loadPackage(URL baseUrl, String urlSpec) throws Exception {
        logger.debug("loadPackage: baseUrl={}, urlSpec={}", baseUrl, urlSpec);
        URL pkgUrl = new URL(baseUrl, urlSpec);
        Package pkg = m_cache.get(pkgUrl);
        if (pkg != null) {
            logger.debug("loadPackage: package in cache");
            return pkg;
        }
        
        pkg = getPackage(pkgUrl, urlSpec);
        m_cache.put(pkgUrl, pkg);
        return pkg;
    }

    private Package getPackage(URL pkgUrl, String urlSpec) throws Exception {
        boolean urlIsAbsolute = new URI(urlSpec).isAbsolute();
        
        Package pkg = tryLoadPackage(pkgUrl, urlSpec);
        if (pkg != null) return pkg;

        if (!urlIsAbsolute) {
            for (URL rootUrl: m_libraryPath.getRoots()) {
                pkgUrl = new URL(rootUrl, urlSpec);
                pkg = tryLoadPackage(pkgUrl, urlSpec);
                if (pkg != null) return pkg;
            }
        }

        throw new XdtlException("Package not found: " + urlSpec);
    }

    private Package tryLoadPackage(URL pkgUrl, String urlSpec) throws IOException {
        logger.debug("tryLoadPackage: loading from '{}'", pkgUrl);
        URLConnection cnn = openConnection(pkgUrl);
        return cnn != null ? m_parser.parse(cnn) : null;
    }

    private URLConnection openConnection(URL pkgUrl) throws IOException {
        try {
            URLConnection cnn = pkgUrl.openConnection();
            cnn.connect();
            return cnn;
        } catch (FileNotFoundException e) {
            logger.debug("openConnection: file not found");
        }
        
        return null;
    }
}
