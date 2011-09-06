package org.mmx.xdtl.runtime.impl;

import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.SourceLocator;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.XdtlError;
import org.mmx.xdtl.services.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.inject.Inject;

public class CommandInvokerImpl implements CommandInvoker {
    private final Logger m_logger = LoggerFactory.getLogger(CommandInvokerImpl.class);
    private final CommandMappingSet m_mappings;
    private final Injector m_injector;
    
    @Inject
    CommandInvokerImpl(CommandMappingSet mappings, Injector injector) {
        m_mappings = mappings;
        m_injector = injector;
    }
    
    /* (non-Javadoc)
     * @see org.mmx.xdtl.runtime.CommandInvoker#invoke(org.mmx.xdtl.model.Command, org.mmx.xdtl.runtime.Context)
     */
    public void invoke(Command cmd, Context context) {
        String oldStepName = (String) MDC.get("xdtlStep");
        String oldDocument = (String) MDC.get("xdtlDocument");
        String oldLineNo   = (String) MDC.get("xdtlLine");
        
        SourceLocator locator = cmd.getSourceLocator();
        
        String stepName = locator.getTagName();
        String documentUrl = locator.getDocumentUrl();

        MDC.put("xdtlStep", stepName == null ? "" : stepName);
        MDC.put("xdtlDocument", documentUrl == null ? "" : documentUrl);
        MDC.put("xdtlLine", Integer.toString(locator.getLineNumber()));
        
        try {
            CommandMapping mapping = m_mappings.findByModelClass(cmd.getClass());
            if (mapping == null) {
                throw new XdtlException("Command mapping for class '"
                        + cmd.getClass().getName() + "' does not exist.",
                        cmd.getSourceLocator());
            }
            
            try {
                CommandBuilder builder = m_injector.getInstance(CommandBuilder.class,
                        cmd.getClass().getName());
                
                RuntimeCommand runtimeCmd = builder.build(context,
                        mapping.getRuntimeClass(), cmd);
                
                m_injector.injectMembers(runtimeCmd);
    
                m_logger.debug("{} running command '{}'", cmd.getSourceLocator(),
                        runtimeCmd.getClass().getName());
                
                runtimeCmd.run(context);
            } catch (XdtlExitException e) {
                throw e;
            } catch (XdtlError e) {
                throw new XdtlException(e.getMessage(), cmd.getSourceLocator(), e);
            } catch (Throwable t) {
                throw new XdtlException("Command '" + cmd.getClass().getName()
                        + "' failed", cmd.getSourceLocator(), t);
            }
        } finally {
            MDC.put("xdtlStep", oldStepName != null ? oldStepName : "");
            MDC.put("xdtlDocument", oldDocument != null ? oldDocument : "");
            MDC.put("xdtlLine", oldLineNo != null ? oldLineNo : "");
        }
    }
}
