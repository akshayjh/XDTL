package org.mmx.xdtl.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Task extends AbstractElement {
    private final String m_name;
    private CommandList m_commandList = new CommandList();
    private final ArrayList<Parameter> m_parameterList = new ArrayList<Parameter>();
    private final String m_onError;
    private final String m_connection;
    private final String m_resume;
    
    public Task(String name) {
        m_name = name;
        m_connection = "";
        m_onError = "";
        m_resume = "0";
        validate();
    }
    
    public Task(String name, String connection, String onError, String resume) {
        m_name = name;
        m_connection = connection;
        m_onError = onError;
        m_resume = resume;
        validate();
    }
    
    public String getName() {
        return m_name;
    }
    
    public void addCommand(Command cmd) {
        m_commandList.add(cmd);
    }
    
    public void addParameter(Parameter param) {
        m_parameterList.add(param);
    }
    
    public List<Parameter> getParameterList() {
        return Collections.unmodifiableList(m_parameterList);
    }

    public String getOnError() {
        return m_onError;
    }

    public String getResume() {
        return m_resume;
    }

    public CommandList getCommandList() {
        return m_commandList;
    }
    
    public void setCommandList(CommandList commandList) {
        m_commandList = commandList;
    }    

    public String getConnection() {
        return m_connection;
    }
    
    private void validate() {
        if (m_name == null) {
            throw new XdtlException("name cannot be null");
        }

        if (m_connection == null) {
            throw new XdtlException("connection cannot be null");
        }

        if (m_onError == null) {
            throw new XdtlException("onError cannot be null");
        }
    }
}
