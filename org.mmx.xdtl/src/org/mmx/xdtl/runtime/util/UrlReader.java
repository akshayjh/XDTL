package org.mmx.xdtl.runtime.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlbeans.impl.util.Base64;

public class UrlReader {
	
	public String read(String uri) throws IOException {
		if (uri == null || uri.length() == 0)
			return null;
		
		Pattern pattern = Pattern.compile("(http|https)://(\\S+):(\\S+)@([\\S&&[^:]]+)(:(\\d+))?");
		Matcher matcher = pattern.matcher(uri);
		
		URL url = new URL(uri);
		URLConnection connection = url.openConnection();
		
		if (matcher.matches()) {
			String username = matcher.group(2);
			String password = matcher.group(3);
			
			if (username != null && username.length() > 0) {
				String authString = username + ":" + password;
				String authHeader = new String(Base64.encode(authString.getBytes()));
								
				connection.setRequestProperty("Authorization", "Basic " + authHeader);
			}
		}
		
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
		
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
