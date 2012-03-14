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
		
		List<String> attributeNames = new ArrayList<String>();
		NamedNodeMap attrs = node.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node a = attrs.item(i);
			m.put(a.getNodeName(), a.getNodeValue());
			attributeNames.add(a.getNodeName());
		}
		
		List<Map<String,Object>> arrayNodes = new ArrayList<Map<String,Object>>();
		NodeList children = node.getChildNodes();
		
		int len = children.getLength();
		Map<String,Integer> tagCounts = new HashMap<String,Integer>();
		StringBuilder builder = new StringBuilder(); 
		
		for (int i = 0; i < len; i++) {
			Node child = children.item(i);
			
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element)child;
				arrayNodes.add(parseObject(elem));
				
				if (!tagCounts.containsKey(elem.getTagName())) {
					Integer count = new Integer(1);
					tagCounts.put(elem.getTagName(), count);
				} else {
					Integer count = tagCounts.get(elem.getTagName());
					tagCounts.put(elem.getTagName(), new Integer(count.intValue()+1));
				}
			} else if (child.getNodeType() == Node.TEXT_NODE) {
				builder.append(child.getNodeValue());
			}
		}
		
		// compression
		
		String text = builder.toString();
		if (text.length() > 0) {
			m.put("textvalue", text);
		}
		
		List<Map<String,Object>> realArrayNodes = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < arrayNodes.size(); i++) {
			Map<String, Object> arrayNode = arrayNodes.get(i);
			String tagName = (String)arrayNode.get("tagName");
			Integer count = tagCounts.get(tagName);
			
			if (count.intValue() == 1 && !attributeNames.contains(tagName)) {
				if (arrayNode.size() == 2 && arrayNode.containsKey("textvalue")) {
					m.put(tagName, arrayNode.get("textvalue"));
				} else if (arrayNode.size() == 1) {
					m.put(tagName, null);
				} else {
					m.put(tagName, arrayNode);
				}
			} else if (count.intValue() > 1 || arrayNode.size() == 2 && !arrayNode.containsKey("textvalue") || arrayNode.size() > 2) {
				realArrayNodes.add(arrayNode);
			} else {
				m.put(tagName, arrayNode.get("textvalue"));
			}
		}
		
		if (realArrayNodes.size() > 0) {
			m.put(node.getTagName(), realArrayNodes);
		}
		
		return m;
	}
}
