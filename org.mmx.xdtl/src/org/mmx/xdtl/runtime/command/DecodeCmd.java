package org.mmx.xdtl.runtime.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Decode;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.util.JsonDecoder;
import org.mmx.xdtl.runtime.util.XmlDocumentDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private final Logger Logger = LoggerFactory.getLogger(DecodeCmd.class);
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
		Logger.info(String.format("decode: source='%s', target='%s', type='%s'",
				m_source, m_target, m_type));
		
		String content = readSource();
		
		if (m_type == Decode.Type.JSON)
			parseJson(content, context);
		else if (m_type == Decode.Type.XML)
			parseXml(content, context);
		else
			throw new XdtlException(String.format("Unknown Decode type: '%s'", m_type));
	}
	
	private void parseJson(String content, Context context) {
		List<Object> parsed = new JsonDecoder().Decode(content);
		context.assignVariable(m_target, parsed);
	}

	private void parseXml(String content, Context context) throws Throwable {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringReader reader = new StringReader(content);
		InputSource inputSource = new InputSource(reader);
		Document document = builder.parse(inputSource);
		
		List<Object> parsed = new XmlDocumentDecoder().Decode(document);
		context.assignVariable(m_target, parsed);
	}

	private String readSource() throws Exception {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(m_source).openStream())); 
		
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}
		}
		finally {
			reader.close();
		}
		
		return builder.toString();
	}
}
