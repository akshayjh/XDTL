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
		
		Assert.assertTrue(result.size() == 1);
	}
	
	@Test
	public void shouldParseSimpleRoot() throws ParserConfigurationException, SAXException, IOException {
		String fragment = "<projects count=\"1\" joke=\"2\"></projects>";
	
		Document doc = getDocument(fragment);
		XmlDocumentDecoder decoder = new XmlDocumentDecoder();
		Map<String,Object> result = decoder.decode(doc);
		
		System.out.println(result.toString());
		Assert.assertTrue(result.size() == 3);
		
	}
	
	@Test
	public void shouldParseDirectChildrenAsProperties() throws ParserConfigurationException, SAXException, IOException {
		String fragment = "<projects count=\"1\" joke=\"2\"><id>3</id><kama /></projects>";
	
		Document doc = getDocument(fragment);
		XmlDocumentDecoder decoder = new XmlDocumentDecoder();
		Map<String,Object> result = decoder.decode(doc);
		
		System.out.println(result.toString());
		Assert.assertTrue(result.size() == 5);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldParseChildrenAsArray() throws ParserConfigurationException, SAXException, IOException {
		String fragment = "<projects><project><id>1</id></project><project><id>2</id></project></projects>";
	
		Document doc = getDocument(fragment);
		XmlDocumentDecoder decoder = new XmlDocumentDecoder();
		Map<String,Object> result = decoder.decode(doc);
		
		System.out.println(result.toString());
		Assert.assertTrue(result.size() == 2);
		Assert.assertTrue(((List<Map<String,Object>>)result.get("projects")).size() == 2);
	}
	
	@Test
	public void shouldParseSimpleMultichildren() throws ParserConfigurationException, SAXException, IOException {
		String fragment = "<projects count=\"1\" joke=\"2\"><p>1</p><p>2</p><p>3</p></projects>";
	
		Document doc = getDocument(fragment);
		XmlDocumentDecoder decoder = new XmlDocumentDecoder();
		Map<String,Object> result = decoder.decode(doc);
		System.out.println(result.toString());
		Assert.assertTrue(result.size() == 4);
	}
	
	@Test
	public void shouldParseSingleChildToPropertyObject() throws ParserConfigurationException, SAXException, IOException {
		String fragment = "<p a=\"1\"><p1><p1a>1</p1a></p1><p2>Text</p2></p>";
	
		Document doc = getDocument(fragment);
		XmlDocumentDecoder decoder = new XmlDocumentDecoder();
		Map<String,Object> result = decoder.decode(doc);
		System.out.println(result.toString());
	}

	@Test
	public void shouldParseAll() throws ParserConfigurationException, SAXException, IOException {
		String fragment = "<projects count=\"2\"><project><id>34</id><name>Project öäüe</name><identifier>demo-demo1</identifier><description/><parent name=\"Something parent\" id=\"21\"/><created_on>2011-12-15T17:31:21+02:00</created_on><updated_on>2011-12-15T17:31:21+02:00</updated_on></project><project><id>35</id><name>Öärk</name><identifier>eas-ajakirjad</identifier><description/><parent name=\"Plumps\" id=\"11\"/><created_on>2011-12-15T17:31:21+02:00</created_on><updated_on>2011-12-15T17:31:21+02:00</updated_on></project></projects>";
	
		Document doc = getDocument(fragment);
		XmlDocumentDecoder decoder = new XmlDocumentDecoder();
		Map<String,Object> result = decoder.decode(doc);
		System.out.println(result.toString());
	}

	private Document getDocument(String fragment)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dfactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(fragment)));
		return doc;
	}
}
