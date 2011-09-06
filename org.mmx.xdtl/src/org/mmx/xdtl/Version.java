package org.mmx.xdtl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Version {
    private static final String PROPERTIES_RESOURCE = "Version.properties";
        
    private final Logger m_logger = LoggerFactory.getLogger(Version.class);
    
    private String m_specificationVersion;
    private String m_specificationTitle;
    private String m_specificationVendor;
    private String m_implementationVersion;
    private String m_implementationTitle;
    private String m_implementationVendor;
    
    public Version() {
        init();
    }

    private void init() {
        if (getClass().getPackage().getImplementationVersion() != null) {
            initFromPackage();
        } else {
            try {
                // only used in development
                initFromProperties(PROPERTIES_RESOURCE);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load version info from '" +
                        PROPERTIES_RESOURCE + "'", e);
            }
        }
    }

    private void initFromProperties(String resource) throws IOException {
        InputStream is = getClass().getResourceAsStream(resource);
        if (is != null) {
            Properties props = new Properties();
            try {
                props.load(is);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    m_logger.warn("Failed to close input stream", e);
                }
            }
            
            m_specificationTitle    = props.getProperty("xdtl.specification.title");
            m_specificationVersion  = props.getProperty("xdtl.specification.version");
            m_specificationVendor   = props.getProperty("xdtl.specification.vendor");
            m_implementationTitle   = props.getProperty("xdtl.implementation.title");
            m_implementationVersion = props.getProperty("xdtl.implementation.version.prefix") + ".00";
            m_implementationVendor  = props.getProperty("xdtl.implementation.vendor");
        }
    }

    private void initFromPackage() {
        Package pkg = this.getClass().getPackage();
        m_specificationTitle    = pkg.getSpecificationTitle();
        m_specificationVendor   = pkg.getSpecificationVendor();
        m_specificationVersion  = pkg.getSpecificationVersion();
        m_implementationTitle   = pkg.getImplementationTitle();
        m_implementationVendor  = pkg.getImplementationVendor();
        m_implementationVersion = pkg.getImplementationVersion();
    }

    public String getSpecificationVersion() {
        return m_specificationVersion;
    }

    public String getSpecificationTitle() {
        return m_specificationTitle;
    }

    public String getSpecificationVendor() {
        return m_specificationVendor;
    }

    public String getImplementationVersion() {
        return m_implementationVersion;
    }

    public String getImplementationTitle() {
        return m_implementationTitle;
    }

    public String getImplementationVendor() {
        return m_implementationVendor;
    }
}
