package org.mmx.xdtl.runtime.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.mmx.xdtl.model.Package;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.parser.Parser;
import org.mmx.xdtl.services.PathList;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class PackageLoader {
    private static final Logger logger = Logger.getLogger("xdtl.rt.packageLoader");

    private HashMap<URL, Package> m_cache = new HashMap<URL, Package>();
    private PathList m_libraryPath;
    private Parser m_parser;
    
    @Inject
    public PackageLoader(Parser parser, @Named("library.path") PathList libraryPath) {
        m_parser = parser;
        m_libraryPath = libraryPath;
    }
    
    public Package loadPackage(URL baseUrl, String urlSpec) throws Exception {
        if (logger.isTraceEnabled()) {
            logger.trace("loadPackage: baseUrl=" + baseUrl + ", urlSpec=" + urlSpec);
        }

        URL pkgUrl = new URL(baseUrl, urlSpec);
        Package pkg = m_cache.get(pkgUrl);
        if (pkg != null) {
            logger.trace("loadPackage: package in cache");
            return pkg;
        }
        
        pkg = getPackage(pkgUrl, urlSpec);
        m_cache.put(pkgUrl, pkg);
        return pkg;
    }

    private Package getPackage(URL pkgUrl, String urlSpec) throws Exception {
        boolean urlIsAbsolute = new URI(urlSpec).isAbsolute();
        
        Package pkg = tryLoadPackage(pkgUrl);
        if (pkg != null) return pkg;

        if (!urlIsAbsolute) {
            for (URL rootUrl: m_libraryPath.getRoots()) {
                pkgUrl = new URL(rootUrl, urlSpec);
                pkg = tryLoadPackage(pkgUrl);
                if (pkg != null) return pkg;
            }
        }

        throw new XdtlException("Package not found: " + urlSpec);
    }

    private Package tryLoadPackage(URL pkgUrl) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("tryLoadPackage: loading from '" + pkgUrl + "'");
        }

        URLConnection cnn = openConnection(pkgUrl);
        return cnn != null ? m_parser.parse(cnn) : null;
    }

    private URLConnection openConnection(URL pkgUrl) throws IOException {
        try {
            URLConnection cnn = pkgUrl.openConnection();
            cnn.connect();
            return cnn;
        } catch (FileNotFoundException e) {
            logger.trace("openConnection: file not found");
        }
        
        return null;
    }
}
