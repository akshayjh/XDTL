package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Transaction;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.RuntimeCommandClassMap;

import com.google.inject.Inject;

public class TransactionCmdBuilder implements CommandBuilder {
    private ExpressionEvaluator m_exprEval;

    @Inject
    public TransactionCmdBuilder(ExpressionEvaluator expressionEvaluator) {
        m_exprEval = expressionEvaluator;
    }

    @Override
    public RuntimeCommand build(Context context,
            RuntimeCommandClassMap rtCmdClassMap, Command cmd) throws Exception {

        Transaction tx = (Transaction) cmd;
        Connection cnn = null;

        if (tx.getConnection().length() > 0) {
            Object obj = m_exprEval.evaluate(context, tx.getConnection());
            if (!(obj instanceof Connection)) {
                throw new XdtlException("Invalid connection type: '" + obj.getClass().getName() + "'");
            }

            cnn = (Connection) obj;
        }

        JdbcConnection jdbcConnection =
            context.getConnectionManager().getJdbcConnection(cnn);

        Class<? extends RuntimeCommand> rtCmdClass = rtCmdClassMap.getCommandClass(null);
        Constructor<? extends RuntimeCommand> ctor = rtCmdClass.getConstructor(JdbcConnection.class,
                CommandList.class);

        return ctor.newInstance(jdbcConnection, tx.getCommandList());
    }
}
