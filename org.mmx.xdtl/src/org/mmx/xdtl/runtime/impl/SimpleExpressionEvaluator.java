package org.mmx.xdtl.runtime.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;

/**
 * Simple regexp-based expression evaluator.
 * 
 * @author vsi
 */
public class SimpleExpressionEvaluator implements ExpressionEvaluator {
    private final Pattern m_pattern = Pattern.compile("\\$[\\w]+");

    /**
     * Evaluates expressions by replacing $[A-Za-z_0-9]+ patterns with
     * corresponding values of variables from execution context. Expression
     * consisting only of single variable reference, like $abc, will return the
     * value unaltered. Any other expressions convert all referenced variables
     * to strings before replacement.
     */
    @Override
    public Object evaluate(Context context, String expr) {
        if (expr == null) {
            return null;
        }
        
        Matcher matcher = m_pattern.matcher(expr);
        StringBuffer sb = new StringBuffer();

        if (matcher.matches()) {
            String varName = matcher.group().substring(1);
            return context.getVariableValue(varName);
        }

        matcher.reset();
        
        while (matcher.find()) {
            String varName = matcher.group().substring(1);
            Object varValue = context.getVariableValue(varName);
            String replacement = "";
            
            if (varValue != null) {
                replacement = Matcher.quoteReplacement(varValue.toString());
            }
            
            matcher.appendReplacement(sb, replacement);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
}
