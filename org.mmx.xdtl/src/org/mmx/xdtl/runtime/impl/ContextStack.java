package org.mmx.xdtl.runtime.impl;

import java.util.ArrayList;

import org.mmx.xdtl.runtime.Context;
import org.slf4j.MDC;

public class ContextStack {
    private final ArrayList<Context> m_contextStack = new ArrayList<Context>();

    public ContextStack(Context globalContext) {
        push(globalContext);
    }

    public Context getGlobalContext() {
        return m_contextStack.get(0);
    }

    public void push(Context context) {
        m_contextStack.add(context);
        updateMDC();
    }

    public Context pop() {
        Context result = m_contextStack.remove(m_contextStack.size() - 1);
        updateMDC();
        return result;
    }

    public Context getTop() {
        if (m_contextStack.size() == 0) return null;
        return m_contextStack.get(m_contextStack.size() - 1);
    }    

    public PackageContext getTopPackageContext() {
        for (int i = m_contextStack.size() - 1; i > 0; i--) {
            Context context = m_contextStack.get(i);
            if (context instanceof PackageContext) {
                return (PackageContext) context;
            }
        }

        return null;
    }
    
    public TaskContext getTopTaskContext() {
        for (int i = m_contextStack.size() - 1; i > 0; i--) {
            Context context = m_contextStack.get(i);
            if (context instanceof TaskContext) {
                return (TaskContext) context;
            }
        }

        return null;
    }

    public int size() {
        return m_contextStack.size();
    }
    
    private void updateMDC() {
        String taskName = "";
        String packageName = "";
        
        Context top = getTop();
        
        if (top instanceof TaskContext) {
            taskName = ((TaskContext) top).getTask().getName();
        }
        
        PackageContext pkgCtx = getTopPackageContext();
        if (pkgCtx != null) {
            packageName = pkgCtx.getPackage().getName();
        }
        
        MDC.put("xdtlTask", taskName);
        MDC.put("xdtlPackage", packageName);
        MDC.put("xdtlStep", "");
    }
}
