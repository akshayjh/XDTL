package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.model.XdtlException;

public class Transaction extends AbstractElement implements Command {
    private final String m_connection;
    private final CommandList m_commandList = new CommandList();
    
    public Transaction(String connection) {
        m_connection = connection;
        
        if (m_connection == null) {
            throw new XdtlException("connection cannot be null");
        }
    }
    
    public CommandList getCommandList() {
        return m_commandList;
    }

    public String getConnection() {
        return m_connection;
    }
}
