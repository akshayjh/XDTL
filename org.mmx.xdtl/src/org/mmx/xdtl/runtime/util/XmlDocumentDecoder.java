package org.mmx.xdtl.runtime.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlDocumentDecoder {
	public Map<String,Object> decode(Document document) {
		
		Element root = document.getDocumentElement();
		return parseObject(root);
	}

	private Map<String, Object> parseObject(Element node) {
		
		Map<String, Object> m = new HashMap<String,Object>();
		m.put("tagName", node.getTagName());
		
		// map attributes to properties
		NamedNodeMap attrs = node.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node a = attrs.item(i);
			m.put(a.getNodeName(), a.getNodeValue());
		}
		
		NodeList children = node.getChildNodes();
		int len = children.getLength();
		
		Map<String, List<Object>> tagmap = new HashMap<String, List<Object>>();
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < len; i++) {
			Node child = children.item(i);
			
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				addToTagmap(tagmap, (Element)child);
			} else if (child.getNodeType() == Node.TEXT_NODE) {
				builder.append(child.getNodeValue());
			}
		}
		
		String textvalue = builder.toString().trim();
		if (textvalue.length() > 0) {
			m.put("textvalue", textvalue);
		}
	
		return m;
	}

	private void addToTagmap(Map<String, List<Object>> tagmap, Element elem) {
		String tag = elem.getTagName();
		
		if (!tagmap.containsKey(tag)) {
			tagmap.put(tag, new ArrayList<Object>());
		}
		
		if (isObject(elem)) {
			tagmap.get(tag).add(parseObject(elem));
		} else {
			tagmap.get(tag).add(getText(elem));
		}
	}

	private boolean isObject(Element elem) {
		
		if (elem.hasAttributes()) return true;
		
		NodeList children = elem.getChildNodes();
		int len = children.getLength();
		
		for (int i = 0; i < len; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				return true;
			}
		}
		return false;
	}
	
	private String getText(Element elem) {
		
		NodeList children = elem.getChildNodes();
		int len = children.getLength();
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < len; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				builder.append(child.getNodeValue());
			}
		}
		return builder.toString();
	}
}
