package org.mmx.xdtl.runtime.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.HashMap;

import org.mmx.xdtl.model.Package;
import org.mmx.xdtl.parser.Parser;
import org.mmx.xdtl.services.PathList;
import org.mmx.xdtl.services.PathList.ForEachCallback;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ExtensionLoader {
    private final PathList m_pathList;
    private final Parser m_parser;
    private final HashMap<String, Package> m_cache = new HashMap<String, Package>();

    @Inject
    public ExtensionLoader(@Named("extensions.path") PathList pathList, Parser parser) {
        m_pathList = pathList;
        m_parser = parser;
    }
    
    public Package getExtensionPackage(String nsUri, final String extensionName) {
        String key = getCacheKey(nsUri, extensionName);
        Package pkg = m_cache.get(key);
        if (pkg != null) {
            return pkg;
        }
        
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xdtl");
            }
        };

        ForEachCallback callback = new ForEachCallback() {
            @Override
            public Object execute(File file) {
                try {
                    Package pkg = m_parser.parse(new URL("file://" + file.getAbsolutePath()).openConnection());
                    return pkg.getTask(extensionName) != null ? pkg : null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        pkg = (Package) m_pathList.forEachFile(filter, callback);
        if (pkg != null) {
            m_cache.put(key, pkg);
        }

        return pkg;
    }

    private String getCacheKey(String nsUri, String extensionName) {
        return "_" + nsUri + "_" + extensionName;
    }
}
