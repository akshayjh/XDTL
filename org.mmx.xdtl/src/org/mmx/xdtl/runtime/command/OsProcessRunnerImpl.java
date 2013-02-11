package org.mmx.xdtl.runtime.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.mmx.xdtl.model.XdtlException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class OsProcessRunnerImpl implements OsProcessRunner {
    private static final String DEFAULT_LOGGER_PREFIX = "process.";
    
    private static final Logger logger = Logger.getLogger(OsProcessRunnerImpl.class);
    private final String m_streamEncoding;
    private final String m_loggerPrefix;
    
    @Inject
    public OsProcessRunnerImpl(
            @Named("osprocessrunner.stream.encoding")
            String streamEncoding,
            @Named("osprocessrunner.logger.prefix")
            String loggerPrefix) {
        m_streamEncoding = streamEncoding != null ? streamEncoding : "";
        m_loggerPrefix = loggerPrefix != null ? loggerPrefix : DEFAULT_LOGGER_PREFIX;
    }
    
    @Override
    public OsRunnerResult run(List<String> args) throws Exception {
        if (args == null || args.size() == 0) {
            throw new XdtlException("Argument list cannot be null or empty");
        }
        
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true);
        
        File file = new File(args.get(0));
        Logger procLogger = Logger.getLogger(m_loggerPrefix + file.getName());

        logger.info("Starting process: " + args);
        logger.debug("Stream encoding: '" + m_streamEncoding + ", logger name='" + procLogger.getName() + "'");

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
            logger.info(s);
			lineno++;
        }
        
        proc.waitFor();
        
        int exitValue = proc.exitValue();
        logger.info("Process ended with exit value " + exitValue);
        
        return new OsRunnerResult(exitValue, builder.toString());
    }
}
