package org.mmx.xdtl.runtime.command.send;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.mmx.xdtl.db.Column;
import org.mmx.xdtl.db.CsvSource;
import org.mmx.xdtl.db.RowHandler;
import org.mmx.xdtl.db.RowSet;
import org.mmx.xdtl.db.Source;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Send;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.TypeConverter;
import org.mmx.xdtl.runtime.command.RtTextFileProperties;
import org.mmx.xdtl.runtime.util.VariableNameValidator;
import org.mmx.xdtl.services.UriSchemeParser;

import com.google.inject.Inject;

public class SendCmd implements RuntimeCommand {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.send");

    private final Object  m_source;
    private final String  m_target;
    private final boolean m_overwrite;
    private VariableNameValidator m_variableNameValidator;
    private UriSchemeParser m_uriSchemeParser;
    private TypeConverter m_typeConverter;
    private RtTextFileProperties<Send.Type> m_textFileProps;
    private boolean m_header;
    private int m_skip;
    private Object m_rowSet;

    public SendCmd(Object source, String target, boolean overwrite,
            RtTextFileProperties<Send.Type> textFileProps, boolean header,
            int skip, Object rowSet) {
        m_source = source;
        m_target = target;
        m_overwrite = overwrite;
        m_textFileProps = textFileProps;
        m_header = header;
        m_skip = skip;
        m_rowSet = rowSet;
    }

    @Override
    public void run(Context context) throws Throwable {
        boolean targetIsVariable = m_variableNameValidator.isValidVariableName(m_target);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("source=%s, target=%s, rowset=%s", m_source, m_target, m_rowSet));
        }

        Object data = m_source;
        if (m_source instanceof String && ((String)m_source).startsWith("file:")) {
            String path = ((String)m_source).substring(5);
            if (path.startsWith("//")) path = path.substring(2);

            Scanner scanner = new Scanner(new File(path));
            try {
            	data = scanner.useDelimiter("\\Z").next();
            } finally {
                scanner.close();
            }
        }

        if (targetIsVariable) {
            context.assignVariable(m_target, data);
            return;
        }

        if (m_rowSet != null && !isEmptyString(m_rowSet)) {
            if (m_source == null || m_source.toString().length() == 0) {
                sendRowSetToTarget(context, (RowSet) m_rowSet);
                return;
            } else if (m_target == null || m_target.length() == 0) {
                sendSourceToRowSet(context, m_typeConverter.toString(m_rowSet));
                return;
            } else {
                throw new XdtlException("Either 'source' and 'rowset' or 'rowset' and 'target' must be given, not all at once");
            }
        }

        String targetScheme = m_uriSchemeParser.getScheme(m_target);
        sendSourceToUrl(targetScheme);
    }

    private boolean isEmptyString(Object obj) {
        return (obj instanceof String) && ((String) obj).length() == 0;
    }

    private void sendSourceToRowSet(Context context, String rowsetVarName) throws Throwable {
        Source source;

        URL url = new URL(m_source.toString());
        InputStream stream = url.openStream();

        try {
            final RowSetHolder rowSetHolder = new RowSetHolder();

            switch (m_textFileProps.getType()) {
            case CSV:
                source = new CsvSource(stream, m_textFileProps.getEncoding(),
                        m_textFileProps.getDelimiter(),
                        m_textFileProps.getQuote(), m_header, m_skip,
                        m_textFileProps.getEscape());
                break;
            default:
                throw new XdtlException("File type " + m_textFileProps.getType() + " is not supported.");
            }

            List<Column> columns = source.getColumns();
            if (columns != null) {
                rowSetHolder.m_rowSet = new RowSet(columns);
            }

            source.fetchRows(new RowHandler() {
                @Override
                public void handleRow(Object[] data, List<String> columnNames)
                        throws Exception {
                    if (rowSetHolder.m_rowSet == null) {
                        rowSetHolder.m_rowSet = new RowSet(data.length);
                    }

                    rowSetHolder.m_rowSet.append(data);
                }
            });

            context.assignVariable(rowsetVarName, rowSetHolder.m_rowSet);
        } finally {
            stream.close();
        }
    }

    private void sendRowSetToTarget(Context context, RowSet source) throws Exception {
        switch (m_textFileProps.getType()) {
        case CSV:
            sendRowSetToCsv(context, source);
            break;
        default:
            throw new XdtlException("File type " + m_textFileProps.getType() + " is not supported.");
        }
    }

    private void sendRowSetToCsv(Context context, RowSet source) throws Exception {
        OutputStream os = openTargetForOutput();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, m_textFileProps.getEncoding()));

        try {
            source.toCsv(writer, m_header, m_textFileProps.getDelimiter(),
                    m_textFileProps.getQuote(),
                    m_textFileProps.getEscape());
        } finally {
            writer.close();
        }
    }

    private void sendSourceToUrl(String targetScheme) throws Exception {
        Writer writer = new BufferedWriter(new OutputStreamWriter(openTargetForOutput(targetScheme)));

        try {
            writer.write("" + m_typeConverter.toString(m_source));
        } finally {
            writer.close();
        }
    }

    private OutputStream openTargetForOutput() throws Exception {
        return openTargetForOutput(m_uriSchemeParser.getScheme(m_target));
    }

    private OutputStream openTargetForOutput(String scheme) throws Exception {
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

    @Inject
    public void setTypeConverter(TypeConverter typeConverter) {
        m_typeConverter = typeConverter;
    }

    private static class RowSetHolder {
        RowSet m_rowSet;
    }
}
