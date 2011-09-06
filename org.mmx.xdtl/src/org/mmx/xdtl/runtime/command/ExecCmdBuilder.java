package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.command.Exec;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class ExecCmdBuilder extends AbstractCmdBuilder {
    private final ExpressionEvaluator m_exprEvaluator;
    private final TypeConverter m_typeConverter;

    private String m_shell;
    private String m_cmd;
    private String m_targetVariable;
    
    @Inject
    public ExecCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        m_exprEvaluator = exprEvaluator;
        m_typeConverter = typeConverter;
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
    	if (m_targetVariable == null || m_targetVariable.length() == 0) {
    		Constructor<? extends RuntimeCommand> ctor =
    			getRuntimeClass().getConstructor(String.class, String.class);
    		return ctor.newInstance(m_shell, m_cmd);
    	}
		
    	Constructor<? extends RuntimeCommand> ctor =
			getRuntimeClass().getConstructor(String.class, String.class, String.class);
        
        return ctor.newInstance(m_shell, m_cmd, m_targetVariable);
    }

    @Override
    protected void evaluate(Command cmd) throws Exception {
        Exec exec = (Exec) cmd;
        Context ctx = getContext();
        m_shell = m_typeConverter.toString(m_exprEvaluator.evaluate(ctx, exec.getShell()));
        m_cmd = m_typeConverter.toString(m_exprEvaluator.evaluate(ctx, exec.getCmd()));
        m_targetVariable = exec.getTargetVariable();
    }
}
