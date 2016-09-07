package org.mmx.xdtl.runtime.command.send;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.command.Send;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;
import org.mmx.xdtl.runtime.command.CmdBuilderBase;
import org.mmx.xdtl.runtime.command.RtTextFileProperties;

import com.google.inject.Inject;

public class SendCmdBuilder extends CmdBuilderBase {
	private Object m_source;
	private String m_target;
	private boolean m_overwrite;
	private RtTextFileProperties<Send.Type> m_textFileProperties;
	private boolean m_header;
	private int m_skip;
	private Object m_rowSet;

    @Inject
    public SendCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        super(exprEvaluator, typeConverter);
    }

    @Override
    protected void evaluate(Command cmd) throws Exception {
        Send elem = (Send) cmd;
        m_target = evalToString(elem.getTarget());

        m_source = eval(elem.getSource());
        m_overwrite = evalToBoolean(elem.getOverwrite());
        m_textFileProperties = evalTextFileProperties(elem.getTextFileProperties(), Send.Type.class);
        m_header = evalToBoolean(elem.getHeader(), false);
        m_skip = evalToInteger(elem.getSkip(), 0);
        m_rowSet = eval(elem.getRowset());
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor =
                getRuntimeClass().getConstructor(Object.class, String.class,
                        boolean.class, RtTextFileProperties.class,
                        boolean.class, int.class, Object.class);
        return ctor.newInstance(m_source, m_target, m_overwrite,
                m_textFileProperties, m_header, m_skip, m_rowSet);
    }
}
