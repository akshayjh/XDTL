package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.model.TextFileProperties;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.TypeConverter;

public abstract class CmdBuilderBase extends AbstractCmdBuilder {
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConv;

    public CmdBuilderBase(ExpressionEvaluator exprEval, TypeConverter typeConv) {
        super();
        m_exprEval = exprEval;
        m_typeConv = typeConv;
    }

    protected <T extends Enum<T>> RtTextFileProperties<T> evalTextFileProperties(TextFileProperties props, Class<T> typeClass) {
        T type = m_typeConv.toEnumMember(typeClass, m_exprEval.evaluate(getContext(), props.getType()));
        char delimiter = evalToChar(props.getDelimiter(), TextFileProperties.DEFAULT_DELIMITER);
        char quote = evalToChar(props.getQuote(), TextFileProperties.DEFAULT_QUOTE);
        String nul = evalToString(props.getNull());
        char escape = evalToChar(props.getEscape(), TextFileProperties.DEFAULT_ESCAPE);
        String encoding = evalToString(props.getEncoding());

        return new RtTextFileProperties<T>(type, delimiter, quote, nul, escape, encoding);
    }

    protected String evalToString(String expr) {
        return m_typeConv.toString(m_exprEval.evaluate(getContext(), expr));
    }

    protected Boolean evalToBoolean(String expr) {
        return m_typeConv.toBoolean(m_exprEval.evaluate(getContext(), expr));
    }

    protected boolean evalToBoolean(String expr, boolean deflt) {
        if (expr == null || expr.length() == 0) {
            return deflt;
        }

        return evalToBoolean(expr);
    }

    protected Character evalToChar(String expr) {
        return m_typeConv.toChar(m_exprEval.evaluate(getContext(), expr));
    }

    protected char evalToChar(String expr, char deflt) {
        if (expr == null || expr.length() == 0) {
            return deflt;
        }

        return evalToChar(expr);
    }

    protected Integer evalToInteger(String expr) {
        return m_typeConv.toInteger(m_exprEval.evaluate(getContext(), expr));
    }

    protected int evalToInteger(String expr, int deflt) {
        if (expr == null || expr.length() == 0) {
            return deflt;
        }
        return evalToInteger(expr);
    }

    protected Object eval(String expr) {
        return m_exprEval.evaluate(getContext(), expr);
    }

    protected ExpressionEvaluator getExpressionEvaluator() {
        return m_exprEval;
    }

    protected TypeConverter getTypeConverter() {
        return m_typeConv;
    }
}
