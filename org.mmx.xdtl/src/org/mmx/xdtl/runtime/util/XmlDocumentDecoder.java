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
		
		NamedNodeMap attrs = node.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node a = attrs.item(i);
			m.put(a.getNodeName(), a.getNodeValue());
		}
		
		List<Map<String,Object>> arrayNodes = new ArrayList<Map<String,Object>>();
		NodeList children = node.getChildNodes();
		
		int len = children.getLength();
		for (int i = 0; i < len; i++) {
			Node child = children.item(i);
			
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element)child;
				
				if (isTextValueElement(elem) && !m.containsKey(elem.getTagName())) {
					m.put(elem.getTagName(), elem.getNodeValue());
				} else {
					arrayNodes.add(parseObject(elem));
				}
			}
		}
		
		if (arrayNodes.size() > 0) {
			m.put(node.getTagName(), arrayNodes);
		}
		
		return m;
	}

	private boolean isTextValueElement(Element elem) {
		if (elem.hasAttributes()) return false;
		
		if (elem.hasChildNodes()) {
			NodeList children = elem.getChildNodes();
			int len = children.getLength();
			
			for (int i = 0; i < len; i++) {
				if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
					return false;
				}
			}
		}
		
		return true;
	}
}
