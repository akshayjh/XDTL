package org.mmx.xdtl.model;

public interface Element {
    String getId();
    String getNoLog();
    SourceLocator getSourceLocator();

    void setId(String id);
    void setNoLog(String noLog);
    void setSourceLocator(SourceLocator sourceLocator);
}
