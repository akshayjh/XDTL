package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.command.Transaction;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class TransactionHandler extends AbstractElementHandler {
    Transaction m_transaction;
    
    @Override
    public Element endElement() {
        return m_transaction;
    }

    @Override
    public void startElement(Attributes attr) {
        m_transaction = new Transaction(attr.getStringValue("connection"));
    }

    @Override
    public void childElementComplete(Object child) {
        if (child instanceof Command) {
            m_transaction.getCommandList().add((Command) child);
        }
    }
}
