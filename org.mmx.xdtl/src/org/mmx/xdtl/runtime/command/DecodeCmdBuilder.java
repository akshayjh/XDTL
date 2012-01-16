package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Decode;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class DecodeCmdBuilder extends AbstractCmdBuilder {
	
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConverter;
    private String m_source;
    private String m_target;
    private Decode.Type m_type;
    
    @Inject
    public DecodeCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        super();
        m_exprEval = exprEvaluator;
        m_typeConverter = typeConverter;
    }

    @Override
    protected void evaluate(Command cmd) throws Exception {
    	Decode decode = (Decode)cmd;
    	Context ctx = getContext();
    	
        m_source    = m_typeConverter.toString(m_exprEval.evaluate(ctx, decode.getSource()));
        m_target    = m_typeConverter.toString(m_exprEval.evaluate(ctx, decode.getTarget()));
        m_type      = m_typeConverter.toEnumMember(Decode.Type.class, m_exprEval.evaluate(ctx, decode.getType()));
        
        if (m_target == null || m_target.length() == 0) {
        	throw new XdtlException("'target' must be specified");
        }
    }
    
    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor = getRuntimeClass()
                .getConstructor(String.class, String.class,
                        Decode.Type.class);
        
        return ctor.newInstance(m_source, m_target, m_type);
    }
}
