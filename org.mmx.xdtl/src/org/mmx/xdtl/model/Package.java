package org.mmx.xdtl.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The <code>Package</code> represents the entire XDTL package.
 * 
 * @author vsi
 */
public class Package extends AbstractElement {
    private static final String DEFAULT_URL = "file://";
    
    private URL m_url;
    private final String m_name;
    private final String m_onError;
    private final String m_resume;
    
    private TaskList m_tasks = new TaskList();
    private final ArrayList<Parameter> m_parameterList = new ArrayList<Parameter>();
    private final ArrayList<Variable> m_variableList = new ArrayList<Variable>();
    private final ArrayList<Connection> m_connectionList = new ArrayList<Connection>();

    /**
     * Creates a new XDTL package with given <code>name</code>.
     * @param name The name of the package.
     */
    public Package(String name, String onError, String resume) {
        try {
            m_url = new URL(DEFAULT_URL);
        } catch (MalformedURLException e) {
            throw new XdtlException("Invalid default URL", e);
        }

        m_name = name;
        m_onError = onError;
        m_resume = resume;
        
        assert m_onError != null;        
    }

    public Package(String name, String onError, String resume, URL url) {
        validateUrl(url);
        m_url = url;
        m_name = name;
        m_onError = onError;
        m_resume = resume;

        assert m_onError != null;        
    }
    
    /**
     * Appends the variable to the end of the list of variables.
     * @param var The variable to add.
     */
    public void addVariable(Variable var) {
        m_variableList.add(var);
    }
    
    /**
     * Appends the parameter to the end of the list of parameters.
     * @param param The parameter to add.
     */
    public void addParameter(Parameter param) {
        m_parameterList.add(param);
    }
    
    /**
     * Appends the connection to the end of the list of connections.
     * @param cnn The connection to add.
     */
    public void addConnection(Connection cnn) {
        m_connectionList.add(cnn);
    }
    
    /**
     * Returns the list of tasks.
     * @return list of tasks.
     */
    public TaskList getTasks() {
        return m_tasks;
    }

    /**
     * Replaces entire list of tasks with tasks from {@code taskList}.
     * @param taskList The new list of tasks.
     */
    public void setTasks(TaskList taskList) {
        m_tasks = taskList;
    }
    
    /**
     * Returns the list of variables.
     * @return list of variables.
     */
    public List<Variable> getVariableList() {
        return Collections.unmodifiableList(m_variableList);
    }

    /**
     * Returns the list of parameters.
     * @return list of parameters.
     */
    public List<Parameter> getParameterList() {
        return Collections.unmodifiableList(m_parameterList);
    }
    
    /**
     * Returns the list of connections.
     * @return list of connections.
     */
    public List<Connection> getConnectionList() {
        return Collections.unmodifiableList(m_connectionList);
    }
    
    /**
     * Returns a task with given {@code name}.
     * @param name The name of the task
     * @return A task or {@code null} when task was not found.
     */
    public Task getTask(String name) {
        return m_tasks.get(name);
    }

    /**
     * Returns the name of the package.
     * @return The name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the URL of the error handler for this package.
     * @return URL of the package/task, which is executed in case of errors or
     * empty string (no error handler). 
     */
    public String getOnError() {
        return m_onError;
    }

    public URL getUrl() {
        return m_url;
    }
    
    public void setUrl(URL url) {
        validateUrl(url);
        m_url = url;
    }
    
    private static void validateUrl(URL url) {
        if (url == null) {
            throw new XdtlException("url cannot be null");
        }
        
        String ref = url.getRef();
        if (ref != null) {
            throw new XdtlException("package url must not have 'reference' part: " + url);
        }
    }

    public String getResume() {
        return m_resume;
    }
}
