package org.mmx.xdtl.model.command;

import java.util.Collections;
import java.util.List;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.Parameter;

public class Query extends AbstractElement implements Command {
    public static final String QUERYTYPE_SQL = "sql";

    private final String m_source;
    private final String m_connection;
    private final String m_queryType;
    private final String m_target;
    private final List<Parameter> m_parameterList;

    public Query(String source, String connection, String queryType, String target, List<Parameter> parameterList) {
        super();
        m_source = source;
        m_connection = connection;
        m_queryType = queryType;
        m_target = target;

        if (parameterList == null) {
            m_parameterList = Collections.emptyList();
        } else {
            m_parameterList = Collections.unmodifiableList(parameterList);
        }
    }

    public String getSource() {
        return m_source;
    }

    public String getConnection() {
        return m_connection;
    }

    public String getQueryType() {
        return m_queryType;
    }

    public String getTarget() {
        return m_target;
    }

    public List<Parameter> getParameterList() {
        return m_parameterList;
    }
}
