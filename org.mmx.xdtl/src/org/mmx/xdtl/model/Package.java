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
    private final ArrayList<Element> m_header = new ArrayList<Element>();

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
        m_header.add(var);
    }

    /**
     * Appends the parameter to the end of the list of parameters.
     * @param param The parameter to add.
     */
    public void addParameter(Parameter param) {
        m_header.add(param);
    }

    /**
     * Appends the configuration to the end of the list of configurations.
     * @param config The config element to add.
     */
    public void addConfig(Config config) {
        m_header.add(config);
    }

    /**
     * Appends the connection to the end of the list of connections.
     * @param cnn The connection to add.
     */
    public void addConnection(Connection cnn) {
        m_header.add(cnn);
    }

    /**
     * Appends the script to the end of the list of scripts.
     * @param cnn The script to add.
     */
    public void addScript(Script script) {
        m_header.add(script);
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
     * Returns the list of header elements.
     * @return list of header elements.
     */
    public List<Element> getHeader() {
        return Collections.unmodifiableList(m_header);
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
