package org.mmx.xdtl.runtime.command.render;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.ResolvingXMLReader;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.Variable;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.util.UrlStreamFactory;
import org.mmx.xdtl.services.UriSchemeParser;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.google.inject.Inject;

public class XslRenderCmd implements RuntimeCommand {
    private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.render.xsl");
    private static final String SAXON_INITIAL_TEMPLATE_FEATURE = "http://saxon.sf.net/feature/initialTemplate";

    private final String m_template;
    private final String m_source;
    private final String m_target;
    private final String m_initialTemplate;
    private final List<Variable> m_parameters;
    private TransformerFactory m_transformerFactory;
    private UriSchemeParser m_uriSchemeParser;
    private UrlStreamFactory m_urlStreamFactory;
    private URL m_packageUrl;


    public XslRenderCmd(String template, String source, String target,
            String initialTemplate, List<Variable> parameters) {
        super();
        m_template = template;
        m_source = source;
        m_target = target;
        m_parameters = parameters;
        m_initialTemplate = initialTemplate;
    }

    @Override
    public void run(Context context) throws Throwable {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("source=%s, template=%s target=%s", m_source, m_template, m_target));
        }

        if (m_target == null || m_target.length() == 0) {
            throw new XdtlException("'target' cannot be null or empty");
        }

        m_packageUrl = context.getPackage().getUrl();
        if (m_initialTemplate != null && m_initialTemplate.length() > 0) {
            m_transformerFactory.setAttribute(SAXON_INITIAL_TEMPLATE_FEATURE, m_initialTemplate);
        }

        Transformer transformer = m_transformerFactory.newTransformer(newXslSource(m_template));

        for (Variable param: m_parameters) {
            transformer.setParameter(param.getName(), param.getValue());
        }

        Source source = newXmlSource(m_source);

        String targetScheme = m_uriSchemeParser.getScheme(m_target);
        boolean targetIsVar = targetScheme.length() == 0;

        if (targetIsVar) {
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            transformer.transform(source, result);
            context.assignVariable(m_target, stringWriter.toString());
        } else {
            OutputStream outputStream = m_urlStreamFactory.getOutputStream(targetScheme, m_target, true);
            try {
                Result result = new StreamResult(outputStream);
                transformer.transform(source, result);
            } finally {
                outputStream.close();
            }
        }
    }

    private Source newXslSource(String source) throws Exception {
        String scheme = m_uriSchemeParser.getScheme(source);

        if (scheme.length() == 0) {
            return new StreamSource(new StringReader(source));
        }

        return new StreamSource(m_urlStreamFactory.getInputStream(source), source);
    }


    private Source newXmlSource(String source) throws Exception {
        if (source == null || source.length() == 0) {
            return null;
        }

        String scheme = m_uriSchemeParser.getScheme(source);
        InputSource inputSource;

        if (scheme.length() == 0) {
            inputSource = new InputSource(new StringReader(source));
        } else {
            inputSource = new InputSource(m_urlStreamFactory.getInputStream(source));
            inputSource.setSystemId(source);
        }

        SAXSource result = new SAXSource(inputSource);
        CatalogManager catalogManager = new CatalogManager();
        catalogManager.setIgnoreMissingProperties(true);
        catalogManager.setUseStaticCatalog(false);

        String xdtlCatUrl = new URL(m_packageUrl, "xdtl.cat").toString();

        @SuppressWarnings("unchecked")
        Vector<String> catalogFiles = (Vector<String>) catalogManager.getCatalogFiles();

        if (catalogFiles == null || catalogFiles.size() == 0) {
            catalogManager.setCatalogFiles(xdtlCatUrl);
        } else {
            catalogManager.setCatalogFiles(xdtlCatUrl + ";" + toDelimitedString(catalogFiles, ';'));
        }

        XMLReader xmlReader = new ResolvingXMLReader(catalogManager);
        result.setXMLReader(xmlReader);
        return result;
    }

    private String toDelimitedString(Vector<String> vector, char delimiter) {
        StringBuilder buf = new StringBuilder();
        for (String s: vector) {
            buf.append(s).append(delimiter);
        }

        if (buf.length() > 0) {
            buf.setLength(buf.length() - 1);
        }

        return buf.toString();
    }

    public TransformerFactory getTransformerFactory() {
        return m_transformerFactory;
    }

    @Inject
    public void setTransformerFactory(TransformerFactory transformerFactory) {
        m_transformerFactory = transformerFactory;
    }

    public UriSchemeParser getUriSchemeParser() {
        return m_uriSchemeParser;
    }

    @Inject
    public void setUriSchemeParser(UriSchemeParser uriSchemeParser) {
        m_uriSchemeParser = uriSchemeParser;
    }

    public UrlStreamFactory getUrlStreamFactory() {
        return m_urlStreamFactory;
    }

    @Inject
    public void setUrlStreamFactory(UrlStreamFactory urlStreamFactory) {
        m_urlStreamFactory = urlStreamFactory;
    }
}
