package org.mmx.xdtl.model.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Parameter;

public class Call extends AbstractElement implements Command {
    private final String m_ref;
    private final List<Parameter> m_parameters = new ArrayList<Parameter>();
    
    public Call(String ref) {
        m_ref = ref;
    }
    
    public void addParameter(Parameter param) {
        m_parameters.add(param);
    }
    
    public List<Parameter> getParameters() {
        return Collections.unmodifiableList(m_parameters);
    }

    public String getRef() {
        return m_ref;
    }
}
