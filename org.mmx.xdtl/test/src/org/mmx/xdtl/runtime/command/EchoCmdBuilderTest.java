package org.mmx.xdtl.runtime.command;

import junit.framework.Assert;

import org.junit.Test;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.model.command.Echo;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.impl.SimpleExpressionEvaluator;

public class EchoCmdBuilderTest {
    @Test
    public void test() throws Exception {
        Echo node = new Echo("$proov");
        EchoCmdBuilder builder = new EchoCmdBuilder(new SimpleExpressionEvaluator());
        
        Context context = new Context();
        context.addVariable(new Variable("proov", "test"));
        
        EchoCmd cmd = (EchoCmd) builder.build(context, EchoCmd.class, node);
        Assert.assertEquals("test", cmd.getMessage());
        Assert.assertEquals(cmd.getClass(), EchoCmd.class);
    }
}
