package org.mmx.xdtl.parser.impl;

import java.util.ArrayList;

import org.mmx.xdtl.model.SourceLocator;
import org.mmx.xdtl.parser.ElementHandler;

/**
 * Stack of element handlers. Each stack item holds an element handler and the
 * location of the corresponding starting tag in source document.
 *
 * @author vsi
 */
class ElementHandlerStack {
    static class Item {
        private final ElementHandler m_elementHandler;
        private final SourceLocator m_sourceLocator;
        private final String m_id;
        private final String m_noLog;

        public Item(ElementHandler elementHandler, SourceLocator sourceLocator, String id, String noLog) {
            super();
            m_elementHandler = elementHandler;
            m_sourceLocator = sourceLocator;
            m_id = id;
            m_noLog = noLog;
        }

        public ElementHandler getElementHandler() {
            return m_elementHandler;
        }

        public SourceLocator getSourceLocator() {
            return m_sourceLocator;
        }

        public String getId() {
            return m_id;
        }

        public String getNoLog() {
            return m_noLog;
        }
    }

    private final ArrayList<Item> m_list = new ArrayList<Item>();

    public void push(ElementHandler elementHandler, SourceLocator sourceLocator,
            String id, String noLog) {
        m_list.add(new Item(elementHandler, sourceLocator, id, noLog));
    }

    public Item pop() {
        return m_list.remove(m_list.size() - 1);
    }

    public Item top() {
        if (m_list.size() == 0) {
            return null;
        }

        return m_list.get(m_list.size() - 1);
    }
}
