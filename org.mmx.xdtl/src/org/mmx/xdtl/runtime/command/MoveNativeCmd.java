package org.mmx.xdtl.runtime.command;

// import org.apache.log4j.Logger;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.util.FileManipulator;

public class MoveNativeCmd implements RuntimeCommand {
    // private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.move");

    private final String m_source;
    private final String m_target;
    private final boolean m_overwrite;
    
    public MoveNativeCmd(String source, String target, boolean overwrite) {
        super();
        m_source = source;
        m_target = target;
        m_overwrite = overwrite;
    }    
	
	@Override
	public void run(Context context) throws Throwable {
		FileManipulator.move(m_source, m_target, m_overwrite);
	}

}
