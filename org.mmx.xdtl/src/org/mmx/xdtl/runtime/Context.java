package org.mmx.xdtl.runtime;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.Bindings;

import org.mmx.xdtl.model.Package;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.model.XdtlException;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class Context {
    public static final String VARNAME_XDTL_VERSION = "xdtlVersion";
    public static final String VARNAME_XDTL_ERROR = "xdtlError";
    public static final String VARNAME_XDTL_ERRORCODE = "xdtlErrorCode";
    public static final String VARNAME_XDTL_ERRORDESC = "xdtlErrorDesc";
    public static final String VARNAME_XDTL_ERRORTYPE = "xdtlErrorType";
    public static final String VARNAME_XDTL_ERRORLOCATION = "xdtlErrorLocation";
    public static final String VARNAME_XDTL_ERRORCAUSE = "xdtlErrorCause";
    public static final String VARNAME_XDTL_ROWCOUNT = "xdtlRowcount";
    public static final String VARNAME_XDTL_ROWID = "xdtlRowid";
    public static final String VARNAME_XDTL_RESUME = "xdtlResume";
    public static final String VARNAME_XDTL_PACKAGE_DIR = "xdtlPackageDir";

    // Exit code for last system call
    public static final String VARNAME_XDTL_EXITCODE = "xdtlExitCode";

    // The "virtual" variable, where scriptengine's global object is stored
    public static final String VARNAME_SCRIPTING_GLOBAL = "nashorn.global";

    private final EngineControl m_engineControl;
    private final ConnectionManager m_connectionManager;
    private final Context m_upperContext;
    private ScriptObjectMirror m_scriptingGlobal;
    private Bindings m_bindings;

    public Context(Object scriptingGlobal) {
        m_engineControl = null;
        m_connectionManager = null;
        m_upperContext = null;
        m_scriptingGlobal = (ScriptObjectMirror) scriptingGlobal;
    }

    public Context(EngineControl engineControl,
            ConnectionManager connectionManager, Object scriptingGlobal) {
        m_engineControl = engineControl;
        m_connectionManager = connectionManager;
        m_upperContext = null;
        m_scriptingGlobal = (ScriptObjectMirror) scriptingGlobal;
    }

    public Context(Context upperContext, ConnectionManager connectionManager, Object scriptingGlobal) {
        m_engineControl = upperContext.m_engineControl;
        m_scriptingGlobal = (ScriptObjectMirror) scriptingGlobal;
        m_connectionManager = connectionManager;
        m_upperContext = upperContext;
        bindProperties(upperContext.m_scriptingGlobal, m_scriptingGlobal);
    }

    private void bindProperties(ScriptObjectMirror srcGlobal,
            ScriptObjectMirror dstGlobal) {
        ScriptObjectMirror toObject = (ScriptObjectMirror) dstGlobal.get("Object");
        toObject.callMember("bindProperties", dstGlobal, srcGlobal);
    }

    /**
     * @see org.mmx.xdtl.runtime.Context#getVariable(java.lang.String)
     */
    public Variable getVariable(String name) {
        if (m_scriptingGlobal.containsKey(name)) {
            ScriptObjectMirror propertyDesc = (ScriptObjectMirror) m_scriptingGlobal.getOwnPropertyDescriptor(name);
            Boolean writable = (Boolean) propertyDesc.get("writable");
            return new Variable(name, m_scriptingGlobal.get(name), !writable);
        }

        return null;
    }

    public boolean hasVariable(String name) {
        return m_scriptingGlobal.containsKey(name);
    }

    /**
     * @see org.mmx.xdtl.runtime.Context#assignVariable(java.lang.String, java.lang.Object)
     */
    public Object assignVariable(String name, Object value) {
        try {
            return m_scriptingGlobal.put(name, value);
        } catch (NashornException e) {
            String message = e.getMessage();
            if (message != null
                    && message.startsWith("TypeError:")
                    && message.endsWith("is not a writable property of [object global]")) {
                throw new XdtlException("Variable '" + name + "' is read-only");
            }

            throw e;
        }
    }

    /**
     * @see org.mmx.xdtl.runtime.Context#defineVariable(org.mmx.xdtl.model.Variable)
     */
    public void defineVariable(Variable var) {
        if (m_scriptingGlobal.containsKey(var.getName())) {
            throw new XdtlException("Variable '" + var.getName() + "' is already defined",
                    var.getSourceLocator());
        }

        createPropertyInScriptingGlobal(var);
    }

    private void createPropertyInScriptingGlobal(Variable var) {
        m_scriptingGlobal.put(var.getName(), var.getValue());

        if (var.isReadOnly()) {
            ScriptObjectMirror obj = (ScriptObjectMirror) m_scriptingGlobal.get("Object");
            ScriptObjectMirror propertyDesc = (ScriptObjectMirror) m_scriptingGlobal.getOwnPropertyDescriptor(var.getName());
            propertyDesc.put("writable", false);
            obj.callMember("defineProperty", m_scriptingGlobal, var.getName(), propertyDesc);
        }
    }

    /**
     * @see org.mmx.xdtl.runtime.Context#undefineVariable(java.lang.String)
     */
    public Object undefineVariable(String varname) {
        return m_scriptingGlobal.remove(varname);
    }

    /**
     * @see org.mmx.xdtl.runtime.Context#addVariable(org.mmx.xdtl.model.Variable)
     */
    public void addVariable(Variable var) {
        String varName = var.getName();

        if (hasVariable(varName)) {
            assignVariable(varName, var.getValue());
        } else {
            createPropertyInScriptingGlobal(var);
        }
    }

    /**
     * @see org.mmx.xdtl.runtime.Context#getVariableValue(java.lang.String)
     */
    public Object getVariableValue(String varname) {
        if (!m_scriptingGlobal.containsKey(varname)) {
            throw new XdtlException("Variable '" + varname + "' is not defined");
        }

        return m_scriptingGlobal.get(varname);
    }

    public Package getPackage() {
        return m_upperContext != null ? m_upperContext.getPackage() : null;
    }

    /**
     * @see org.mmx.xdtl.runtime.Context#getEngineControl()
     */
    public EngineControl getEngineControl() {
        return m_engineControl;
    }

    public ConnectionManager getConnectionManager() {
        return m_connectionManager;
    }

    public String getTraceLine() {
        return "";
    }

    public void dump(PrintStream stream) {
        String traceLine = getTraceLine();
        stream.println("\nContext: " + getTraceLine());
        stream.println(separator(9 + traceLine.length()));

        ArrayList<Entry<String, Object>> list = new ArrayList<>(m_scriptingGlobal.size());

        for (Entry<String, Object> entry: m_scriptingGlobal.entrySet()) {
            list.add(entry);
        }

        list.sort(new Comparator<Entry<String, Object>>() {
            @Override
            public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        for (Entry<String, Object> entry: list) {
            dump(stream, entry);
        }
    }

    private char[] separator(int count) {
        char[] result = new char[count];
        Arrays.fill(result, '-');
        return result;
    }

    private void dump(PrintStream stream, Entry<String, Object> entry) {
        Object value = entry.getValue();
        String valueStr;

        if (value instanceof ScriptObjectMirror) {
            ScriptObjectMirror valueMirror = (ScriptObjectMirror) value;
            if (valueMirror.isFunction()) {
                valueStr = "function ...";
            } else {
                valueStr = value.toString();
            }
        } else {
            valueStr = String.valueOf(value);
        }

        ScriptObjectMirror propertyDesc = (ScriptObjectMirror) m_scriptingGlobal.getOwnPropertyDescriptor(entry.getKey());
        boolean writable = (Boolean) propertyDesc.get("writable");
        stream.printf("%s%s: %s\n", entry.getKey(), writable ? "" : " (R/O)", valueStr);
    }

    public Bindings getBindings() {
        if (m_bindings == null) {
            m_bindings = new BindingsImpl();
        }

        return m_bindings;
    }

    private class BindingsImpl implements Bindings {
        private Map<String, Object> m_map;

        public BindingsImpl() {
            m_map = new HashMap<String, Object>(1);
            m_map.put(VARNAME_SCRIPTING_GLOBAL, m_scriptingGlobal);
            m_map = Collections.unmodifiableMap(m_map);
        }

        @Override
        public int size() {
            return m_map.size();
        }

        @Override
        public boolean isEmpty() {
            return m_map.isEmpty();
        }

        @Override
        public boolean containsValue(Object value) {
            return m_map.containsValue(value);
        }

        @Override
        public void clear() {
            m_map.clear();
        }

        @Override
        public Set<String> keySet() {
            return m_map.keySet();
        }

        @Override
        public Collection<Object> values() {
            return m_map.values();
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return m_map.entrySet();
        }

        @Override
        public Object put(String name, Object value) {
            return m_map.put(name, value);
        }

        @Override
        public void putAll(Map<? extends String, ? extends Object> toMerge) {
            m_map.putAll(toMerge);
        }

        @Override
        public boolean containsKey(Object key) {
            return m_map.containsKey(key);
        }

        @Override
        public Object get(Object key) {
            return m_map.get(key);
        }

        @Override
        public Object remove(Object key) {
            return m_map.remove(key);
        }
    }
}
