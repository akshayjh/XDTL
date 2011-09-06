package org.mmx.xdtl.runtime.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.Context;

public class SimpleExpressionEvaluatorTest {
    @Test
    public void test() {
       SimpleExpressionEvaluator ev = new SimpleExpressionEvaluator();
       Context context = new Context();
       Object result = ev.evaluate(context, "test");
       assertEquals("test", result);

       try {
           result = ev.evaluate(context, "$test");
           fail("Undefined variable should throw");
       } catch (XdtlException e) {
       }
       
       context.defineVariable(new Variable("test123", "one"));
       context.defineVariable(new Variable("test456", "two"));
       
       result = ev.evaluate(context, "$test123");
       assertEquals("one", result);

       result = ev.evaluate(context, "test123");
       assertEquals("test123", result);
       
       result = ev.evaluate(context, "$test456");
       assertEquals("two", result);       
    }
    
    @Test
    public void testConcat() {
        SimpleExpressionEvaluator ev = new SimpleExpressionEvaluator();
        Context context = new Context();
        context.defineVariable(new Variable("test123", "one"));
        context.defineVariable(new Variable("test456", "two"));
        context.defineVariable(new Variable("obj", new Integer(42)));
        
        Object result = ev.evaluate(context, "http://$test123/$test456/$obj.txt");
        assertEquals(String.class, result.getClass());
        assertEquals("http://one/two/42.txt", result);
    }

    @Test
    public void testObjectRef() {
        SimpleExpressionEvaluator ev = new SimpleExpressionEvaluator();
        Context context = new Context();

        context.defineVariable(new Variable("obj", new Integer(42)));
        Object result = ev.evaluate(context, "$obj");
        assertEquals(Integer.class, result.getClass());
        assertEquals(42, result);
    }

    @Test
    public void testVarAtStartOfLine() {
        SimpleExpressionEvaluator ev = new SimpleExpressionEvaluator();
        Context context = new Context();

        context.defineVariable(new Variable("source", "srcdir"));
        String result = (String) ev.evaluate(context, "$source/proov.txt");
        assertEquals("srcdir/proov.txt", result);
    }
    
    @Test
    public void testSpecialChars() {
        SimpleExpressionEvaluator ev = new SimpleExpressionEvaluator();
        Context context = new Context();

        context.defineVariable(new Variable("source", "$3"));
        String result = (String) ev.evaluate(context, "$source/proov.txt");
        assertEquals("$3/proov.txt", result);
    }
}
