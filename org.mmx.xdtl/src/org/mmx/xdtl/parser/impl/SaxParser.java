package org.mmx.xdtl.parser.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.SAXParser;

import org.mmx.xdtl.model.Package;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * XDTL parser implementation using SAX. Same instance of the parser can be used
 * repeatedly, it will retain all schemas used in the process.
 * 
 * @author vsi
 */
public class SaxParser implements Parser {
    private final Logger m_logger = LoggerFactory.getLogger(SaxParser.class);    
    private final SAXParser m_saxParser;
    private final ElementHandlerSet m_elementHandlerSet;

    @Inject
    SaxParser(SAXParser saxParser, ElementHandlerSet elementHandlerSet) {
        m_saxParser = saxParser;
        m_elementHandlerSet = elementHandlerSet;
    }
    
    @Override
    public Package parse(URLConnection cnn) {
        URL url = cnn.getURL();
        if (url.getRef() != null) {
            throw new XdtlException("Reference is not allowed in package url: url='" + url + "'");
        }
        
        try {
            Package pkg;
            Handler handler = new Handler(url.toString(), m_elementHandlerSet);
            InputStream is = cnn.getInputStream();

            try {
                m_logger.info("Parsing '" + url + "'");
                m_saxParser.parse(is, handler);
            } finally {
                closeInputStream(is);
            }
            
            pkg = handler.getPackage();
            pkg.setUrl(url);
            m_logger.debug("Parsed package '" + pkg.getName() + "' from '" + url + "'");
            return pkg;
        } catch (Exception e) {
            throw new XdtlException(e);
        }        
    }

    private void closeInputStream(InputStream is) {
        try {
            is.close();
        } catch (IOException e) {
            m_logger.warn("Failed to close input stream", e);
        }
    }
}
