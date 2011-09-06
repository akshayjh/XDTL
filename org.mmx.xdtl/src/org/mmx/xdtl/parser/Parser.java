package org.mmx.xdtl.parser;

import java.net.URL;

import org.mmx.xdtl.model.Package;

/**
 * An interface for parsers of XDTL documents.
 *  
 * @author vsi
 */
public interface Parser {
    /**
     * Parses an XDTL document from URL <code>url</code> to a <code>Package</code>.
     * 
     * @param url The URL of the document to parse.
     * @return XDTL package.
     */
    Package parse(URL url);
}
