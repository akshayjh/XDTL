package org.mmx.xdtl.runtime.impl;

import junit.framework.Assert;

import org.junit.Test;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.model.command.Echo;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.command.EchoCmd;
import org.mmx.xdtl.runtime.command.EchoCmdBuilder;
import org.mmx.xdtl.services.MockInjector;

public class CommandInvokerTest {
    @Test
    public void test() {
        CommandMapping mapping = new CommandMapping(
                Echo.class,
                EchoCmdBuilder.class,
                EchoCmd.class);
        
        CommandMappingSet mappingSet = new CommandMappingSet();
        mappingSet.putMapping(mapping);
        
        Echo echo = new Echo("$proov");

        Context context = new Context();
        context.addVariable(new Variable("proov", "Hello"));
        
        MockInjector injector = new MockInjector(new EchoCmdBuilder(new SimpleExpressionEvaluator()));
        CommandInvoker invoker = new CommandInvokerImpl(mappingSet, injector);
        invoker.invoke(echo, context);
        Assert.assertTrue(injector.isInjectMembersCalled());
    }
}
