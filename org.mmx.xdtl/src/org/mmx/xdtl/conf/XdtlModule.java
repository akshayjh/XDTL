package org.mmx.xdtl.conf;

import java.io.IOException;
import java.util.Properties;

import org.mmx.xdtl.model.XdtlException;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class XdtlModule extends AbstractModule {
    private final Properties m_properties;
    private final Properties m_defaultProperties;
    
    private static class PropertiesModule extends AbstractModule {
        private final Properties m_properties;
        
        public PropertiesModule(Properties properties) {
            m_properties = properties;
        }
        
        @Override
        protected void configure() {
            Names.bindProperties(binder(), m_properties);
        }
    }
    
    public XdtlModule(Properties properties) {
        m_properties = properties;
        m_defaultProperties = new Properties();

        try {
            m_defaultProperties.put("home", getXdtlHomeDir());
            m_defaultProperties.load(getClass().getResourceAsStream("default.properties"));
        } catch (IOException e) {
            throw new XdtlException("Failed to load default properties", e);
        }
    }
    
    private String getXdtlHomeDir() {
        return System.getProperty("user.home") + "/.xdtl/";
    }

    @Override
    protected void configure() {
        Properties properties = new Properties(m_defaultProperties);
        properties.putAll(m_properties);
        install(new PropertiesModule(properties));
        install(new ParserModule());
        install(new RuntimeModule(properties));
    }
}
