package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.FileTransfer;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class FileTransferCmdBuilder extends AbstractCmdBuilder {
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConverter;

    private String m_cmdName;
    private String m_cmd;
    private String m_source;
    private String m_target;
    private boolean m_overwrite;
    
    @Inject
    public FileTransferCmdBuilder(ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        m_exprEval = exprEvaluator;
        m_typeConverter = typeConverter;
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor =
            getRuntimeClass().getConstructor(String.class, String.class,
                    String.class, boolean.class, String.class);
        
        return ctor.newInstance(m_cmd, m_source, m_target, m_overwrite,
                m_cmdName);
    }

    @Override
    protected void evaluate(Command obj) throws Exception {
        FileTransfer cmd = (FileTransfer) obj;
        Context ctx = getContext();
        
        // Determine command name from command class name
        m_cmdName = obj.getClass().getSimpleName().toLowerCase();

        m_cmd = cmd.getCmd();
        if (m_cmd == null || m_cmd.length() == 0) {
            m_cmd = m_typeConverter.toString(ctx.getVariableValue(m_cmdName));
        } else {
            m_cmd = m_typeConverter.toString(m_exprEval.evaluate(ctx, m_cmd));
        }
        
        Object source = m_exprEval.evaluate(ctx, cmd.getSource());
        m_source = asString(source, cmd);
        
        Object target = m_exprEval.evaluate(ctx, cmd.getTarget());
        m_target = asString(target, cmd);
        
        m_overwrite = m_typeConverter.toBoolean(m_exprEval.evaluate(ctx,
                cmd.getOverwrite()));        
    }

    private String asString(Object src, Command cmd) {
        if (src instanceof Connection) {
            Connection cnn = (Connection) src;
            if (!cnn.isFile() && !cnn.isUri()) {
                throw new XdtlException("only file or uri connections are allowed",
                        cmd.getSourceLocator());
            }
            
            return cnn.getValue();
        } else if (src instanceof String) {
            return (String) src;
        } else {
            throw new XdtlException("Invalid type: connection or string " +
                    "expected, actual type is '" + src.getClass() + "'",
                    cmd.getSourceLocator());
        }
    }
}
