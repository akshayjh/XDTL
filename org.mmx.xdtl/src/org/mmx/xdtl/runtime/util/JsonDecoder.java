package org.mmx.xdtl.runtime.util;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class JsonDecoder {
	
	public List<Object> Decode(String jsonText) {
		
		List<Object> result = new ArrayList<Object>();
		JsonElement root = new JsonParser().parse(jsonText);
		
		Object o = parseElement(root);
		
		if (o instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>)o;
			for (int i = 0; i < list.size(); i++) {
				result.add(list.get(i));
			}
		}
		else {
			result.add(o);
		}
		
		return result;
	}
	
	private Object parseElement(JsonElement e) {
		if (e.isJsonObject()) 
			return parseObject(e.getAsJsonObject());
			
		if (e.isJsonArray()) 
			return parseArray(e.getAsJsonArray());
			
		if (e.isJsonNull())
			return null;
			
		return e.getAsJsonPrimitive().toString();
	}
	
	private Map<String,Object> parseObject(JsonObject o) {
		Set<Map.Entry<String,JsonElement>> entry = o.entrySet();
				
		Map<String, Object> map = new HashMap<String,Object>();
		
		for (Map.Entry<String,JsonElement> item : entry) {
			map.put(item.getKey(), parseElement(item.getValue()));
		}
		
		return map;
	}
	
	private List<Object> parseArray(JsonArray a) {
		List<Object> list = new ArrayList<Object>();
		int size = a.size();
		
		for (int i = 0; i < size; i++) {
			list.add(parseElement(a.get(i)));
		}
		
		return list;
	}	
}
