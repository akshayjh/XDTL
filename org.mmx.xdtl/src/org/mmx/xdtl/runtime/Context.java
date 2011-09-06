package org.mmx.xdtl.runtime;

import java.util.HashMap;

import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.model.XdtlException;

public class Context {
    public static final String VARNAME_XDTL_VERSION       = "xdtlVersion";
    public static final String VARNAME_XDTL_ERROR         = "xdtlError";
    public static final String VARNAME_XDTL_ERRORCODE     = "xdtlErrorCode";
    public static final String VARNAME_XDTL_ERRORDESC     = "xdtlErrorDesc";
    public static final String VARNAME_XDTL_ERRORTYPE     = "xdtlErrorType";
    public static final String VARNAME_XDTL_ERRORLOCATION = "xdtlErrorLocation";
    public static final String VARNAME_XDTL_ROWCOUNT      = "xdtlRowcount";
    public static final String VARNAME_XDTL_ROWID         = "xdtlRowid";
    public static final String VARNAME_XDTL_RESUME        = "xdtlResume";

    private final EngineControl m_engineControl;
    private final HashMap<String, Variable> m_varmap = new HashMap<String, Variable>();
    private final ConnectionManager m_connectionManager;
    
    public Context() {
        m_engineControl = null;
        m_connectionManager = null;
    }
    
    public Context(EngineControl engineControl,
            ConnectionManager connectionManager) {
        m_engineControl = engineControl;
        m_connectionManager = connectionManager;
    }
    
    public Context(Context upperContext, ConnectionManager connectionManager) {
        m_engineControl = upperContext.m_engineControl;
        m_varmap.putAll(upperContext.m_varmap);
        m_connectionManager = connectionManager;
    }
    
    /**
     * @see org.mmx.xdtl.runtime.Context#getVariable(java.lang.String)
     */
    public Variable getVariable(String name) {
        return m_varmap.get(name);
    }
    
    /**
     * @see org.mmx.xdtl.runtime.Context#assignVariable(java.lang.String, java.lang.Object)
     */
    public Variable assignVariable(String name, Object value) {
        Variable var = getVariable(name);
        if (var == null) {
            var = new Variable(name, value);
            addVariable(var);
        } else {
            var.setValue(value);
        }
        
        return var;
    }
    
    /**
     * @see org.mmx.xdtl.runtime.Context#defineVariable(org.mmx.xdtl.model.Variable)
     */
    public void defineVariable(Variable var) {
        if (m_varmap.containsKey(var.getName())) {
            throw new XdtlException("Variable '" + var.getName() + "' is already defined",
                    var.getSourceLocator());
        }
        
        m_varmap.put(var.getName(), var);
    }
    
    /**
     * @see org.mmx.xdtl.runtime.Context#undefineVariable(java.lang.String)
     */
    public Variable undefineVariable(String varname) {
        return m_varmap.remove(varname);
    }
    
    /**
     * @see org.mmx.xdtl.runtime.Context#addVariable(org.mmx.xdtl.model.Variable)
     */
    public void addVariable(Variable var) {
        m_varmap.put(var.getName(), var);
    }
    
    /**
     * @see org.mmx.xdtl.runtime.Context#getVariableValue(java.lang.String)
     */
    public Object getVariableValue(String varname) {
        Variable var = getVariable(varname);
        if (var == null) {
            throw new XdtlException("Variable '" + varname + "' is not defined");
        }
        
        return var.getValue();
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
}
