package org.mmx.xdtl.model.command;

public class Read extends ReadWrite {
    private final String m_errors;
    private final String m_header;
    private final String m_skip;
    private final String m_batch;
    
    public Read(String source, String target, String connection, String type,
            String overwrite, String delimiter, String quote, String encoding,
            String errors, String header, String skip, String batch) {
        super(source, target, connection, type, overwrite, delimiter, quote, encoding);
        
        m_errors = errors;
        m_header = header;
        m_skip = skip;
        m_batch = batch;
    }

    public String getErrors() {
        return m_errors;
    }

    public String getHeader() {
        return m_header;
    }
    
    public String getSkip() {
        return m_skip;
    }

    public String getBatch() {
        return m_batch;
    }
}
