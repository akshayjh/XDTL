package org.mmx.xdtl.runtime.impl;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.log.XdtlMdc;
import org.mmx.xdtl.log.XdtlMdc.MdcState;
import org.mmx.xdtl.model.Command;
import org.mmx.xdtl.model.SourceLocator;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.CommandBuilder;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.ExpressionEvaluator;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;
import org.mmx.xdtl.runtime.XdtlError;
import org.mmx.xdtl.services.Injector;

import com.google.inject.Inject;

public class CommandInvokerImpl implements CommandInvoker {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.rt.commandInvoker");
    private final CommandMappingSet m_mappings;
    private final Injector m_injector;
    private final ExpressionEvaluator m_exprEvaluator;
    private final TypeConverter m_typeConverter;

    @Inject
    CommandInvokerImpl(CommandMappingSet mappings, Injector injector,
            ExpressionEvaluator exprEvaluator, TypeConverter typeConverter) {
        m_mappings = mappings;
        m_injector = injector;
        m_exprEvaluator = exprEvaluator;
        m_typeConverter = typeConverter;
    }

    /* (non-Javadoc)
     * @see org.mmx.xdtl.runtime.CommandInvoker#invoke(org.mmx.xdtl.model.Command, org.mmx.xdtl.runtime.Context)
     */
    public void invoke(Command cmd, Context context) {
        MdcState mdcState = XdtlMdc.saveState();

        SourceLocator locator = cmd.getSourceLocator();
        String stepName = locator.getTagName();
        XdtlMdc.setState(stepName, locator);

        try {
            Boolean noLog = m_typeConverter.toBoolean(m_exprEvaluator.evaluate(context, cmd.getNoLog()));
            if (noLog == null) {
                noLog = Boolean.FALSE;
            }

            XdtlMdc.setLoggingDisabled(noLog);

            CommandMapping mapping = m_mappings.findByModelClass(cmd.getClass());
            if (mapping == null) {
                throw new XdtlException("Command mapping for class '"
                        + cmd.getClass().getName() + "' does not exist.",
                        cmd.getSourceLocator());
            }

            try {
                CommandBuilder builder = m_injector.getInstance(CommandBuilder.class,
                        cmd.getClass().getName());

                RuntimeCommand runtimeCmd = builder.build(context, mapping, cmd);

                m_injector.injectMembers(runtimeCmd);

                if (logger.isTraceEnabled()) {
                    logger.trace("running command '" + runtimeCmd.getClass().getName() + "'");
                }

                runtimeCmd.run(context);
            } catch (XdtlExitException e) {
                throw e;
            } catch (XdtlError e) {
                throw new XdtlException(e.getMessage(), cmd.getSourceLocator(), e);
            } catch (XdtlException e) {
                if (e.getSourceLocator().isNull()) {
                    e.setSourceLocator(cmd.getSourceLocator());
                }
                throw e;
            } catch (Throwable t) {
                throw new XdtlException(cmd.getSourceLocator(), t);
            }
        } finally {
            XdtlMdc.restoreState(mdcState);
        }
    }
}
