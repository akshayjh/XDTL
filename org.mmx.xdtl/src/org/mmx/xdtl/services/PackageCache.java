package org.mmx.xdtl.services;

import java.net.URL;
import java.util.HashMap;

import org.mmx.xdtl.model.Package;

public class PackageCache {
    private HashMap<URL, Package> m_cache = new HashMap<URL, Package>();
    
    public void put(Package pkg) {
        m_cache.put(pkg.getUrl(), pkg);
    }
    
    public Package get(URL url) {
        return m_cache.get(url);
    }
}
