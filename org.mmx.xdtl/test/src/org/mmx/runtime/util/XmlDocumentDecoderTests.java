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
	
	@Test
	public void shouldParseTerriblyLongXml() throws ParserConfigurationException, SAXException, IOException {
		Document doc = getDocument(terriblyLongXml);
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
	
	private static String terriblyLongXml = "<?xml version='1.0' encoding='UTF-8'?><feed xmlns='http://www.w3.org/2005/Atom' xmlns:media='http://search.yahoo.com/mrss/' xmlns:openSearch='http://a9.com/-/spec/opensearchrss/1.0/' xmlns:gd='http://schemas.google.com/g/2005' xmlns:yt='http://gdata.youtube.com/schemas/2007'>		<id>http://gdata.youtube.com/feeds/api/users/Didits1/uploads</id><updated>2012-03-15T18:22:58.462Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://gdata.youtube.com/schemas/2007#video'/><title type='text'>Uploads by Didits1</title><logo>http://www.youtube.com/img/pic_youtubelogo_123x63.gif</logo><link rel='related' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/users/didits1'/><link rel='alternate' type='text/html' href='https://www.youtube.com/user/Didits1/videos'/><link rel='http://schemas.google.com/g/2005#feed' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/users/Didits1/uploads'/><link rel='http://schemas.google.com/g/2005#batch' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/users/Didits1/uploads/batch'/><link rel='self' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/users/Didits1/uploads?start-index=1&amp;max-results=25'/><author><name>Didits1</name><uri>https://gdata.youtube.com/feeds/api/users/Didits1</uri></author><generator version='2.1' uri='http://gdata.youtube.com'>YouTube data API</generator><openSearch:totalResults>5</openSearch:totalResults><openSearch:startIndex>1</openSearch:startIndex><openSearch:itemsPerPage>25</openSearch:itemsPerPage><entry><id>http://gdata.youtube.com/feeds/api/videos/IDjydWRY2t0</id><published>2011-08-31T14:49:31.000Z</published><updated>2011-11-11T23:54:28.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://gdata.youtube.com/schemas/2007#video'/><category scheme='http://gdata.youtube.com/schemas/2007/categories.cat' term='Music' label='Muusika'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Elton'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='John'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Funeral'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='for'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Friend'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Live'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Sound'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Piano'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Solo'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Yamaha'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='CLP-370'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Roland'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='P-330'/><title type='text'>Searching for the Elton John live piano sound</title><content type='text'/><link rel='alternate' type='text/html' href='https://www.youtube.com/watch?v=IDjydWRY2t0&amp;feature=youtube_gdata'/><link rel='http://gdata.youtube.com/schemas/2007#video.responses' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/videos/IDjydWRY2t0/responses'/><link rel='http://gdata.youtube.com/schemas/2007#video.related' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/videos/IDjydWRY2t0/related'/><link rel='http://gdata.youtube.com/schemas/2007#mobile' type='text/html' href='https://m.youtube.com/details?v=IDjydWRY2t0'/><link rel='self' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/users/Didits1/uploads/IDjydWRY2t0'/><author><name>Didits1</name><uri>https://gdata.youtube.com/feeds/api/users/Didits1</uri></author><gd:comments><gd:feedLink rel='http://gdata.youtube.com/schemas/2007#comments' href='https://gdata.youtube.com/feeds/api/videos/IDjydWRY2t0/comments' countHint='1'/></gd:comments><yt:hd/><media:group><media:category label='Muusika' scheme='http://gdata.youtube.com/schemas/2007/categories.cat'>Music</media:category><media:content url='https://www.youtube.com/v/IDjydWRY2t0?version=3&amp;f=user_uploads&amp;app=youtube_gdata' type='application/x-shockwave-flash' medium='video' isDefault='true' expression='full' duration='110' yt:format='5'/><media:content url='rtsp://v4.cache4.c.youtube.com/CigLENy73wIaHwnd2lhkdfI4IBMYDSANFEgGUgx1c2VyX3VwbG9hZHMM/0/0/0/video.3gp' type='video/3gpp' medium='video' expression='full' duration='110' yt:format='1'/><media:content url='rtsp://v2.cache8.c.youtube.com/CigLENy73wIaHwnd2lhkdfI4IBMYESARFEgGUgx1c2VyX3VwbG9hZHMM/0/0/0/video.3gp' type='video/3gpp' medium='video' expression='full' duration='110' yt:format='6'/><media:description type='plain'/><media:keywords>Elton, John, Funeral, for, Friend, Live, Sound, Piano, Solo, Yamaha, CLP-370, Roland, P-330</media:keywords><media:player url='https://www.youtube.com/watch?v=IDjydWRY2t0&amp;feature=youtube_gdata_player'/><media:thumbnail url='http://i.ytimg.com/vi/IDjydWRY2t0/0.jpg' height='360' width='480' time='00:00:55'/><media:thumbnail url='http://i.ytimg.com/vi/IDjydWRY2t0/1.jpg' height='90' width='120' time='00:00:27.500'/><media:thumbnail url='http://i.ytimg.com/vi/IDjydWRY2t0/2.jpg' height='90' width='120' time='00:00:55'/><media:thumbnail url='http://i.ytimg.com/vi/IDjydWRY2t0/3.jpg' height='90' width='120' time='00:01:22.500'/><media:title type='plain'>Searching for the Elton John live piano sound</media:title><yt:duration seconds='110'/></media:group><gd:rating average='5.0' max='5' min='1' numRaters='1' rel='http://schemas.google.com/g/2005#overall'/><yt:statistics favoriteCount='0' viewCount='215'/></entry><entry><id>http://gdata.youtube.com/feeds/api/videos/fiWGT9VSzuk</id><published>2011-08-31T10:00:26.000Z</published><updated>2012-02-01T00:31:28.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://gdata.youtube.com/schemas/2007#video'/><category scheme='http://gdata.youtube.com/schemas/2007/categories.cat' term='Music' label='Muusika'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Elton'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='John'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Carla'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Etude'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Live'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Sound'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Piano'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Solo'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Yamaha'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='CLP-370'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Roland'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='P-330'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='cover'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='music'/><title type='text'>Elton John - Carla-Etude piano cover</title><content type='text'>Piano cover (well I tried...) of Carla/Etude more or less as played by Elton John during solo shows. Trademark Elton John live piano sound using Yamaha CLP-370 piano and Roland P-330 sound module. As usual, a few mistakes here and there.</content><link rel='alternate' type='text/html' href='https://www.youtube.com/watch?v=fiWGT9VSzuk&amp;feature=youtube_gdata'/><link rel='http://gdata.youtube.com/schemas/2007#video.responses' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/videos/fiWGT9VSzuk/responses'/><link rel='http://gdata.youtube.com/schemas/2007#video.related' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/videos/fiWGT9VSzuk/related'/><link rel='http://gdata.youtube.com/schemas/2007#mobile' type='text/html' href='https://m.youtube.com/details?v=fiWGT9VSzuk'/><link rel='self' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/users/Didits1/uploads/fiWGT9VSzuk'/><author><name>Didits1</name><uri>https://gdata.youtube.com/feeds/api/users/Didits1</uri></author><gd:comments><gd:feedLink rel='http://gdata.youtube.com/schemas/2007#comments' href='https://gdata.youtube.com/feeds/api/videos/fiWGT9VSzuk/comments' countHint='1'/></gd:comments><yt:hd/><media:group><media:category label='Muusika' scheme='http://gdata.youtube.com/schemas/2007/categories.cat'>Music</media:category><media:content url='https://www.youtube.com/v/fiWGT9VSzuk?version=3&amp;f=user_uploads&amp;app=youtube_gdata' type='application/x-shockwave-flash' medium='video' isDefault='true' expression='full' duration='224' yt:format='5'/><media:content url='rtsp://v1.cache6.c.youtube.com/CigLENy73wIaHwnpzlLVT4YlfhMYDSANFEgGUgx1c2VyX3VwbG9hZHMM/0/0/0/video.3gp' type='video/3gpp' medium='video' expression='full' duration='224' yt:format='1'/><media:content url='rtsp://v3.cache5.c.youtube.com/CigLENy73wIaHwnpzlLVT4YlfhMYESARFEgGUgx1c2VyX3VwbG9hZHMM/0/0/0/video.3gp' type='video/3gpp' medium='video' expression='full' duration='224' yt:format='6'/><media:description type='plain'>Piano cover (well I tried...) of Carla/Etude more or less as played by Elton John during solo shows. Trademark Elton John live piano sound using Yamaha CLP-370 piano and Roland P-330 sound module. As usual, a few mistakes here and there.</media:description><media:keywords>Elton, John, Carla, Etude, Live, Sound, Piano, Solo, Yamaha, CLP-370, Roland, P-330, cover, music</media:keywords><media:player url='https://www.youtube.com/watch?v=fiWGT9VSzuk&amp;feature=youtube_gdata_player'/><media:thumbnail url='http://i.ytimg.com/vi/fiWGT9VSzuk/0.jpg' height='360' width='480' time='00:01:52'/><media:thumbnail url='http://i.ytimg.com/vi/fiWGT9VSzuk/1.jpg' height='90' width='120' time='00:00:56'/><media:thumbnail url='http://i.ytimg.com/vi/fiWGT9VSzuk/2.jpg' height='90' width='120' time='00:01:52'/><media:thumbnail url='http://i.ytimg.com/vi/fiWGT9VSzuk/3.jpg' height='90' width='120' time='00:02:48'/><media:title type='plain'>Elton John - Carla-Etude piano cover</media:title><yt:duration seconds='224'/></media:group><gd:rating average='5.0' max='5' min='1' numRaters='7' rel='http://schemas.google.com/g/2005#overall'/><yt:statistics favoriteCount='2' viewCount='601'/></entry><entry><id>http://gdata.youtube.com/feeds/api/videos/OOfFdmrd6TA</id><published>2009-09-08T13:17:44.000Z</published><updated>2012-03-12T04:19:47.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://gdata.youtube.com/schemas/2007#video'/><category scheme='http://gdata.youtube.com/schemas/2007/categories.cat' term='Music' label='Muusika'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Elton'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='John'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Candle'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Wind'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Piano'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Cover'/><title type='text'>Candle in the wind piano cover</title><content type='text'>As usual with a few mistakes here and there.</content><link rel='alternate' type='text/html' href='https://www.youtube.com/watch?v=OOfFdmrd6TA&amp;feature=youtube_gdata'/><link rel='http://gdata.youtube.com/schemas/2007#video.responses' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/videos/OOfFdmrd6TA/responses'/><link rel='http://gdata.youtube.com/schemas/2007#video.related' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/videos/OOfFdmrd6TA/related'/><link rel='http://gdata.youtube.com/schemas/2007#mobile' type='text/html' href='https://m.youtube.com/details?v=OOfFdmrd6TA'/><link rel='self' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/users/Didits1/uploads/OOfFdmrd6TA'/><author><name>Didits1</name><uri>https://gdata.youtube.com/feeds/api/users/Didits1</uri></author><gd:comments><gd:feedLink rel='http://gdata.youtube.com/schemas/2007#comments' href='https://gdata.youtube.com/feeds/api/videos/OOfFdmrd6TA/comments' countHint='7'/></gd:comments><media:group><media:category label='Muusika' scheme='http://gdata.youtube.com/schemas/2007/categories.cat'>Music</media:category><media:content url='https://www.youtube.com/v/OOfFdmrd6TA?version=3&amp;f=user_uploads&amp;app=youtube_gdata' type='application/x-shockwave-flash' medium='video' isDefault='true' expression='full' duration='230' yt:format='5'/><media:content url='rtsp://v5.cache3.c.youtube.com/CigLENy73wIaHwkw6d1qdsXnOBMYDSANFEgGUgx1c2VyX3VwbG9hZHMM/0/0/0/video.3gp' type='video/3gpp' medium='video' expression='full' duration='230' yt:format='1'/><media:content url='rtsp://v4.cache4.c.youtube.com/CigLENy73wIaHwkw6d1qdsXnOBMYESARFEgGUgx1c2VyX3VwbG9hZHMM/0/0/0/video.3gp' type='video/3gpp' medium='video' expression='full' duration='230' yt:format='6'/><media:description type='plain'>As usual with a few mistakes here and there.</media:description><media:keywords>Elton, John, Candle, Wind, Piano, Cover</media:keywords><media:player url='https://www.youtube.com/watch?v=OOfFdmrd6TA&amp;feature=youtube_gdata_player'/><media:thumbnail url='http://i.ytimg.com/vi/OOfFdmrd6TA/0.jpg' height='360' width='480' time='00:01:55'/><media:thumbnail url='http://i.ytimg.com/vi/OOfFdmrd6TA/1.jpg' height='90' width='120' time='00:00:57.500'/><media:thumbnail url='http://i.ytimg.com/vi/OOfFdmrd6TA/2.jpg' height='90' width='120' time='00:01:55'/><media:thumbnail url='http://i.ytimg.com/vi/OOfFdmrd6TA/3.jpg' height='90' width='120' time='00:02:52.500'/><media:title type='plain'>Candle in the wind piano cover</media:title><yt:duration seconds='230'/></media:group><gd:rating average='5.0' max='5' min='1' numRaters='12' rel='http://schemas.google.com/g/2005#overall'/><yt:statistics favoriteCount='7' viewCount='3806'/></entry><entry><id>http://gdata.youtube.com/feeds/api/videos/4WrvVVJi8y0</id><published>2009-09-08T08:42:21.000Z</published><updated>2010-08-27T01:00:02.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://gdata.youtube.com/schemas/2007#video'/><category scheme='http://gdata.youtube.com/schemas/2007/categories.cat' term='Music' label='Muusika'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Coldplay'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Lost'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Piano'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Instrumental'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Cover'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Keyboard'/><title type='text'>Coldplay: Lost instrumental piano cover</title><content type='text'/><link rel='alternate' type='text/html' href='https://www.youtube.com/watch?v=4WrvVVJi8y0&amp;feature=youtube_gdata'/><link rel='http://gdata.youtube.com/schemas/2007#video.responses' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/videos/4WrvVVJi8y0/responses'/><link rel='http://gdata.youtube.com/schemas/2007#video.related' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/videos/4WrvVVJi8y0/related'/><link rel='http://gdata.youtube.com/schemas/2007#mobile' type='text/html' href='https://m.youtube.com/details?v=4WrvVVJi8y0'/><link rel='self' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/users/Didits1/uploads/4WrvVVJi8y0'/><author><name>Didits1</name><uri>https://gdata.youtube.com/feeds/api/users/Didits1</uri></author><gd:comments><gd:feedLink rel='http://gdata.youtube.com/schemas/2007#comments' href='https://gdata.youtube.com/feeds/api/videos/4WrvVVJi8y0/comments' countHint='1'/></gd:comments><yt:location>Lyon, France</yt:location><media:group><media:category label='Muusika' scheme='http://gdata.youtube.com/schemas/2007/categories.cat'>Music</media:category><media:content url='https://www.youtube.com/v/4WrvVVJi8y0?version=3&amp;f=user_uploads&amp;app=youtube_gdata' type='application/x-shockwave-flash' medium='video' isDefault='true' expression='full' duration='163' yt:format='5'/><media:content url='rtsp://v8.cache6.c.youtube.com/CigLENy73wIaHwkt82JSVe9q4RMYDSANFEgGUgx1c2VyX3VwbG9hZHMM/0/0/0/video.3gp' type='video/3gpp' medium='video' expression='full' duration='163' yt:format='1'/><media:content url='rtsp://v2.cache4.c.youtube.com/CigLENy73wIaHwkt82JSVe9q4RMYESARFEgGUgx1c2VyX3VwbG9hZHMM/0/0/0/video.3gp' type='video/3gpp' medium='video' expression='full' duration='163' yt:format='6'/><media:description type='plain'/><media:keywords>Coldplay, Lost, Piano, Instrumental, Cover, Keyboard</media:keywords><media:player url='https://www.youtube.com/watch?v=4WrvVVJi8y0&amp;feature=youtube_gdata_player'/><media:thumbnail url='http://i.ytimg.com/vi/4WrvVVJi8y0/0.jpg' height='360' width='480' time='00:01:21.500'/><media:thumbnail url='http://i.ytimg.com/vi/4WrvVVJi8y0/1.jpg' height='90' width='120' time='00:00:40.750'/><media:thumbnail url='http://i.ytimg.com/vi/4WrvVVJi8y0/2.jpg' height='90' width='120' time='00:01:21.500'/><media:thumbnail url='http://i.ytimg.com/vi/4WrvVVJi8y0/3.jpg' height='90' width='120' time='00:02:02.250'/><media:title type='plain'>Coldplay: Lost instrumental piano cover</media:title><yt:duration seconds='163'/></media:group><gd:rating average='5.0' max='5' min='1' numRaters='3' rel='http://schemas.google.com/g/2005#overall'/><yt:recorded>2009-09-08</yt:recorded><yt:statistics favoriteCount='2' viewCount='1134'/></entry><entry><id>http://gdata.youtube.com/feeds/api/videos/G8RhD2RPbQI</id><published>2009-09-07T17:35:25.000Z</published><updated>2010-10-22T10:22:42.000Z</updated><category scheme='http://schemas.google.com/g/2005#kind' term='http://gdata.youtube.com/schemas/2007#video'/><category scheme='http://gdata.youtube.com/schemas/2007/categories.cat' term='Music' label='Muusika'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Coldplay'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Piano'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Postcards'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Away'/><category scheme='http://gdata.youtube.com/schemas/2007/keywords.cat' term='Cover'/><title type='text'>Coldplay: Postcards from far away cover</title><content type='text'>Sorry cause I've never had piano lessons...</content><link rel='alternate' type='text/html' href='https://www.youtube.com/watch?v=G8RhD2RPbQI&amp;feature=youtube_gdata'/><link rel='http://gdata.youtube.com/schemas/2007#video.responses' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/videos/G8RhD2RPbQI/responses'/><link rel='http://gdata.youtube.com/schemas/2007#video.related' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/videos/G8RhD2RPbQI/related'/><link rel='http://gdata.youtube.com/schemas/2007#mobile' type='text/html' href='https://m.youtube.com/details?v=G8RhD2RPbQI'/><link rel='self' type='application/atom+xml' href='https://gdata.youtube.com/feeds/api/users/Didits1/uploads/G8RhD2RPbQI'/><author><name>Didits1</name><uri>https://gdata.youtube.com/feeds/api/users/Didits1</uri></author><gd:comments><gd:feedLink rel='http://gdata.youtube.com/schemas/2007#comments' href='https://gdata.youtube.com/feeds/api/videos/G8RhD2RPbQI/comments' countHint='2'/></gd:comments><yt:location>Lyon, France</yt:location><media:group><media:category label='Muusika' scheme='http://gdata.youtube.com/schemas/2007/categories.cat'>Music</media:category><media:content url='https://www.youtube.com/v/G8RhD2RPbQI?version=3&amp;f=user_uploads&amp;app=youtube_gdata' type='application/x-shockwave-flash' medium='video' isDefault='true' expression='full' duration='44' yt:format='5'/><media:content url='rtsp://v3.cache1.c.youtube.com/CigLENy73wIaHwkCbU9kD2HEGxMYDSANFEgGUgx1c2VyX3VwbG9hZHMM/0/0/0/video.3gp' type='video/3gpp' medium='video' expression='full' duration='44' yt:format='1'/><media:content url='rtsp://v5.cache4.c.youtube.com/CigLENy73wIaHwkCbU9kD2HEGxMYESARFEgGUgx1c2VyX3VwbG9hZHMM/0/0/0/video.3gp' type='video/3gpp' medium='video' expression='full' duration='44' yt:format='6'/><media:description type='plain'>Sorry cause I've never had piano lessons...</media:description><media:keywords>Coldplay, Piano, Postcards, Away, Cover</media:keywords><media:player url='https://www.youtube.com/watch?v=G8RhD2RPbQI&amp;feature=youtube_gdata_player'/><media:thumbnail url='http://i.ytimg.com/vi/G8RhD2RPbQI/0.jpg' height='360' width='480' time='00:00:22'/><media:thumbnail url='http://i.ytimg.com/vi/G8RhD2RPbQI/1.jpg' height='90' width='120' time='00:00:11'/><media:thumbnail url='http://i.ytimg.com/vi/G8RhD2RPbQI/2.jpg' height='90' width='120' time='00:00:22'/><media:thumbnail url='http://i.ytimg.com/vi/G8RhD2RPbQI/3.jpg' height='90' width='120' time='00:00:33'/><media:title type='plain'>Coldplay: Postcards from far away cover</media:title><yt:duration seconds='44'/></media:group><gd:rating average='1.0' max='5' min='1' numRaters='1' rel='http://schemas.google.com/g/2005#overall'/><yt:recorded>2009-09-07</yt:recorded><yt:statistics favoriteCount='1' viewCount='44'/></entry></feed>";
}
