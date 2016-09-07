package org.mmx.xdtl.runtime.command;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;
import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Decode;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.util.JsonDecoder;
import org.mmx.xdtl.runtime.util.UrlReader;
import org.mmx.xdtl.runtime.util.XmlDocumentDecoder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Decode reads from given hierarchical source (json, xml) and translates it to List/Map structure
 * in memory
 *
 * @author urmo
 *
 */
public class DecodeCmd implements RuntimeCommand {
	private static final Logger logger = XdtlLogger.getLogger("xdtl.cmd.decode");

	private String m_source;
	private String m_target;
	private Decode.Type m_type;

	public DecodeCmd(String source, String target, Decode.Type type) {
		m_source = source;
		m_target = target;
		m_type = type;
	}

	@Override
	public void run(Context context) throws Throwable {
		logger.info("source=" + m_source);

		UrlReader reader = new UrlReader();
		String content = reader.read(m_source);

		int rootNodeCount;
		if (m_type == Decode.Type.JSON)
			rootNodeCount = parseJson(content, context);
		else if (m_type == Decode.Type.XML)
			rootNodeCount = parseXml(content, context);
		else
			throw new XdtlException(String.format("Unknown Decode type: '%s'", m_type));

		logger.info(String.format("bytes read=%d, root elements parsed=%d",
		        reader.getBytesRead(), rootNodeCount));
	}

	private int parseJson(String content, Context context) {
	    Object result = new JsonDecoder().decode(content);
		context.assignVariable(m_target, result);

		if (result instanceof List<?>) {
		    return ((List<?>) result).size();
		} else if (result != null) {
		    return 1;
		}

		return 0;
	}

	private int parseXml(String content, Context context) throws Throwable {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringReader reader = new StringReader(content);
		InputSource inputSource = new InputSource(reader);
		Document document = builder.parse(inputSource);

		Map<String,Object> parsed = new XmlDocumentDecoder().decode(document);
		context.assignVariable(m_target, parsed);

		List<?> items = (List<?>) parsed.get("items");
		if (items != null) {
		    return items.size();
		}

		return 0;
	}
}
