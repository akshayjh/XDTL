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
	public Map<String,Object> Decode(Document document) {
		
		Element root = document.getDocumentElement();
		Map<String, Object> m = new HashMap<String,Object>();
		m.put(root.getTagName(), parseElement(root));
		return m;
	}
	
	private static Object parseElement(Element e) {
		Map<String, Object> m = new HashMap<String,Object>();
		
		NamedNodeMap attrs = e.getAttributes();
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				Node a = attrs.item(i);
				m.put(a.getNodeName(), a.getNodeValue());
			}
		}
		
		List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
		m.put("children", children);
		
		NodeList childNodes = e.getChildNodes();
		
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node iter = childNodes.item(j);
			short nodeType = iter.getNodeType();
			
			if (nodeType == Node.ELEMENT_NODE) {
				Element child = (Element)iter;
				Map<String, Object> childm = new HashMap<String,Object>();
				childm.put(child.getTagName(), parseElement(child));
				children.add(childm);
			}
			else if (nodeType == Node.TEXT_NODE) {
				m.put("text", iter.getNodeValue());
			}
		}
				
		return m;
	}
}
