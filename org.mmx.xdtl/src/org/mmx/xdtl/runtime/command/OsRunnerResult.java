package org.mmx.xdtl.runtime.command;

/**
 * Result returned by OsProcessRunner
 */
public class OsRunnerResult {
	
	private int m_exitCode;
	private String m_output;

	public OsRunnerResult(int exitCode) {
		super();
		m_exitCode = exitCode;
	}
	
	public OsRunnerResult(int exitCode, String output) {
		super();
		m_exitCode = exitCode;
		m_output = output;
	}
	
	public int getExitCode() {
		return m_exitCode;
	}
	
	public String getOutput() {
		return m_output;
	}
}
