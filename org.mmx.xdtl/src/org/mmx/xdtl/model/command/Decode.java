package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public class Decode extends AbstractElement implements Command {
	
    private final String m_source;
    private final String m_type;
    private final String m_target;
    
    public Decode(String source, String target, String type) {
    	super();
    	
    	m_source = source;
    	m_target = target;
    	m_type = type;
    }
    
    public String getSource() {
    	return m_source;
    }
    
    public String getTarget() {
    	return m_target;
    }
    
    public String getType() {
    	return m_type;
    }
	
    public enum Type {
        JSON,
        XML
    }
}
