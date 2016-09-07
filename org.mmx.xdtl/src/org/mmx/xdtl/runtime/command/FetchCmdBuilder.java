package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.db.JdbcConnection;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Fetch;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class FetchCmdBuilder extends CmdBuilderBase {
    private String m_source;
    private String m_target;
    private JdbcConnection m_connection;
    private JdbcConnection m_destination;
    private boolean m_overwrite;
    private boolean m_header;
    private String m_rowset;
    private RtTextFileProperties<Fetch.Type> m_textFileProperties;

    @Inject
    public FetchCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        super(exprEvaluator, typeConverter);
    }

    @Override
    protected void evaluate(Command cmd) throws Exception {
        Fetch fetch = (Fetch) cmd;
        Context ctx = getContext();
        m_source    = evalToString(fetch.getSource());
        m_target    = evalToString(fetch.getTarget());
        m_textFileProperties = evalTextFileProperties(fetch.getTextFileProperties(), Fetch.Type.class);

        m_overwrite = evalToBoolean(fetch.getOverwrite(), false);
        m_header = evalToBoolean(fetch.getHeader(), false);
        m_rowset = evalToString(fetch.getRowset());

        if (m_target != null && m_target.length() == 0) {
            m_target = null;
        }

        if (m_rowset != null && m_rowset.length() == 0) {
            m_rowset = null;
        }

        if (m_target == null && m_rowset == null) {
            throw new XdtlException("Either 'target' or 'rowset' must be specified");
        }

        Object cnnObj = eval(fetch.getConnection());
        if (cnnObj != null && !(cnnObj instanceof Connection)) {
            throw new XdtlException("Invalid connection type: '" + cnnObj.getClass() + "'");
        }

        m_connection = ctx.getConnectionManager().getJdbcConnection((Connection) cnnObj);

        cnnObj = eval(fetch.getDestination());
        if (cnnObj != null) {
            if (!(cnnObj instanceof Connection)) {
                throw new XdtlException("Invalid destination connection type: '" + cnnObj.getClass() + "'");
            }

            m_destination = ctx.getConnectionManager().getJdbcConnection((Connection) cnnObj);
        }
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor = getRuntimeClass()
                .getConstructor(String.class, JdbcConnection.class,
                        boolean.class, RtTextFileProperties.class,
                        boolean.class, String.class, String.class,
                        JdbcConnection.class);

        return ctor.newInstance(m_source, m_connection, m_overwrite,
                m_textFileProperties, m_header, m_target, m_rowset,
                m_destination);
    }
}
