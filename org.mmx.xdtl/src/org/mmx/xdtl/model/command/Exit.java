package org.mmx.xdtl.model.command;

import org.mmx.xdtl.model.AbstractElement;
import org.mmx.xdtl.model.Command;

public class Exit extends AbstractElement implements Command {
	private final String m_code;
	private final String m_global;
	
	public Exit(String code, String global) {
		super();
		m_code = code;
		m_global = global;
	}

	public String getCode() {
		return m_code;
	}

	public String getGlobal() {
		return m_global;
	}
}
