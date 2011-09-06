package org.mmx.xdtl.model;

public interface Element {

    public abstract String getId();
    public abstract SourceLocator getSourceLocator();

    public abstract void setId(String id);
    public abstract void setSourceLocator(SourceLocator sourceLocator);
}