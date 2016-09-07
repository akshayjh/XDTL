/**
 * 
 */
package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.model.command.Move;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

/**
 * @author vsi
 */
public class MoveCmdBuilder extends AbstractCmdBuilder {
	private static final String CMD_NAME = "move";

	private final ExpressionEvaluator m_exprEval;
	private final TypeConverter m_typeConv;

	private String m_cmd;
	private String m_source;
	private String m_target;
	private boolean m_overwrite;

	@Inject
	public MoveCmdBuilder(ExpressionEvaluator exprEvaluator, TypeConverter typeConverter) {
		m_exprEval = exprEvaluator;
		m_typeConv = typeConverter;
	}

	/**
	 * @see org.mmx.xdtl.runtime.command.AbstractCmdBuilder#createInstance()
	 */
	@Override
	protected RuntimeCommand createInstance() throws Exception {
		if (m_cmd == null || m_cmd.equals("")) {
			// go with native implementation, if no move command if defined in config
			Constructor<? extends RuntimeCommand> ctor = MoveNativeCmd.class.getConstructor(String.class, String.class, boolean.class);
			return ctor.newInstance(m_source, m_target, m_overwrite);
		}

		Constructor<? extends RuntimeCommand> ctor = getRuntimeClass().getConstructor(String.class, String.class, boolean.class);
		return ctor.newInstance(m_cmd, m_source, m_target, m_overwrite);
	}

	/**
	 * @see org.mmx.xdtl.runtime.command.AbstractCmdBuilder#evaluate(org.mmx.xdtl.model.Command)
	 */
	@Override
	protected void evaluate(Command elem) throws Exception {
		Move cmd = (Move) elem;
		Context ctx = getContext();

		Variable cmdvar = ctx.getVariable(CMD_NAME);
		if (cmdvar == null) {
			m_cmd = null;
		} else {
			m_cmd = m_typeConv.toString(cmdvar.getValue());
		}
		
		m_source = m_typeConv.toString(m_exprEval.evaluate(ctx, cmd.getSource()));
		m_target = m_typeConv.toString(m_exprEval.evaluate(ctx, cmd.getTarget()));
		m_overwrite = m_typeConv.toBoolean(m_exprEval.evaluate(ctx, cmd.getOverwrite()));
	}
}
