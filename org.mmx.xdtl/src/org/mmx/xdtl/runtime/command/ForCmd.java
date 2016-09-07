/**
 *
 */
package org.mmx.xdtl.runtime.command;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

/**
 * @author vsi
 */
public class ForCmd implements RuntimeCommand {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.for");

    private final Iterable<?> m_iterable;
    private final String      m_loopVarName;
    private final String      m_indexVarName;
    private final String      m_countVarName;
    private final CommandList m_commandList;

    public ForCmd(String loopVarName, String indexVarName, String countVarName,
            Iterable<?> iterable, CommandList commandList) {
        super();
        m_iterable = iterable;
        m_loopVarName = loopVarName;
        m_indexVarName = indexVarName;
        m_countVarName = countVarName;
        m_commandList = commandList;
    }

    /**
     * @see org.mmx.xdtl.runtime.RuntimeCommand#run(org.mmx.xdtl.runtime.Context)
     */
    @Override
    public void run(Context context) throws Throwable {
        TempVar var = new TempVar(context, m_loopVarName);
        TempVar indexVar = new TempVar(context, m_indexVarName);
        TempVar countVar = new TempVar(context, m_countVarName);

        try {
            if (m_iterable == null) {
                countVar.setValue(0);
                logger.info("rowset=null, count=0");
                return;
            } else if (m_iterable instanceof Collection<?>) {
                countVar.setValue(((Collection<?>) m_iterable).size());
            } else if (m_iterable instanceof Map<?, ?>) {
                countVar.setValue(((Map<?, ?>) m_iterable).size());
            } else {
                countVar.setValue(null);
            }

            logger.debug("rowsetClass=" + m_iterable.getClass().getName() +
                    ", count=" + countVar.getValue());

            int index = 0;

            for (Object o: m_iterable) {
                var.setValue(o);
                indexVar.setValue(index++);

                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("%s=%s", m_loopVarName, o));
                }
                context.getEngineControl().execute(m_commandList);
            }
        } finally {
            countVar.remove();
            indexVar.remove();
            var.remove();
        }
    }

    private static class TempVar {
        private boolean m_wasDefined;
        private String m_name;
        private Context m_context;

        public TempVar(Context context, String varName) {
            m_context = context;
            m_name = varName;
            m_wasDefined = context.hasVariable(varName);

            if (!m_wasDefined) {
                context.addVariable(new Variable(varName));
            }
        }

        public void setValue(Object value) {
            m_context.assignVariable(m_name, value);
        }

        public Object getValue() {
            return m_context.getVariableValue(m_name);
        }

        public void remove() {
            if (!m_wasDefined) {
                m_context.undefineVariable(m_name);
            }
        }
    }
}
