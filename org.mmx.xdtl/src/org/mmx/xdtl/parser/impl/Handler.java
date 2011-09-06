/**
 * 
 */
package org.mmx.xdtl.parser.impl;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Package;
import org.mmx.xdtl.model.SourceLocator;
import org.mmx.xdtl.parser.ElementHandler;
import org.mmx.xdtl.parser.impl.ElementHandlerStack.Item;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Event handler for SAX events.
 * 
 * @author vsi
 */
class Handler extends DefaultHandler {
    private final ElementHandlerStack m_elementHandlerStack = new ElementHandlerStack();
    private final ElementHandlerSet m_elementHandlerSet;
    private final String m_documentUrl;
    
    private Locator m_locator;
    private Element m_lastModelElement;

    public Handler(String documentUrl, ElementHandlerSet elementHandlerSet) {
        m_documentUrl = documentUrl;
        m_elementHandlerSet = elementHandlerSet;
    }
    
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attr) throws SAXException {

        Class<? extends ElementHandler> clazz = getElementHandlerClass(localName);
        
        ElementHandler elementHandler;
        
        try {
            elementHandler = clazz.newInstance();
        } catch (Exception e) {
            throw new SAXException("Failed to instantiate handler for element '" + localName + "'", e);
        }
        
        org.mmx.xdtl.parser.Attributes wrappedAttrs = new AttributesImpl(attr);
        
        m_elementHandlerStack.push(elementHandler,
                new SourceLocator(m_documentUrl, m_locator.getLineNumber(), localName),
                wrappedAttrs.getStringValue("id", ""));
        
        elementHandler.startElement(localName, wrappedAttrs);
    }

    /**
     * Lookup element handler class corresponding to element name.
     * 
     * @param localName
     * @return
     * @throws SAXException
     */
    private Class<? extends ElementHandler> getElementHandlerClass(
            String elementName) throws SAXException {
        
        Class<? extends ElementHandler> clazz = m_elementHandlerSet.get(
                elementName);
        
        if (clazz == null) {
            throw new SAXException("Cannot find handler for element '"
                    + elementName + "'");
        }
        
        return clazz;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        m_locator = locator;
    }

    public Package getPackage() {
        return (Package) m_lastModelElement;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        Item stackItem = m_elementHandlerStack.pop();
        m_lastModelElement = stackItem.getElementHandler().endElement();
        m_lastModelElement.setSourceLocator(stackItem.getSourceLocator());
        m_lastModelElement.setId(stackItem.getId());
        notifyTopElementHandler();
    }

    private void notifyTopElementHandler() {
        Item item = m_elementHandlerStack.top();
        if (item != null) {
            item.getElementHandler().childElementComplete(m_lastModelElement);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        m_elementHandlerStack.top().getElementHandler().characters(ch, start, length);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }        
}