package org.mmx.xdtl.runtime.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;

import com.google.inject.Inject;

/**
 * JDK 1.6 scripting based expression evaluator implementation.
 *
 * @author vsi
 */
public class ScriptExpressionEvaluator implements ExpressionEvaluator {
    private final Pattern m_pattern = Pattern.compile("\\$[a-zA-Z_](?:(?:\\w+|\\:[a-zA-Z_]+)|(?:\\[.+?\\]))*|\\$\\{.*?\\}", Pattern.DOTALL);
    private final ScriptEngine m_scriptEngine;

    @Inject
    public ScriptExpressionEvaluator(ScriptEngine scriptEngine) {
        m_scriptEngine = scriptEngine;
    }

    private String getSubExpression(String match) {
        if (match.startsWith("${")) {
            return match.substring(2, match.length() - 1);
        }

        return convertToJs(match);
    }

    private String convertToJs(String expr) {
        // expr starts with '$', which we discard
        StringBuilder buf = new StringBuilder(expr.length() - 1);
        boolean betweenQuotes = false;
        boolean escape = false;

        for (int i = 1; i < expr.length(); i++) {
            char c = expr.charAt(i);
            switch (c) {
            case '\\':
                escape = !escape;
                buf.append(c);
                continue;  // to the top of the loop
            case '\'':
                if (!escape) {
                    betweenQuotes = !betweenQuotes;
                }
                break;
            case ':':
                if (!(betweenQuotes || escape)) {
                    c = '.';
                }
                break;
            }

            escape = false;
            buf.append(c);
        }

        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.mmx.xdtl.runtime.ExpressionEvaluator#evaluate(org.mmx.xdtl.runtime.Context, java.lang.String)
     */
    @Override
    public Object evaluate(Context context, String expr) {
        try {
            if (expr == null) {
                return null;
            } else if (expr.length() == 0) {
                return "";
            }

            Matcher matcher = m_pattern.matcher(expr);
            StringBuffer sb = new StringBuffer();

            m_scriptEngine.setBindings(context.getBindings(), ScriptContext.ENGINE_SCOPE);

            if (matcher.matches()) {
                String subexpr = getSubExpression(matcher.group());
                return m_scriptEngine.eval(subexpr);
            }

            matcher.reset();
            int lastMatchEndPos = 0;

            while (matcher.find()) {
                lastMatchEndPos = matcher.end();
                String subexpr = getSubExpression(matcher.group());

                Object scriptResult = m_scriptEngine.eval(subexpr);
                String replacement = "";

                if (scriptResult != null) {
                    replacement = Matcher.quoteReplacement(scriptResult.toString());
                }

                matcher.appendReplacement(sb, replacement);
            }

            checkForUnterminatedBrace(expr, lastMatchEndPos);
            matcher.appendTail(sb);
            return sb.toString();
        } catch (ScriptException e) {
            throw new XdtlException(e);
        }
    }

    private void checkForUnterminatedBrace(String expr, int offset) {
        int pos = expr.indexOf("${", offset);
        if (pos == -1) {
            return;
        }

        throw new XdtlException("Unterminated '${' in expression '" + getTextAroundPosition(expr, pos, 5, 50) + "'");
    }

    private String getTextAroundPosition(String str, int pos, int numLeadingChars, int maxLength) {
        int start = pos - numLeadingChars;
        int end = start + maxLength;

        if (start < 0) {
            end -= start;
            start = 0;
        }

        if (end > str.length()) {
            end = str.length();
        }

        String prefix = start > 0 ? "..." : "";
        String suffix = end != str.length() ? "..."  : "";
        return prefix + str.substring(start, end) + suffix;
    }
}
