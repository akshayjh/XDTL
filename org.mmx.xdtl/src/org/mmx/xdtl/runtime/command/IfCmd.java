/**
 * 
 */
package org.mmx.xdtl.runtime.command;

import org.apache.log4j.Logger;
import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

/**
 * @author vsi
 */
public class IfCmd implements RuntimeCommand {
    private static final Logger logger = Logger.getLogger(IfCmd.class);
    
    private final boolean m_condition;
    private final CommandList m_commandList;
    
    public IfCmd(boolean condition, CommandList commandList) {
        super();
        m_condition = condition;
        m_commandList = commandList;
    }

    /**
     * @see org.mmx.xdtl.runtime.RuntimeCommand#run(org.mmx.xdtl.runtime.Context)
     */
    @Override
    public void run(Context context) throws Throwable {
        if (m_condition) {
            logger.debug("Condition is true, executing commands");
            context.getEngineControl().execute(m_commandList);
        } else {
            logger.debug("Condition is false, skipping commands");
        }
    }
}
