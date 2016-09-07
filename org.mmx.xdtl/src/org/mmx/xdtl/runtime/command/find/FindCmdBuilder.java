package org.mmx.xdtl.runtime.command.find;

import java.io.File;
import java.lang.reflect.Constructor;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.command.Find;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;
import org.mmx.xdtl.runtime.command.CmdBuilderBase;

import com.google.inject.Inject;

public class FindCmdBuilder extends CmdBuilderBase {
    private String m_source;
    private String m_match;
    private boolean m_recursive;
    private String m_rowset;

    @Inject
    public FindCmdBuilder(ExpressionEvaluator exprEval, TypeConverter typeConv) {
        super(exprEval, typeConv);
    }

    @Override
    protected void evaluate(Command elem) throws Exception {
        Find cmd = (Find) elem;
        m_source = evalToString(cmd.getSource());
        m_match = evalToString(cmd.getMatch());
        m_recursive = evalToBoolean(cmd.getRecursive(), true);
        m_rowset = evalToString(cmd.getRowset());

        String fileSeparator = File.separator;
        if (!"/".equals(fileSeparator)) {
            if ("\\".equals(fileSeparator)) {   // single backslash
                fileSeparator = "\\\\\\\\";     // double backslash after replacement
            }

            m_match = m_match.replaceAll("/", fileSeparator);
        }
    }

    @Override
    protected RuntimeCommand createInstance() throws Exception {
        Constructor<? extends RuntimeCommand> ctor =
                getRuntimeClass().getConstructor(String.class, String.class,
                        boolean.class, String.class);

            return ctor.newInstance(m_source, m_match, m_recursive, m_rowset);
    }
}
