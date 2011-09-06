/**
 * 
 */
package org.mmx.xdtl.runtime.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.util.ContextToBindingsAdapter;

import com.google.inject.Inject;

/**
 * JDK 1.6 scripting based expression evaluator implementation.
 * 
 * @author vsi
 */
public class ScriptExpressionEvaluator implements ExpressionEvaluator {
    private final Pattern m_pattern = Pattern.compile("\\$[\\w]+|\\$\\{.+?\\}");
    private final ScriptEngine m_scriptEngine;

    @Inject
    public ScriptExpressionEvaluator(ScriptEngine scriptEngine) {
        m_scriptEngine = scriptEngine;
    }
    
    private String getSubExpression(String match) {
        if (match.startsWith("${")) {
            return match.substring(2, match.length() - 1);
        }
        
        return match.substring(1);
    }

    /* (non-Javadoc)
     * @see org.mmx.xdtl.runtime.ExpressionEvaluator#evaluate(org.mmx.xdtl.runtime.Context, java.lang.String)
     */
    @Override
    public Object evaluate(Context context, String expr) {
        try {
            if (expr == null) {
                return null;
            }
            
            Matcher matcher = m_pattern.matcher(expr);
            StringBuffer sb = new StringBuffer();

            if (matcher.matches()) {
                String subexpr = getSubExpression(matcher.group());
                return m_scriptEngine.eval(subexpr, new ContextToBindingsAdapter(context));
            }

            matcher.reset();
            Bindings bindings = null;
            
            while (matcher.find()) {                
                String subexpr = getSubExpression(matcher.group());
                if (bindings == null) {
                    bindings = new ContextToBindingsAdapter(context);
                }
                
                Object scriptResult = m_scriptEngine.eval(subexpr, bindings);
                String replacement = "";
                
                if (scriptResult != null) {
                    replacement = Matcher.quoteReplacement(scriptResult.toString());
                }
                
                matcher.appendReplacement(sb, replacement);
            }

            matcher.appendTail(sb);
            return sb.toString();
        } catch (ScriptException e) {
            throw new XdtlException(e);
        }
    }    
}
