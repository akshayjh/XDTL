package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Parse;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class ParseCmdBuilder extends AbstractCmdBuilder {
	
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConverter;
    private String m_source;
    private String m_rowset;
    private String m_target;
    private String m_grammar;
    private Parse.Type m_type;
    private String m_template;
    
    @Inject
    public ParseCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        super();
        m_exprEval = exprEvaluator;
        m_typeConverter = typeConverter;
    }

    @Override
    protected void evaluate(Command cmd) throws Exception {
    	Parse parse = (Parse)cmd;
    	Context ctx = getContext();
    	
        m_source    = m_typeConverter.toString(m_exprEval.evaluate(ctx, parse.getSource()));
        m_rowset    = m_typeConverter.toString(m_exprEval.evaluate(ctx, parse.getRowset()));
        m_target    = m_typeConverter.toString(m_exprEval.evaluate(ctx, parse.getTarget()));
        m_grammar   = m_typeConverter.toString(m_exprEval.evaluate(ctx, parse.getGrammar()));
        m_type      = m_typeConverter.toEnumMember(Parse.Type.class, m_exprEval.evaluate(ctx, parse.getType()));
        m_template  = m_typeConverter.toString(m_exprEval.evaluate(ctx, parse.getTemplate()));
        
        if (m_rowset == null || m_rowset.length() == 0) {
        	throw new XdtlException("'rowset' must be specified");
        }
        if (m_grammar == null || m_grammar.length() == 0) {
        	throw new XdtlException("'grammar' must be specified");
        }
    }
    
    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor = getRuntimeClass()
                .getConstructor(String.class, String.class, String.class, String.class, Parse.Type.class, String.class); //Parameters!
        
        return ctor.newInstance(m_source, m_rowset, m_target, m_grammar, m_type, m_template);
    }
}
