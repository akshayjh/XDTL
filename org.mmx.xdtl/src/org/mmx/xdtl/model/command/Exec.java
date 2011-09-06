package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.AbstractElement;

public class Exec extends AbstractElement implements Command {
    private String m_cmd;
    private String m_shell;
    private String m_targetVariable;

    public Exec() {
    }
    
    public Exec(String shell, String cmd) {
        super();
        m_shell = shell;
        m_cmd = cmd;
    }
    
    public Exec(String shell, String cmd, String targetVariable) {
        this(shell, cmd);
        m_targetVariable = targetVariable;
    }

    public String getShell() {
        return m_shell;
    }

    public void setShell(String shell) {
        m_shell = shell;
    }

    public String getCmd() {
        return m_cmd;
    }

    public void setCmd(String cmd) {
        m_cmd = cmd;
    }
    
    public String getTargetVariable() {
    	return m_targetVariable;
    }
    
    public void setTargetVariable(String targetVariable) {
    	m_targetVariable = targetVariable;
    }
}
