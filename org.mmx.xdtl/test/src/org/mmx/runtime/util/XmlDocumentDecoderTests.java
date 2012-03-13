package org.mmx.runtime.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Test;
import org.mmx.xdtl.runtime.util.XmlDocumentDecoder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlDocumentDecoderTests {

	@Test
	public void shouldParseEmptyRoot() throws ParserConfigurationException, SAXException, IOException {
		String fragment = "<projects></projects>";
	
		Document doc = getDocument(fragment);
		XmlDocumentDecoder decoder = new XmlDocumentDecoder();
		Map<String,Object> result = decoder.decode(doc);
		
		Assert.assertTrue(result.size() == 0);
	}
	
	@Test
	public void shouldParseSimpleRoot() throws ParserConfigurationException, SAXException, IOException {
		String fragment = "<projects count=\"1\" joke=\"2\"></projects>";
	
		Document doc = getDocument(fragment);
		XmlDocumentDecoder decoder = new XmlDocumentDecoder();
		Map<String,Object> result = decoder.decode(doc);
		
		Assert.assertTrue(result.size() == 2);
	}
	
	@Test
	public void shouldParseDirectChildrenAsProperties() throws ParserConfigurationException, SAXException, IOException {
		String fragment = "<projects count=\"1\" joke=\"2\"><id>3</id><kama /></projects>";
	
		Document doc = getDocument(fragment);
		XmlDocumentDecoder decoder = new XmlDocumentDecoder();
		Map<String,Object> result = decoder.decode(doc);
		
		Assert.assertTrue(result.size() == 4);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldParseChildrenAsArray() throws ParserConfigurationException, SAXException, IOException {
		String fragment = "<projects><project><id>1</id></project><project><id>2</id></project></projects>";
	
		Document doc = getDocument(fragment);
		XmlDocumentDecoder decoder = new XmlDocumentDecoder();
		Map<String,Object> result = decoder.decode(doc);
		
		Assert.assertTrue(result.size() == 1);
		Assert.assertTrue(((List<Map<String,Object>>)result.get("projects")).size() == 2);
	}
	

	private Document getDocument(String fragment)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dfactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(fragment)));
		return doc;
	}
}
