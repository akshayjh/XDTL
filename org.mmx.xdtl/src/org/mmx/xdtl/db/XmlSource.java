package org.mmx.xdtl.db;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.mmx.xdtl.model.XdtlException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlSource implements Source {
    private SAXParser m_parser;
    private String m_sourceUri;

    public XmlSource(String sourceUri) {
        m_sourceUri = sourceUri;

        try {
            m_parser = createParser();
        } catch (Exception e) {
            throw new XdtlException(e);
        }
    }

    private SAXParser createParser() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newSAXParser();
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void fetchRows(RowHandler rowHandler) throws Exception {
        SaxEventHandler handler = new SaxEventHandler(rowHandler);
        m_parser.parse(m_sourceUri, handler);
    }

    private static class Attr {
        private String name;
        private String value;

        public Attr(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    private static class ElemNode {
        private String m_name;
        private String m_value;
        private ArrayList<Attr> m_attributes;
        private ArrayList<ElemNode> m_children;

        public ElemNode(String name, Attributes attributes) {
            m_name = name;

            for (int i = 0; i < attributes.getLength(); i++) {
                String attrName  = attributes.getLocalName(i);
                String attrValue = attributes.getValue(i);
                addAttribute(new Attr(attrName, attrValue));
            }
        }

        public String getName() {
            return m_name;
        }

        public boolean hasValue() {
            return m_value != null && m_value.length() > 0;
        }

        public String getValue() {
            return m_value;
        }

        public void appendToValue(String str) {
            if (m_value == null) {
                m_value = str;
            } else {
                m_value += str;
            }
        }

        public List<Attr> getAttributes() {
            return m_attributes;
        }

        public void addAttribute(Attr attr) {
            if (m_attributes == null) {
                m_attributes = new ArrayList<Attr>();
            }

            m_attributes.add(attr);
        }

        public List<ElemNode> getChildren() {
            return m_children;
        }

        public boolean hasChildren() {
            return m_children != null;
        }

        public void addChild(ElemNode child) {
            if (m_children == null) {
                m_children = new ArrayList<ElemNode>();
            }

            m_children.add(child);
        }
    }

    private static class RowGenerator {
        private RowHandler m_rowHandler;
        private ArrayList<String> m_columns = new ArrayList<String>();
        private ArrayList<String> m_values = new ArrayList<String>();

        public RowGenerator(RowHandler rowHandler) {
            m_rowHandler = rowHandler;
        }

        public void generateRows(ElemNode rootNode) throws Exception {
            processNode("", rootNode);
            sendRowToRowHandler();
            m_columns.clear();
            m_values.clear();
        }

        private void processNode(String columnPrefix, ElemNode node) throws Exception {
            columnPrefix = getColumnName(columnPrefix, node.getName());

            if (node.hasValue() || !node.hasChildren()) {
                m_columns.add(columnPrefix);
                m_values.add(node.getValue());
            }

            attributesToColumns(columnPrefix, node.getAttributes());

            if (node.hasChildren()) {
                int columnsSize = m_columns.size();
                String prevChildName = "";
                for (ElemNode childNode: node.getChildren()) {
                    if (prevChildName.equals(childNode.getName())) {
                        sendRowToRowHandler();
                        shrinkArraysToSize(columnsSize);
                    }

                    processNode(columnPrefix, childNode);
                    prevChildName = childNode.getName();
                }
            }
        }

        private void shrinkArraysToSize(int newSize) {
            for (int i = m_columns.size() - 1; i >= newSize; i--) {
                m_columns.remove(i);
                m_values.remove(i);
            }
        }

        private void sendRowToRowHandler() throws Exception {
            m_rowHandler.handleRow(m_values.toArray(), m_columns);
        }

        private void attributesToColumns(String columnPrefix, List<Attr> attributes) {
            if (attributes == null) return;
            for (Attr attr: attributes) {
                m_columns.add(getColumnName(columnPrefix, attr.getName()));
                m_values.add(attr.getValue());
            }
        }

        private String getColumnName(String columnPrefix, String name) {
            if (columnPrefix == null || columnPrefix.length() == 0) return name;
            return columnPrefix + "_" + name;
        }
    }

    private static class SaxEventHandler extends DefaultHandler {
        private ArrayDeque<ElemNode> elementStack = new ArrayDeque<ElemNode>();
        private RowGenerator m_rowGenerator;

        public SaxEventHandler(RowHandler rowHandler) {
            m_rowGenerator = new RowGenerator(rowHandler);
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            ElemNode node = new ElemNode(localName, attributes);
            if (elementStack.size() > 1) {
                ElemNode parentNode = elementStack.peek();
                parentNode.addChild(node);
            }

            elementStack.push(node);
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            ElemNode node = elementStack.pop();
            if (elementStack.size() == 1) {
                generateRows(node);
            }
        }

        private void generateRows(ElemNode node) {
            try {
                m_rowGenerator.generateRows(node);
            } catch (Exception e) {
                throw new XdtlException(e);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            ElemNode node = elementStack.peek();
            node.appendToValue(String.valueOf(ch, start, length).trim());
        }
    }

    @Override
    public List<Column> getColumns() throws Exception {
        throw new Exception("getColumns() is not implemented");
    }
}
