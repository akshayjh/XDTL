package org.mmx.xdtl.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.impl.CommandMapping;
import org.mmx.xdtl.runtime.impl.CommandMappingSet;

public class CommandOverrides {
    private static final String DEFAULT_PROPERTY_NAME_SUFFIX = ".impl";

    private final Properties m_properties;
    private final CommandMappingSet m_mappings;
    private final HashMap<String, CommandMapping> m_map = new HashMap<String, CommandMapping>();
    private final String m_propertyNameSuffix;
    
    public CommandOverrides(Properties properties, CommandMappingSet mappings) {
        
        m_properties = properties;
        m_mappings = mappings;
        m_propertyNameSuffix = DEFAULT_PROPERTY_NAME_SUFFIX;
        init();
    }
    
    public CommandOverrides(Properties properties, CommandMappingSet mappings,
            String propertyNameSuffix) {
        
        m_properties = properties;
        m_mappings = mappings;
        m_propertyNameSuffix = propertyNameSuffix;
        init();
    }

    private void init() {
        for (CommandMapping mapping: m_mappings.getMappings()) {
            m_map.put(mapping.getModelClass().getSimpleName().toLowerCase(),
                    mapping);
        }
    }
    
    public void apply() throws ClassNotFoundException {
        int suffixLen = m_propertyNameSuffix.length();
        
        for (Map.Entry<Object, Object> entry: m_properties.entrySet()) {
            String name = (String) entry.getKey();
            
            if (name.endsWith(m_propertyNameSuffix)) {
                String mappingName = name.substring(0, name.length() - suffixLen);
                CommandMapping mapping = m_map.get(mappingName);
                
                if (mapping != null) {
                    String cmdClassName = entry.getValue().toString();
                    Class<?> cmdClass = Class.forName(cmdClassName);
                    
                    CommandMapping newMapping = new CommandMapping(
                            mapping.getModelClass(),
                            mapping.getBuilderClass(),
                            cmdClass.asSubclass(RuntimeCommand.class));
                    
                    m_mappings.putMapping(newMapping);
                }
            }
        }
    }
}
