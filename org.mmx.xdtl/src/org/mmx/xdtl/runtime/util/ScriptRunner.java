package org.mmx.xdtl.runtime.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.Context;

public class ScriptRunner {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.rt.util.scriptRunner");

    private ScriptEngine m_scriptEngine;

    public ScriptRunner(ScriptEngine scriptEngine) {
        m_scriptEngine = scriptEngine;
    }

    public Object runScript(String script, Context context) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("Running script '%s'", script));
        }

        try {
            m_scriptEngine.setBindings(context.getBindings(), ScriptContext.ENGINE_SCOPE);
            return m_scriptEngine.eval(script);
        } catch (ScriptException e) {
            throw new XdtlException(e);
        }
    }

    public Object runScript(URL href, String encoding, Context context) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("Running script @ '%s'", href));
        }

        try {
            InputStream stream = href.openStream();
            try {
                return runScript(stream, encoding, context);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.warn("Failed to close input stream", e);
                }
            }
        } catch (Exception e) {
            throw new XdtlException(e);
        }
    }

    public Object runScript(InputStream stream, String encoding, Context context) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));
            try {
                m_scriptEngine.setBindings(context.getBindings(), ScriptContext.ENGINE_SCOPE);
                return m_scriptEngine.eval(reader);
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.warn("Failed to close input stream reader", e);
                }
            }
        } catch (Exception e) {
            throw new XdtlException(e);
        }
    }
}
