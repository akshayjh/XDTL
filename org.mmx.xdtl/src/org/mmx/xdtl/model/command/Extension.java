package org.mmx.xdtl.model.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Parameter;

public class Extension extends AbstractElement implements Command {
    private final String m_nsUri;
    private final String m_name;
    private final ArrayList<Parameter> m_params = new ArrayList<Parameter>();

    public Extension(String nsUri, String name) {
        m_nsUri = nsUri;
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void addParameter(Parameter param) {
        m_params.add(param);
    }
    
    public List<Parameter> getParameters() {
        return Collections.unmodifiableList(m_params);
    }

    public String getNsUri() {
        return m_nsUri;
    }
}
