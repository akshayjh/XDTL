package org.mmx.xdtl.runtime.impl;

public class TaskRunResult {
	private boolean m_exit;
	private int m_exitCode;
	private boolean m_exitRuntime;
	
	private static TaskRunResult m_success = new TaskRunResult();
	
	public TaskRunResult() {
		super();
		m_exit = false;
		m_exitCode = 0;
		m_exitRuntime = false;
	}

	public TaskRunResult(int code, boolean exitRuntime) {
		super();
		m_exit = true;
		m_exitCode = code;
		m_exitRuntime = exitRuntime;
	}

	public boolean getExit() {
		return m_exit;
	}

	public int getExitCode() {
		return m_exitCode;
	}

	public boolean getExitRuntime() {
		return m_exitRuntime;
	}
	
	public static TaskRunResult success() {
		return m_success;
	}
}
