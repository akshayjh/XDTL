package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.TextFileProperties;

public class Fetch extends AbstractElement implements Command {
    private final String m_source;
    private final String m_connection;
    private final String m_overwrite;
    private final TextFileProperties m_textFileProperties;
    private final String m_header;
    private final String m_target;
    private final String m_rowset;
    private final String m_destination;

    public enum Type {
        CSV,
        FIXED,
        XML
    }

    public Fetch(String source, String connection, String overwrite,
            TextFileProperties textFileProperties, String header, String target,
            String rowset, String destination) {
        super();
        m_source = source;
        m_connection = connection;
        m_overwrite = overwrite;
        m_textFileProperties = textFileProperties;
        m_header = header;
        m_target = target;
        m_rowset = rowset;
        m_destination = destination;
    }

    public String getSource() {
        return m_source;
    }

    public String getConnection() {
        return m_connection;
    }

    public String getOverwrite() {
        return m_overwrite;
    }

    public String getTarget() {
        return m_target;
    }

    public String getRowset() {
        return m_rowset;
    }

    public String getDestination() {
        return m_destination;
    }

    public TextFileProperties getTextFileProperties() {
        return m_textFileProperties;
    }

    public String getHeader() {
        return m_header;
    }
}
