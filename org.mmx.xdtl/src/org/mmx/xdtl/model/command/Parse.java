package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public class Parse extends AbstractElement implements Command {
	
    private final String m_source;
    private final String m_rowset;
    private final String m_target;
    private final String m_grammar;
    private final String m_type;
    private final String m_template;
    
    public Parse(String source, String rowset, String target, String grammar, String type, String template) {
    	super();
    	
    	m_source = source;
    	m_rowset = rowset;
    	m_target = target;
    	m_grammar = grammar;
    	m_type = type;
    	m_template = template;
    }
    
    public String getSource() {
    	return m_source;
    }
    
    public String getRowset() {
    	return m_rowset;
    }
    
    public String getTarget() {
    	return m_target;
    }
    
    public String getGrammar() {
    	return m_grammar;
    }
 
    public String getTemplate() {
    	return m_template;
    }
    
    public String getType() {
    	return m_type;
    }
	
    public enum Type {
    	TXT,
        XML,
        JSON,
        MAP,
        ROWSET
     }
}
