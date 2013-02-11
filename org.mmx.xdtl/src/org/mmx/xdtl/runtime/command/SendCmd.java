package org.mmx.xdtl.runtime.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.util.VariableNameValidator;
import org.mmx.xdtl.services.UriSchemeParser;

import com.google.inject.Inject;

public class SendCmd implements RuntimeCommand {
    private static final Logger logger = Logger.getLogger("xdtl.cmd.send");

    private final Object  m_source;
    private final String  m_target;
    private final boolean m_overwrite;
    private VariableNameValidator m_variableNameValidator;
    private UriSchemeParser m_uriSchemeParser;
    
    public SendCmd(Object source, String target, Boolean overwrite) {
        m_source = source;
        m_target = target;
        m_overwrite = overwrite.booleanValue();
    }
    
    @Override
    public void run(Context context) throws Throwable {
        boolean targetIsVariable = m_variableNameValidator.isValidVariableName(m_target);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("source='%s', target='%s'", m_source, m_target));
        }
        
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

            boolean append = false;
            if (!m_overwrite && f.exists()) {
                append = true;
            }

            return new FileOutputStream(f, append);
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
    public void setUriSchemeParser(UriSchemeParser uriSchemeParser) {
        m_uriSchemeParser = uriSchemeParser;
    }
}
