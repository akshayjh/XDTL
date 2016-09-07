package org.mmx.xdtl.runtime.impl;

import org.mmx.xdtl.model.Package;
import org.mmx.xdtl.model.SourceLocator;
import org.mmx.xdtl.runtime.ConnectionManager;
import org.mmx.xdtl.runtime.Context;

public class PackageContext extends Context {
    private final Package m_package;
    private final String m_onErrorRef;
    private final boolean m_resumeOnErrorEnabled;

    public PackageContext(Context upperContext,
            ConnectionManager connectionManager, Object scriptingGlobal,
            Package pkg, String onErrorRef, boolean resumeOnErrorEnabled) {

        super(upperContext, connectionManager, scriptingGlobal);
        m_package = pkg;
        m_onErrorRef = onErrorRef;
        m_resumeOnErrorEnabled = resumeOnErrorEnabled;
    }

    public Package getPackage() {
        return m_package;
    }

    public String getOnErrorRef() {
        return m_onErrorRef;
    }

    public boolean isResumeOnErrorEnabled() {
        return m_resumeOnErrorEnabled;
    }

    @Override
    public String getTraceLine() {
        SourceLocator loc = m_package.getSourceLocator();
        return "pckg: " + m_package.getName() + "@" + loc.getDocumentUrl()
                + ":" + loc.getLineNumber();
    }
}
