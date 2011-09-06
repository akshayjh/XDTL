package org.mmx.xdtl.runtime.command;

import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

public class EchoCmd implements RuntimeCommand {
    private String m_message;

    public EchoCmd(String message) {
        m_message = message;
    }
    
    public String getMessage() {
        return m_message;
    }

    @Override
    public void run(Context context) {
        System.out.println(m_message);
    }
}
