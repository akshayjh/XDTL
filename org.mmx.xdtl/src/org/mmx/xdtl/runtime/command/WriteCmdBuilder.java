package org.mmx.xdtl.runtime.command;

import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;

import com.google.inject.Inject;

public class WriteCmdBuilder extends ReadWriteCmdBuilder {

    @Inject
    public WriteCmdBuilder(ExpressionEvaluator exprEval,
            TypeConverter typeConverter) {
        super(exprEval, typeConverter);
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor =
                getRuntimeClass().getConstructor(String.class, String.class,
                        String.class, boolean.class, String.class, String.class, String.class,
                        String.class, Connection.class);

        RuntimeCommand rtCmd = ctor.newInstance(getSource(), getTarget(), getType(),
                isOverwrite(), getDelimiter(), getQuote(), getEncoding(), getEscape(),
                getConnection());
        
        return rtCmd;
    }
}
