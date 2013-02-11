package org.mmx.xdtl.runtime.impl;

import java.util.ArrayList;

import org.apache.log4j.MDC;
import org.mmx.xdtl.runtime.Context;

public class ContextStack {
    private final ArrayList<Context> m_stack = new ArrayList<Context>();

    public ContextStack(Context globalContext) {
        push(globalContext);
    }

    public Context getGlobalContext() {
        return m_stack.get(0);
    }

    public void push(Context context) {
        m_stack.add(context);
        updateMDC();
    }

    public Context pop() {
        Context result = m_stack.remove(m_stack.size() - 1);
        updateMDC();
        return result;
    }

    public Context getTop() {
        if (m_stack.size() == 0) return null;
        return m_stack.get(m_stack.size() - 1);
    }    

    public PackageContext getTopPackageContext() {
        for (int i = m_stack.size() - 1; i > 0; i--) {
            Context context = m_stack.get(i);
            if (context instanceof PackageContext) {
                return (PackageContext) context;
            }
        }

        return null;
    }
    
    public TaskContext getTopTaskContext() {
        for (int i = m_stack.size() - 1; i > 0; i--) {
            Context context = m_stack.get(i);
            if (context instanceof TaskContext) {
                return (TaskContext) context;
            }
        }

        return null;
    }

    public int size() {
        return m_stack.size();
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

    public void writeTrace(StringBuilder buf) {
        for (int i = m_stack.size() - 1; i > 0; i--) {
            Context ctx = m_stack.get(i);
            buf.append(i).append(". ").append(ctx.getTraceLine()).append('\n');
        }
    }
}
