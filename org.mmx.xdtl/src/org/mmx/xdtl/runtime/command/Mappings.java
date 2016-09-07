package org.mmx.xdtl.runtime.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mappings {
    private final ArrayList<Source> m_sources = new ArrayList<Source>();
    private final ArrayList<Target> m_targets = new ArrayList<Target>();
    private final ArrayList<Column> m_columns = new ArrayList<Column>();
    private final ArrayList<Condition> m_conditions = new ArrayList<Condition>();

    public Mappings() {
        super();
    }

    public void addSource(Source source) {
        m_sources.add(source);
    }

    public void addTarget(Target target) {
        m_targets.add(target);
    }

    public void addColumn(Column column) {
        m_columns.add(column);
    }

    public void addCondition(Condition condition) {
        m_conditions.add(condition);
    }

    public List<Source> getSources() {
        return Collections.unmodifiableList(m_sources);
    }

    public List<Target> getTargets() {
        return Collections.unmodifiableList(m_targets);
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(m_columns);
    }

    public List<Condition> getConditions() {
        return Collections.unmodifiableList(m_conditions);
    }
}
