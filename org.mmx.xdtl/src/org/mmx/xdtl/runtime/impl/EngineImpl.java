package org.mmx.xdtl.runtime.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import org.apache.log4j.Logger;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.model.Connection;
import org.mmx.xdtl.model.Package;
import org.mmx.xdtl.model.Parameter;
import org.mmx.xdtl.model.SourceLocator;
import org.mmx.xdtl.model.Task;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.ConnectionManager;
import org.mmx.xdtl.runtime.ConnectionManagerEvent;
import org.mmx.xdtl.runtime.ConnectionManagerListener;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.Engine;
import org.mmx.xdtl.runtime.EngineControl;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.TypeConverter;
import org.mmx.xdtl.runtime.impl.XdtlMdc.MdcState;
import org.mmx.xdtl.runtime.util.ContextToBindingsAdapter;
import org.mmx.xdtl.services.PathList;
import org.mmx.xdtl.services.PathList.ForEachCallback;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class EngineImpl implements Engine, EngineControl {
    private static final String TASK_DEFAULT_CONNECTION_ARG_NAME = "taskConn";
    private static final String SYSTEM_STARTUP_SCRIPT = "/startup.js";
    private static final Logger logger = Logger.getLogger("xdtl.rt.engine");
    private static final URL DEFAULT_BASE_URL; 
    
    private final ExpressionEvaluator m_exprEval;
    private final TypeConverter m_typeConv;
    private final CommandInvoker m_commandInvoker;
    private final Provider<ConnectionManager> m_connectionManagerProvider;
    private final PackageLoader m_pkgLoader;
    private final String m_version;
    private final PathList m_startupScripts;
    private final ScriptEngine m_scriptEngine;
    private final ExtensionLoader m_extensionLoader;
    
    private ContextStack m_contextStack;
    private boolean m_errorTaskRunning;
    private Throwable m_firstError;

    static {
        try {
            DEFAULT_BASE_URL = new URL("file:");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Inject
    EngineImpl(PackageLoader pkgLoader, ExpressionEvaluator expressionEvaluator,
            TypeConverter typeConverter,
            CommandInvoker commandInvoker,
            Provider<ConnectionManager> connectionManagerProvider,
            ScriptEngine scriptEngine,
            @Named("startup.path") PathList startupScripts,
            @Named("xdtl.version") String version,
            ExtensionLoader extensionLoader) {

        m_pkgLoader = pkgLoader;
        m_exprEval = expressionEvaluator;
        m_typeConv = typeConverter;
        m_commandInvoker = commandInvoker;
        m_connectionManagerProvider = connectionManagerProvider;
        m_scriptEngine = scriptEngine;
        m_startupScripts = startupScripts;
        m_version = version;
        m_extensionLoader = extensionLoader;
    }

    @Override
    public void run(String urlSpec, Map<String, Object> args,
            Map<String, Object> globals) {
        
        Package pkg = parse(toURL("file:"), urlSpec);
        run(pkg, toURL(urlSpec).getRef(), args, globals);
    }    

    @Override
    public void run(Package pkg, String taskname, Map<String, Object> args,
            Map<String, Object> globals) {
        
        XdtlMdc.setState(pkg.getName(), null, null, pkg.getSourceLocator());
        m_contextStack = new ContextStack(createGlobalContext(globals));
        try {
            runStartupScripts();
            run(pkg, taskname, args);
        } finally {
            if (m_contextStack.size() != 1) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Context stack size=" + m_contextStack.size());
                }
            }
        }
    }
    
    private void run(Package pkg, String taskname, Map<String, Object> args) {
        if (taskname == null || taskname.length() == 0) {
            runPackage(pkg, args);
        } else {
            runTask(pkg, taskname, args);            
        }
    }

    private void runTask(Package pkg, String taskname, Map<String, Object> args) {
        if (taskname == null || taskname.length() == 0) {
            throw new XdtlException("Task name must be specified");
        }
        
        Task task = pkg.getTask(taskname);
        if (task == null) {
            throw new XdtlException("Task '" + taskname
                    + "' was not found in package '" + pkg.getName() + "'");
        }

        MdcState mdcState = XdtlMdc.saveState();
        try {
            XdtlMdc.setState(pkg.getName(), task.getName(), null, task.getSourceLocator());
            PackageContext packageContext = m_contextStack.getTopPackageContext();
            TaskRunResult result = null;
    
            if (packageContext != null && packageContext.getPackage() == pkg) {
                // same package
                result = runTask(task, args);
            } else {
                // different or new package
                packageContext = createPackageContext(pkg, args);
                m_contextStack.push(packageContext);
                try {
                    result = runTask(task, args);
                } finally {
                    m_contextStack.pop();
                }
            }
    
            if (result.getExitRuntime()) {
                logger.info("Package '" + pkg.getName()
                        + "' terminated runtime in task '" + task.getName() + "'");
                System.exit(result.getExitCode());
            }
        } finally {
            XdtlMdc.restoreState(mdcState);
        }
    }

    @Override
    public void call(String taskRef, Map<String, Object> args) {
        Package pkg = parse(getCurrentPackage().getUrl(), taskRef);
        run(pkg, toURL(taskRef).getRef(), args);
    }

    @Override
    public void callExtension(String nsUri, String name,
            Map<String, Object> args) {
        if (logger.isTraceEnabled()) {
            logger.trace("callExtension: nsUri=" + nsUri + " name=" + name);
        }
        
        Package pkg = m_extensionLoader.getExtensionPackage(nsUri, name);
        if (pkg == null) {
            throw new XdtlException("Extension package not found: nsUri="
            		+ nsUri + ", name=" + name);
        }

        Context upperCtx = m_contextStack.getTop();
        if (!(upperCtx instanceof TaskContext)) {
            throw new XdtlException("Extension elements must be executed in task context");
        }
        
        Task task = pkg.getTask(name);
        TaskContext context = createTaskContext(upperCtx, task, args);
        
        // Put all arguments into context irrespective of whether task
        // parameters exist.
        for (String argName: args.keySet()) {
            context.addVariable(new Variable(argName, args.get(argName)));
        }
        
        runTaskInContext(context, null);
    }

    /**
     * Execute a list of commands in current task context.
     */
    @Override   
    public void execute(CommandList commands) {
        Context ctx = m_contextStack.getTop();
        if (!(ctx instanceof TaskContext)) {
            throw new XdtlException("Commands must be run in task context",
                    commands.getSourceLocator());
        }
        
        TaskContext taskContext = (TaskContext) ctx;
        
        for (Command cmd: commands) {
            try {
                m_commandInvoker.invoke(cmd, taskContext);
            } catch (XdtlExitException e) {
                logger.trace("command list execution stopped");
                throw e;
            } catch (Throwable t) {
                logError("", cmd.getSourceLocator().getTagName(),
                        cmd.getSourceLocator(), t);
                
                if (runErrorHandler(taskContext.getOnErrorRef(), t) && taskContext.isOnErrorResumeEnabled()) {
                    // Clear error info before resuming
                    m_firstError = null;
                    removeErrorHandlerVariables();
                    logger.trace("Resuming from error");
                } else {
                    if (!(t instanceof RuntimeException)) {
                        String msg = "Command '" + cmd.getClass().getSimpleName() + "' failed";
                        throw new XdtlException(msg, cmd.getSourceLocator(), t); 
                    }
                    throw (RuntimeException) t; 
                }
            }
        }
    }
    
    @Override
    public void exit() {
        Context ctx = m_contextStack.getTop();
        
        if (ctx == null || !((ctx instanceof TaskContext) || (ctx instanceof PackageContext))) {
            throw new XdtlException("exit must be called from within package or task context");
        }
        
        throw new XdtlExitException();
    }
    
    @Override
    public void exit(int code) {
        Context ctx = m_contextStack.getTop();
        
        if (ctx == null || !((ctx instanceof TaskContext) || (ctx instanceof PackageContext))) {
            throw new XdtlException("exit must be called from within package or task context");
        }
        
        throw new XdtlExitException(code, true);
    }
    
    private void runPackage(Package pkg, Map<String, Object> args) {
        MdcState mdcState = XdtlMdc.saveState();
        XdtlMdc.setState(pkg.getName(), "", "", pkg.getSourceLocator());
        try {
            logger.info("Running package '" + pkg.getName() + "'");
            
            PackageContext packageContext = createPackageContext(pkg, args);
            m_contextStack.push(packageContext);
    
            try {
                for (Task task : pkg.getTasks()) {
                	TaskRunResult result = runPackageTask(pkg, packageContext, task);
                	
                    if (result.getExit()) {
                        if (result.getExitRuntime()) {
                            logger.info("Package '" + pkg.getName()
                                    + "' terminated runtime in task '"
                                    + task.getName() + "'");
                            System.exit(result.getExitCode());
                        }
    
                        logger.trace("Package '" + pkg.getName()
                                + "' was terminated by 'exit'");
                        return;
                    }
                }
            } finally {
                m_contextStack.pop();
            }
        } finally {
            XdtlMdc.restoreState(mdcState);
        }
    }

    /**
     * Runs package's task.
     * 
     * @param pkg the package
     * @param packageContext the package context
     * @param task the task
     */
    private TaskRunResult runPackageTask(Package pkg, PackageContext packageContext,
            Task task) {
        MdcState mdcState = XdtlMdc.saveState();
        XdtlMdc.setState(pkg.getName(), task.getName(), "", task.getSourceLocator());
        try {
            return runTask(task, null);
        } catch (Throwable t) {
            logError("", "", pkg.getSourceLocator(), t);
            if (runErrorHandler(packageContext.getOnErrorRef(), t) 
            		&& packageContext.isResumeOnErrorEnabled()) {

            	// Clear error info before resuming
                m_firstError = null;
                removeErrorHandlerVariables();
                logger.trace("Resuming from error");
                return TaskRunResult.success();
            } else {
                if (!(t instanceof RuntimeException)) {
                    String msg = "Package '" + pkg.getName() + "' failed";
                    throw new XdtlException(msg, pkg.getSourceLocator(), t);
                }
                
                throw (RuntimeException) t;
            }
        } finally {
            XdtlMdc.restoreState(mdcState);
        }
    }

    /**
     * Runs the task.
     * 
     * @param task
     *            the task
     * @param args
     *            the map of task arguments
     * @return true, if task was terminated normally, false, if task was
     *         terminated by 'exit'
     */
    private TaskRunResult runTask(Task task, Map<String, Object> args) {
        logger.info("Running task '" + task.getName() + "'");

        PackageContext pkgContext = m_contextStack.getTopPackageContext();
        TaskContext taskContext = createTaskContext(pkgContext, task, args);
        return runTaskInContext(taskContext, args);
    }

    private TaskRunResult runTaskInContext(TaskContext taskContext, Map<String, Object> args) {
        Task task = taskContext.getTask();
        m_contextStack.push(taskContext);
        
        try {
            PackageContext pkgContext = m_contextStack.getTopPackageContext();
            initTaskDefaultConnection(pkgContext, taskContext, args);
            execute(task.getCommandList());
            return TaskRunResult.success();
        } catch (XdtlExitException e) {
            logger.trace("Task '" + task.getName() + "' was terminated by command 'exit'");
            return new TaskRunResult(e.getCode(), e.getGlobal());
        } finally {
            m_contextStack.pop();
            try {
                taskContext.getConnectionManager().releaseAllJdbcConnections();
            } catch (Throwable t) {
                logger.warn("Failed to close JDBC connections", t);
            }
        }
    }
    
    private boolean runErrorHandler(String ref, Throwable error) {
        if (m_errorTaskRunning) {
            return false;
        }
        
        if (m_firstError == null) {
            m_firstError = error;
        }

        if (ref == null) {
            return false;
        }
        
        logger.trace("Starting error handler: " + ref);
        
        initErrorHandlerVariables(m_firstError);
        Package pkg = parse(getCurrentPackage().getUrl(), ref);
        String taskName = toURL(ref).getRef();
        String taskDisplayName = getTaskDisplayName(pkg, taskName);
        
        try {
            m_errorTaskRunning = true;
            run(pkg, taskName, null);
        } catch (Throwable t) {
            logError("Error handler '" + taskDisplayName + "' failed", "",
                    pkg.getSourceLocator(), t);
        } finally {
            m_errorTaskRunning = false;
        }

        logger.trace("Error handler finished: " + taskDisplayName);        
        return true;
    }

    private void runOnOpenHandler(String taskRef, Connection cnn) {
        logger.trace("Starting onOpen handler: " + taskRef);
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put(TASK_DEFAULT_CONNECTION_ARG_NAME, cnn);
        call(taskRef, args);
    }

    private void initErrorHandlerVariables(Throwable t) {
        ErrorProperties errorProps = new ErrorProperties(t);
        initErrorHandlerVariables(m_contextStack.getGlobalContext(), errorProps);
        initErrorHandlerVariables(m_contextStack.getTopPackageContext(), errorProps);
    }
    
    private void initErrorHandlerVariables(Context context, ErrorProperties errorProps) {
        context.addVariable(new Variable(Context.VARNAME_XDTL_ERROR,
                errorProps.getError(), true));

        context.addVariable(new Variable(Context.VARNAME_XDTL_ERRORCODE,
                errorProps.getErrorCode(), true));
        
        context.addVariable(new Variable(Context.VARNAME_XDTL_ERRORTYPE,
                errorProps.getErrorType(), true));

        context.addVariable(new Variable(Context.VARNAME_XDTL_ERRORDESC,
                errorProps.getErrorDesc(), true));

        context.addVariable(new Variable(Context.VARNAME_XDTL_ERRORLOCATION,
                errorProps.getSourceLocator().toString(), true));

        context.addVariable(new Variable(Context.VARNAME_XDTL_ERRORCAUSE,
                errorProps.getErrorCause(), true));
    }

    private void removeErrorHandlerVariables() {        
        removeErrorHandlerVariables(m_contextStack.getGlobalContext());
        removeErrorHandlerVariables(m_contextStack.getTopPackageContext());
    }
    
    private void removeErrorHandlerVariables(Context context) {
        context.undefineVariable(Context.VARNAME_XDTL_ERROR);
        context.undefineVariable(Context.VARNAME_XDTL_ERRORCODE);
        context.undefineVariable(Context.VARNAME_XDTL_ERRORTYPE);
        context.undefineVariable(Context.VARNAME_XDTL_ERRORDESC);
        context.undefineVariable(Context.VARNAME_XDTL_ERRORLOCATION);
        context.undefineVariable(Context.VARNAME_XDTL_ERRORCAUSE);
    }
    
    private PackageContext createPackageContext(Package pkg,
            Map<String, Object> args) {

        Context globalContext = m_contextStack.getGlobalContext();
        String onErrorUrlSpec = evaluateOnError(globalContext, pkg.getOnError());
        
        Boolean onErrorResume = m_typeConv.toBoolean(m_exprEval.evaluate(
                globalContext, pkg.getResume()));
        
        if (onErrorResume == null) {
            onErrorResume = Boolean.FALSE;
        }

        PackageContext context = new PackageContext(
                (Context) globalContext, null, pkg, onErrorUrlSpec, onErrorResume);

        addParametersToContext(context, pkg.getParameterList(), args);
        addVariablesToContext(context, pkg.getVariableList());
        addConnectionsToContext(context, pkg.getConnectionList());

        return context;
    }

    private TaskContext createTaskContext(Context upperContext,
            Task task, Map<String, Object> args) {

        String onErrorUrl = evaluateOnError(upperContext, task.getOnError());
        
        Boolean onErrorResume = m_typeConv.toBoolean(m_exprEval.evaluate(
                upperContext, task.getResume()));
        
        if (onErrorResume == null) {
            onErrorResume = Boolean.FALSE;
        }
     
        ConnectionManager cnnMgr = m_connectionManagerProvider.get();
        initConnectionManager(cnnMgr, task, args);
        
        TaskContext newCtx = new TaskContext(upperContext, cnnMgr,
                m_typeConv, task, onErrorUrl, onErrorResume);
        
        addParametersToContext(newCtx, task.getParameterList(), args);
        return newCtx;
    }

    private void initConnectionManager(ConnectionManager cnnMgr,
            Task task, Map<String, Object> args) {
        
        cnnMgr.addListener(new ConnectionManagerListener() {
            @Override
            public void connectionOpened(ConnectionManagerEvent event) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Connection '" + event.getConnectionElement().getName() + "' opened");
                }
                
                String onOpen = event.getConnectionElement().getOnOpen();
                if (onOpen != null && onOpen.length() != 0) {
                    runOnOpenHandler(onOpen, event.getConnectionElement());
                }
            }
        });
        
        Context topCtx = m_contextStack.getTop();
        if (!m_errorTaskRunning && topCtx instanceof TaskContext) {
            // copy connections from caller (task context at top is in the same package)
            TaskContext taskCtx = (TaskContext) topCtx;
            cnnMgr.addJdbcConnections(taskCtx.getConnectionManager().getJdbcConnections());
        } else {
            // caller is in different package, copy connections
            // corresponding to connection arguments
            copyConnectionArgs(cnnMgr, args);
        }
    }

    private void copyConnectionArgs(ConnectionManager cnnMgr,
            Map<String, Object> args) {
        
        if (args == null) {
            return;
        }
        
        TaskContext taskCtx = m_contextStack.getTopTaskContext();
        if (taskCtx != null) {
            ConnectionManager callerCnnMgr = taskCtx.getConnectionManager(); 
                
            for (Object o: args.values()) {
                if (o instanceof Connection) {
                    Connection cnn = (Connection) o;
                    try {
                        cnnMgr.addJdbcConnection(callerCnnMgr.getJdbcConnection(cnn));
                    } catch (SQLException e) {
                        throw new XdtlException("Could not open connection '" + cnn.getName() + "'", e);
                    }
                }
            }
        }
    }

    private void initTaskDefaultConnection(Context evalContext,
            TaskContext taskContext, Map<String, Object> args) {
        
        Task task = taskContext.getTask();
        ConnectionManager cnnMgr = taskContext.getConnectionManager();

        Connection cnn = null;
        
        if (args != null) {
            cnn = (Connection) args.get(TASK_DEFAULT_CONNECTION_ARG_NAME);
        }
        
        if (cnn == null) {
            if (task.getConnection().length() == 0) {
                return;
            }
            
            Object obj = m_exprEval.evaluate(evalContext, task.getConnection());
            if (!(obj instanceof Connection)) {
                throw new XdtlException("Invalid connection type: '" +
                        obj.getClass().getName() + "'", task.getSourceLocator());
            }
            
            cnn = (Connection) obj;
        }
        
        try {
            cnnMgr.setDefaultJdbcConnection(cnnMgr.getJdbcConnection(cnn));
        } catch (SQLException e) {
            throw new XdtlException("Failed to create JDBC connection", task.getSourceLocator(), e);
        }
    }

    private void addConnectionsToContext(Context context,
            List<Connection> connectionList) {

        for (Connection cnn : connectionList) {
            String value = m_typeConv.toString(m_exprEval.evaluate(context,
                    cnn.getValue()));
            
            String onOpen = m_typeConv.toString(m_exprEval.evaluate(context,
                    cnn.getOnOpen()));
            
            Connection newCnn = new Connection(cnn.getName(), cnn.getType(),
                    cnn.isExists(), value, onOpen);
            
            newCnn.setSourceLocator(cnn.getSourceLocator());
            
            Variable newVar = new Variable(cnn.getName(), newCnn);
            newVar.setSourceLocator(cnn.getSourceLocator());
            
            context.defineVariable(newVar);
        }
    }
    
    private void addVariablesToContext(Context context,
            List<Variable> variableList) {

        for (Variable var : variableList) {
            Object value = m_exprEval.evaluate(context,
                    (String) var.getValue());
            Variable newVar = new Variable(var.getName(), value);
            newVar.setSourceLocator(var.getSourceLocator());
            context.defineVariable(newVar);
        }
    }

    private void addParametersToContext(Context context,
            List<Parameter> parameterList, Map<String, Object> args) {

        for (Parameter param : parameterList) {
            Object value = args != null ? args.get(param.getName()) : null;
            if (value == null) {
                if (param.isRequired()) {
                    throw new XdtlException("Parameter '" + param.getName()
                            + "' is required");
                }

                value = m_exprEval.evaluate(context, param
                        .getDefault());
            }

            Variable newVar = new Variable(param.getName(), value, true);
            newVar.setSourceLocator(param.getSourceLocator());
            context.defineVariable(newVar);
        }
    }

    private Context createGlobalContext(Map<String, Object> globals) {
        Context context = new Context(this, null);
        context.addVariable(new Variable(Context.VARNAME_XDTL_VERSION, m_version, true));
        context.addVariable(new Variable(Context.VARNAME_XDTL_EXITCODE, 0, false));
        
        if (globals != null) {
            for (Map.Entry<String, Object> entry: globals.entrySet()) {
                context.defineVariable(new Variable(entry.getKey(),
                        entry.getValue()));
            }
        }
        return context;
    }
    
    private void runStartupScripts() {
        runSystemStartupScript();
        runUserStartupScripts();
    }

    private void runUserStartupScripts() {
        if (m_startupScripts == null) {
            return;
        }

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".js");
            }
        };
        
        m_startupScripts.forEachFile(filter, new ForEachCallback() {
            @Override
            public Object execute(File file) {
                try {
                    if (logger.isTraceEnabled()) {
                        logger.trace("runUserStartupScripts: running script '" + file + "'");
                    }
                    runScript(new FileInputStream(file));
                } catch (Exception e) {
                    throw new XdtlException("Error while executing user startup script: '" + file + "'", e);
                }
                return null;
            }
        });
    }

    private void runSystemStartupScript() {
        InputStream is = getClass().getResourceAsStream(SYSTEM_STARTUP_SCRIPT);
        if (is == null) {
            logger.trace("runSystemStartupScript: script not found, skipping");
            return;
        }

        try {
            if (logger.isTraceEnabled()) {
                logger.trace("runSystemStartupScript: running script '" + SYSTEM_STARTUP_SCRIPT + "'");
            }
            
            runScript(is);
        } catch (Exception e) {
            throw new XdtlException("Error while executing system startup script: '" + SYSTEM_STARTUP_SCRIPT + "'", e);
        }
    }

    private void runScript(InputStream is) throws Exception {
        try {
            m_scriptEngine.eval(new BufferedReader(new InputStreamReader(is)),
                    new ContextToBindingsAdapter(m_contextStack.getGlobalContext()));
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.warn("runScript: Failed to close input stream", e);
            }
        }
    }

    private void logError(String msg, String step, SourceLocator sourceLocator, Throwable t) {
        if (t instanceof XdtlException) {
            XdtlException e = (XdtlException) t;
            if (e.isLogged()) return;
            e.setLogged(true);
            if (e.getSourceLocator() != null) {
                sourceLocator = e.getSourceLocator();
            }
        }

        MdcState state = XdtlMdc.saveState();
        XdtlMdc.setState(step, sourceLocator);

        String errorMsg = createErrorMessage(msg, t);
        if (logger.isTraceEnabled()) {
            logger.error(errorMsg, t);
        } else {
            logger.error(errorMsg);
        }

        XdtlMdc.restoreState(state);
    }

    private String createErrorMessage(String msg, Throwable t) {
        StringBuilder buf = new StringBuilder();
        if (msg != null && msg.length() > 0) {
            buf.append(msg).append(": ");
        }

        buf.append(t.getMessage());
        buf.append("\nXDTL context stack:\n");
        m_contextStack.writeTrace(buf);
        return buf.toString();
    }

    private Package getCurrentPackage() {
        PackageContext packageContext = m_contextStack.getTopPackageContext();
        if (packageContext != null) {
            return packageContext.getPackage();
        }
        
        return null;
    }
    
    private String evaluateOnError(Context context, String onError) {
        onError = (String) m_exprEval.evaluate(context, onError);
        
        if (onError != null && onError.length() == 0) {
            onError = null;
        }
        
        return onError;
    }
    
    private String getTaskDisplayName(Package pkg, String taskName) {
        if (taskName == null || taskName.length() == 0) {
            return pkg.getName();
        }
        
        return pkg.getName() + "#" + taskName;
    }

    private Package parse(URL baseUrl, String url) {
        try {
            return m_pkgLoader.loadPackage(baseUrl, removeRef(url));
        } catch (Exception e) {
            if (!(e instanceof XdtlException)) {
                throw new XdtlException(e);
            }
            
            throw (XdtlException) e;
        }
    }

    /**
     * Removes ref part from URL if present.
     * 
     * @param url The URL.
     * @return An URL without ref part.
     * @throws MalformedURLException 
     */
    private static String removeRef(String urlSpec) {
        URL url = toURL(urlSpec);
        String ref = url.getRef();
        
        if (ref == null) {
            return urlSpec; 
        }
        
        return urlSpec.substring(0, urlSpec.length() - ref.length() - 1);
    }

    private static URL toURL(String urlSpec) {
        try {
            return new URL(DEFAULT_BASE_URL, urlSpec);
        } catch (MalformedURLException e) {
            throw new XdtlException(e);
        }
    }
}
