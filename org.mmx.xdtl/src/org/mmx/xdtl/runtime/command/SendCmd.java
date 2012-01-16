package org.mmx.xdtl.runtime.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URLConnection;

import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.util.StringShortener;
import org.mmx.xdtl.runtime.util.VariableNameValidator;
import org.mmx.xdtl.services.UriSchemeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class SendCmd implements RuntimeCommand {
    private final Logger  m_logger = LoggerFactory.getLogger(SendCmd.class);    
    private final Object  m_source;
    private final String  m_target;
    private final boolean m_overwrite;
    private VariableNameValidator m_variableNameValidator;
    private StringShortener m_stringShortener;
    private UriSchemeParser m_uriSchemeParser;
    
    public SendCmd(Object source, String target, Boolean overwrite) {
        m_source = source;
        m_target = target;
        m_overwrite = overwrite.booleanValue();
    }
    
    @Override
    public void run(Context context) throws Throwable {
        boolean targetIsVariable = m_variableNameValidator.isValidVariableName(m_target);

        m_logger.info(String.format("send: source='%s' target='%s' overwrite='%s' targetIsVariable='%s'",
                m_source,
                m_stringShortener.shorten(m_target),
                Boolean.toString(m_overwrite),
                Boolean.toString(targetIsVariable)));
        
        if (targetIsVariable) {
            context.assignVariable(m_target, m_source);
            return;
        }
        
        sendSourceToUrl();
    }

    private void sendSourceToUrl() throws Exception {
        Writer writer = new BufferedWriter(new OutputStreamWriter(openTargetForOutput()));

        try {
            writer.write("" + m_source);
        } finally {
            writer.close();
        }
    }

    private OutputStream openTargetForOutput() throws Exception {
        String scheme = m_uriSchemeParser.getScheme(m_target);
        if (scheme.length() == 0) {
            throw new XdtlException("Target URI '" + m_target + "' is without scheme");
        }

        if (scheme.equals("file")) {
            String filePath;
            filePath = m_target.substring(scheme.length() + 1);
            if (filePath.startsWith("//")) filePath = filePath.substring(2);
            
            File f = new File(filePath);
            if (f.isDirectory()) {
                throw new XdtlException("'" + m_target + "' is a directory");
            }

            if (!m_overwrite && f.exists()) {
                throw new XdtlException("'" + m_target + "' exists");
            }

            return new FileOutputStream(f);
        }
        
        URI uri = new URI(m_target);
        URLConnection cnn = uri.toURL().openConnection();
        cnn.setDoOutput(true);
        cnn.setDoInput(false);
        return cnn.getOutputStream();
    }

    @Inject
    public void setVariableNameValidator(VariableNameValidator variableNameValidator) {
        m_variableNameValidator = variableNameValidator;
    }

    @Inject
    public void setStringShortener(StringShortener stringShortener) {
        m_stringShortener = stringShortener;
    }

    @Inject
    public void setUriSchemeParser(UriSchemeParser uriSchemeParser) {
        m_uriSchemeParser = uriSchemeParser;
    }
}
