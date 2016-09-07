package org.mmx.xdtl.runtime.command;

// import org.apache.log4j.Logger;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.util.FileManipulator;

public class ClearNativeCmd implements RuntimeCommand {
    // private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.clear");

    private final String m_target;
    
    public ClearNativeCmd(String target) {
        super();
        m_target = target;
    }    
	
	@Override
	public void run(Context context) throws Throwable {
		FileManipulator.delete(m_target);
	}

}
