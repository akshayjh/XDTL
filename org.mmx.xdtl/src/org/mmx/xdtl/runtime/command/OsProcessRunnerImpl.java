package org.mmx.xdtl.runtime.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.XdtlException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class OsProcessRunnerImpl implements OsProcessRunner {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.exec");
    private final String m_streamEncoding;

    @Inject
    public OsProcessRunnerImpl(@Named("osprocessrunner.stream.encoding") String streamEncoding) {
        m_streamEncoding = streamEncoding != null ? streamEncoding : "";
    }

    @Override
    public OsRunnerResult run(List<String> args) throws Exception {
        if (args == null || args.size() == 0) {
            throw new XdtlException("Argument list cannot be null or empty");
        }

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true);

        if (logger.isTraceEnabled()) {
            logger.trace(String.format("Starting process: '%s', encoding=%s", args, m_streamEncoding));
        }

        Process proc = pb.start();
        InputStreamReader reader;

        if (m_streamEncoding.length() != 0) {
            reader = new InputStreamReader(proc.getInputStream(), m_streamEncoding);
        } else {
            reader = new InputStreamReader(proc.getInputStream());
        }

        BufferedReader stdoutReader = new BufferedReader(reader);

        String s;
        StringBuilder builder = new StringBuilder();

		int lineno = 0;
        while (true) {
            s = stdoutReader.readLine();
            if (s == null) break;
			if (lineno != 0) builder.append("\n");
            builder.append(s);
            if (logger.isDebugEnabled()) {
                logger.debug(s);
            }
			lineno++;
        }

        proc.waitFor();

        int exitValue = proc.exitValue();
        if (logger.isTraceEnabled()) {
            logger.trace("Process ended with exit value " + exitValue);
        }

        return new OsRunnerResult(exitValue, builder.toString());
    }
}
