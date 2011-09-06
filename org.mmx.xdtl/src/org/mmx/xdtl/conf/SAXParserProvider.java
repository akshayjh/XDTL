package org.mmx.xdtl.conf;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.mmx.xdtl.model.XdtlException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class SAXParserProvider implements Provider<SAXParser> {
    private static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    private static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";
    
    private static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";
    
    private final SAXParserFactory m_factory;
    private final Provider<InputStream> m_schemaInputStreamProvider;
    
    @Inject
    SAXParserProvider(@Named("schema") Provider<InputStream> schemaInputStreamProvider) {
        m_factory = SAXParserFactory.newInstance();
        m_factory.setNamespaceAware(true);
        m_factory.setValidating(true);
        m_factory.setXIncludeAware(true);
        m_schemaInputStreamProvider = schemaInputStreamProvider;
    }
    
    protected SAXParser createSAXParser() {
        try {
            SAXParser parser = m_factory.newSAXParser();
            parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            parser.setProperty(JAXP_SCHEMA_SOURCE, m_schemaInputStreamProvider.get());
            return parser;
        } catch (Exception e) {
            throw new XdtlException("Could not create SAX parser", e);
        }
    }
    
    @Override
    public SAXParser get() {
        return createSAXParser();
    }
}
