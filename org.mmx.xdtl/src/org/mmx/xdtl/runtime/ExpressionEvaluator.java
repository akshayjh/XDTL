package org.mmx.xdtl.runtime;


public interface ExpressionEvaluator {
    public Object evaluate(Context context, String expr);
}
