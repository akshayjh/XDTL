package org.mmx.xdtl.runtime.command;

import org.apache.log4j.Logger;
import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.runtime.ConnectionManager;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class TransactionCmd implements RuntimeCommand {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.transaction");

    private final JdbcConnection m_connection;
    private final CommandList m_commandList;

    public TransactionCmd(JdbcConnection connection, CommandList commandList) {
        m_connection = connection;
        m_commandList = commandList;
    }

    @Override
    public void run(Context context) throws Throwable {
        logger.info("Starting transaction");
        boolean prevAutoCommit = m_connection.getAutoCommit();
        m_connection.setAutoCommit(false);

        ConnectionManager cnnMgr = context.getConnectionManager();
        JdbcConnection prevDefault = cnnMgr.getDefaultJdbcConnection();
        cnnMgr.setDefaultJdbcConnection(m_connection);

        try {
            try {
                context.getEngineControl().execute(m_commandList);
                logger.trace("Committing");
                m_connection.commit();
                logger.info("Transaction committed");
            } catch (Throwable t) {
                logger.trace("Rolling back");
                m_connection.rollback();
                logger.info("Transaction rolled back");
                throw t;
            }
        } finally {
            cnnMgr.setDefaultJdbcConnection(prevDefault);
            m_connection.setAutoCommit(prevAutoCommit);
        }
    }
}
