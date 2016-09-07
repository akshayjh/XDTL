package org.mmx.xdtl.runtime.impl;

import org.mmx.xdtl.debugger.Debugger;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.TypeConverter;
import org.mmx.xdtl.services.Injector;

import com.google.inject.Inject;

public class DebugCommandInvoker extends CommandInvokerImpl {
    private Debugger m_debugger;

    @Inject
    DebugCommandInvoker(Debugger debugger, CommandMappingSet mappings,
            Injector injector, ExpressionEvaluator exprEvaluator,
            TypeConverter typeConverter) {
        super(mappings, injector, exprEvaluator, typeConverter);
        m_debugger = debugger;
    }

    @Override
    public void invoke(Command cmd, Context context) {
        m_debugger.preInvoke(cmd, context);
        super.invoke(cmd, context);
        m_debugger.postInvoke(cmd);
    }
}
