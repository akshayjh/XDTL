/**
 * 
 */
package org.mmx.xdtl.runtime.command;

import java.util.Collection;

import org.mmx.xdtl.model.CommandList;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;

/**
 * @author vsi
 */
public class ForCmd implements RuntimeCommand {
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
        TempVar var = createTempVar(context, m_loopVarName);
        TempVar indexVar = createTempVar(context, m_indexVarName);
        TempVar countVar = createTempVar(context, m_countVarName);
        
        if (m_iterable instanceof Collection<?>) {
            countVar.setValue(((Collection<?>) m_iterable).size());
        }
        
        int index = 0;
        
        for (Object o: m_iterable) {
            var.setValue(o);
            indexVar.setValue(index++);
            context.getEngineControl().execute(m_commandList);
        }
        
        removeTempVar(context, countVar);
        removeTempVar(context, indexVar);
        removeTempVar(context, var);
    }

    private TempVar createTempVar(Context context, String varName) {
        Variable var = context.getVariable(varName);
        if (var != null) {
            return new TempVar(var, true);
        }
        
        var = new Variable(varName);
        context.addVariable(var);
        return new TempVar(var, false);
    }
    
    private void removeTempVar(Context context, TempVar var) {
        if (!var.wasDefined()) {
            context.undefineVariable(var.getVariable().getName());
        }
    }
    
    private static class TempVar {
        private boolean m_wasDefined;
        private Variable m_variable;
        
        public TempVar(Variable variable, boolean wasDefined) {
            m_variable = variable;
            m_wasDefined = wasDefined;
        }

        public boolean wasDefined() {
            return m_wasDefined;
        }
        
        public Variable getVariable() {
            return m_variable;
        }
        
        public void setValue(Object value) {
            m_variable.setValue(value);
        }
    }
}
