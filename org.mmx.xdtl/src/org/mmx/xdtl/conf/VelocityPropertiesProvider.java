package org.mmx.xdtl.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class VelocityPropertiesProvider implements Provider<Properties> {
    private Properties m_properties;
    
    @Inject
    public VelocityPropertiesProvider(@Named("home") String homeDir) {
        try {
            Properties defaultProps = loadSystemProperties("/velocity.xml");
            m_properties = loadProperties(defaultProps, homeDir + "velocity.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private Properties loadProperties(Properties defaults, String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) return defaults;
        
        Properties result = new Properties(defaults);
        InputStream is = new FileInputStream(file);
        try {
            result.loadFromXML(is);
            return result;
        } finally {
            is.close();
        }
    }

    private Properties loadSystemProperties(String path) throws Exception {
        Properties props = new Properties();
        InputStream is = this.getClass().getResourceAsStream(path);
        try {
            props.loadFromXML(is);
        } finally {
            is.close();
        }
            
        return props;
    }

    @Override
    public Properties get() {
        return m_properties;
    }
}
