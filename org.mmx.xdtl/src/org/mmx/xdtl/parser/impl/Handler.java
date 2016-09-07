/**
 *
 */
package org.mmx.xdtl.parser.impl;

import org.mmx.xdtl.debugger.Debugger;
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
    private static final String XDTL_URI = "http://xdtl.org/xdtl";

    private final ElementHandlerStack m_elementHandlerStack = new ElementHandlerStack();
    private final ElementHandlerSet m_elementHandlerSet;
    private final String m_documentUrl;

    private Locator m_locator;
    private Element m_lastModelElement;
    private Debugger m_debugger;
    private boolean m_debugBreak;

    public Handler(String documentUrl, ElementHandlerSet elementHandlerSet, Debugger debugger) {
        m_documentUrl = documentUrl;
        m_elementHandlerSet = elementHandlerSet;
        m_debugger = debugger;
    }

    @Override
    public void startElement(String nsUri, String localName, String qName,
            Attributes attr) throws SAXException {

        Class<? extends ElementHandler> clazz = getElementHandlerClass(nsUri,
                localName);

        ElementHandler elementHandler;

        try {
            elementHandler = clazz.newInstance();
        } catch (Exception e) {
            throw new SAXException("Failed to instantiate handler for element '" + localName + "'", e);
        }

        org.mmx.xdtl.parser.Attributes wrappedAttrs = new AttributesImpl(attr);

        SourceLocator locator = new SourceLocator(m_documentUrl, m_locator.getLineNumber(), localName);
        if (m_debugBreak) {
            m_debugger.addBreakpoint(locator);
            m_debugBreak = false;
        }

        m_elementHandlerStack.push(elementHandler, locator,
                wrappedAttrs.getStringValue("id", ""),
                wrappedAttrs.getStringValue("nolog", "0"));

        elementHandler.startElement(nsUri, localName, wrappedAttrs);
    }

    /**
     * Lookup element handler class corresponding to element name.
     *
     * @param localName
     * @return
     * @throws SAXException
     */
    private Class<? extends ElementHandler> getElementHandlerClass(String nsUri,
            String elementName) throws SAXException {

        Class<? extends ElementHandler> clazz;
        if (XDTL_URI.equalsIgnoreCase(nsUri)) {
            clazz = m_elementHandlerSet.get(elementName);
        } else {
            clazz = m_elementHandlerSet.getDefault();
        }

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
        m_lastModelElement.setNoLog(stackItem.getNoLog());
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

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        if (m_debugger != null && "debug".equals(target)) {
            m_debugBreak = true;
        }
    }
}
