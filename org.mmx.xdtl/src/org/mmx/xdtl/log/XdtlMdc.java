package org.mmx.xdtl.log;

import java.util.Hashtable;

import org.apache.log4j.MDC;
import org.mmx.xdtl.model.SourceLocator;

public class XdtlMdc {

    public static MdcState saveState() {
        return new MdcState();
    }

    public static void restoreState(MdcState state) {
        state.restore();
    }

    public static void setState(String pkg, String task, String step, SourceLocator locator) {
        MDC.put("xdtlPackage", pkg == null ? "" : pkg);
        MDC.put("xdtlTask", task == null ? "" : task);
        setState(step, locator);
    }

    public static void setState(SourceLocator locator) {
        setState(locator.getTagName(), locator);
    }

    public static void setState(String step, SourceLocator locator) {
        String documentUrl = locator.getDocumentUrl();
        MDC.put("xdtlStep", step == null ? "" : step);
        MDC.put("xdtlDocument", documentUrl == null ? "" : documentUrl);
        MDC.put("xdtlLine", Integer.toString(locator.getLineNumber()));
        MDC.put("xdtlLocation", locator);
        MDC.put("xdtlNoLog", false);
    }

    public static void setLoggingDisabled(boolean loggingDisabled) {
        MDC.put("xdtlNoLog", loggingDisabled);
    }

    public static boolean isLoggingDisabled() {
        Boolean result = (Boolean) MDC.get("xdtlNoLog");
        return result != null ? result : false;
    }

    public static class MdcState {
        private String m_package;
        private String m_task;
        private String m_step;
        private String m_document;
        private String m_line;
        private SourceLocator m_location;
        private Boolean m_noLog;

        private MdcState() {
            m_package  = (String) MDC.get("xdtlPackage");
            m_task     = (String) MDC.get("xdtlTask");
            m_step     = (String) MDC.get("xdtlStep");
            m_document = (String) MDC.get("xdtlDocument");
            m_line     = (String) MDC.get("xdtlLine");
            m_location = (SourceLocator) MDC.get("xdtlLocation");
            m_noLog    = (Boolean) MDC.get("xdtlNoLog");
        }

        private void restore() {
            Hashtable<?,?> ctx = MDC.getContext();
            restoreKey(ctx, "xdtlPackage", m_package);
            restoreKey(ctx, "xdtlTask", m_task);
            restoreKey(ctx, "xdtlStep", m_step);
            restoreKey(ctx, "xdtlDocument", m_document);
            restoreKey(ctx, "xdtlLine", m_line);
            restoreKey(ctx, "xdtlLocation", m_location);
            restoreKey(ctx, "xdtlNoLog", m_noLog);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static void restoreKey(Hashtable ctx, String key, Object value) {
            if (value == null) {
                ctx.remove(key);
            } else {
                ctx.put(key, value);
            }
        }
    }
}
