package org.mmx.xdtl.model.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Parameter;

public class Render extends AbstractElement implements Command {
    private final String m_templateUrl;
    private final String m_source;
    private final String m_target;
    private final String m_rowset;
    private final ArrayList<Parameter> m_parameters = new ArrayList<Parameter>();
    
    public Render(String templateUrl, String source, String target, String rowset) {
        super();
        m_templateUrl = templateUrl;
        m_source = source;
        m_target = target;
        m_rowset = rowset;
        
    }

    public String getTemplateUrl() {
        return m_templateUrl;
    }

    public String getSource() {
        return m_source;
    }

    public String getTarget() {
        return m_target;
    }

    public String getRowset() {
        return m_rowset;
    }
    
    public void addParameter(Parameter param) {
        m_parameters.add(param);
    }
    
    public List<Parameter> getParameterList() {
        return Collections.unmodifiableList(m_parameters);
    }
}
