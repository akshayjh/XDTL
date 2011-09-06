package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.runtime.ConnectionManager;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionCmd implements RuntimeCommand {
    private final Logger m_logger = LoggerFactory.getLogger(TransactionCmd.class);
    
    private final JdbcConnection m_connection;
    private final CommandList m_commandList;
    
    public TransactionCmd(JdbcConnection connection, CommandList commandList) {
        m_connection = connection;
        m_commandList = commandList;
    }
    
    @Override
    public void run(Context context) throws Throwable {
        m_logger.info("Starting transaction");
        boolean prevAutoCommit = m_connection.getAutoCommit();
        m_connection.setAutoCommit(false);

        ConnectionManager cnnMgr = context.getConnectionManager();
        JdbcConnection prevDefault = cnnMgr.getDefaultJdbcConnection(); 
        cnnMgr.setDefaultJdbcConnection(m_connection);

        try {
            try {
                context.getEngineControl().execute(m_commandList);
                m_logger.debug("Committing");
                m_connection.commit();
                m_logger.info("Transaction committed");
            } catch (Throwable t) {
                m_logger.error("Rolling back", t);
                m_connection.rollback();
                m_logger.info("Transaction rolled back");
                throw t;
            }
        } finally {
            cnnMgr.setDefaultJdbcConnection(prevDefault);
            m_connection.setAutoCommit(prevAutoCommit);
        }
    }
}
