package org.mmx.xdtl.model.command;

import java.util.ArrayList;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Parameter;

public class Extension extends AbstractElement implements Command {
    private final String m_name;
    private final ArrayList<Parameter> m_attrs = new ArrayList<Parameter>();

    public Extension(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void addParameter(Parameter param) {
        m_attrs.add(param);
    }
}
