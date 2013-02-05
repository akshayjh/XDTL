package org.mmx.xdtl.runtime.command;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.model.command.Call;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.EngineControl;
import org.mmx.xdtl.runtime.impl.SimpleExpressionEvaluator;

public class CallCmdBuilderTest {
    @Test
    public void test() throws Exception {
        Call call = new Call("#first");
        call.addParameter(new Parameter("arg0", "", false, "$var"));
        call.addParameter(new Parameter("arg1", "", false, "val"));
        
        CallCmdBuilder builder = new CallCmdBuilder(new SimpleExpressionEvaluator());
        
        final Variable called = new Variable("called", false);
        
        Context context = new Context(new EngineControl() {
            @Override
            public void call(String ref, Map<String, Object> args) {
                Assert.assertEquals("#first", ref);
                Assert.assertEquals(2, args.size());
                Assert.assertEquals("varvalue", args.get("arg0"));
                Assert.assertEquals("val", args.get("arg1"));
                called.setValue(true);
            }

            @Override
            public void execute(CommandList commands) {
                Assert.fail();
            }

            @Override
            public void exit() {
                Assert.fail();
            }

            @Override
            public void callExtension(String nsUri, String name,
                    Map<String, Object> args) {
                Assert.fail();
            }

            @Override
            public void exit(int code) {
                Assert.fail();
            }
        }, null);
        
        context.addVariable(new Variable("var", "varvalue"));
        
        CallCmd cmd = (CallCmd) builder.build(context, CallCmd.class, call);
        cmd.run(context);
        Assert.assertTrue(((Boolean) called.getValue()).booleanValue());
    }
}
