package org.mmx.xdtl.model.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Column;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Condition;
import org.mmx.xdtl.model.Source;
import org.mmx.xdtl.model.Target;

public class Mappings extends AbstractElement implements Command {
    private final ArrayList<Source> m_sourceList = new ArrayList<Source>();
    private final ArrayList<Target> m_targetList = new ArrayList<Target>();
    private final ArrayList<Column> m_columnList = new ArrayList<Column>();
    private final ArrayList<Condition> m_conditionList = new ArrayList<Condition>();
    private final String m_targetVarName;

    public Mappings(String targetVarName) {
        m_targetVarName = targetVarName;
    }
    
    public void addSource(Source source) {
        m_sourceList.add(source);
    }

    public void addTarget(Target target) {
        m_targetList.add(target);
    }
    
    public void addColumn(Column column) {
        m_columnList.add(column);
    }
    
    public void addCondition(Condition condition) {
        m_conditionList.add(condition);
    }
        
    public String getTargetVarName() {
        return m_targetVarName;
    }

    public List<Source> getSources() {
        return Collections.unmodifiableList(m_sourceList);
    }

    public List<Target> getTargets() {
        return Collections.unmodifiableList(m_targetList);
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(m_columnList);
    }

    public List<Condition> getConditions() {
        return Collections.unmodifiableList(m_conditionList);
    }
}
