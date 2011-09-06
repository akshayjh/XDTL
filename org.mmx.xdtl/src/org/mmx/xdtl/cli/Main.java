package org.mmx.xdtl.cli;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import org.mmx.xdtl.conf.XdtlModule;
import org.mmx.xdtl.runtime.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {
    private static final Logger m_logger = LoggerFactory.getLogger(Main.class);
    private static final String RESOURCE_GLOBALS = "/globals.xml";
    private static final String RESOURCE_CONF = "/xdtlrt.xml";
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            System.exit(-1);
        }
        
        try {
            HashMap<String, Object> argMap = createArgumentMap(args);
            MDC.setContextMap(argMap);
            
            Properties conf = loadProperties(RESOURCE_CONF);
            Injector injector = Guice.createInjector(new XdtlModule(conf));
    
            URL url = new URL(new URL("file:"), args[0]);
            injector.getInstance(Engine.class).run(url, argMap, loadGlobals());
            m_logger.info("done");
            System.exit(0);
        } catch (Throwable t) {
            m_logger.error("execution failed", t);
            System.exit(-1);
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Map<String, Object> loadGlobals() throws Exception {
        return (Map) loadProperties(RESOURCE_GLOBALS);
    }

    private static Properties loadProperties(String resourceName) throws IOException,
            InvalidPropertiesFormatException {
        Properties props = new Properties();
        InputStream is = Main.class.getResourceAsStream(resourceName);
        if (is != null) {
            try {
                props.loadFromXML(is);
            } finally {
                close(is);
            }
        }
        return props;
    }

    private static HashMap<String, Object> createArgumentMap(String[] args)
            throws Exception {
        
        if (args.length < 1) {
            return null;
        }
        
        HashMap<String, Object> map = new HashMap<String, Object>();
        
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            int pos = arg.indexOf('=');
            
            if (pos == -1) {
                throw new Exception("'=' expected in argument '" + arg + "'");
            }

            if (pos == 0) {
                throw new Exception("parameter name is missing from argument '" + arg + "'");
            }

            String name = arg.substring(0, pos);
            String value;
            
            if (pos == arg.length() - 1) {
                value = "";
            } else {
                value = arg.substring(pos + 1, arg.length());
            }

            map.put(name, value);
        }
        
        return map;
    }

    private static void close(InputStream is) {
        try {
            is.close();
        } catch (IOException e) {
            m_logger.warn("Failed to close input stream", e);
        }
    }

    private static void usage() {
        System.out.println("Usage: xdtlrt <task url> [param=val ...]");
    }
}
