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
    private long m_bytesRead;
    
	public String read(String uri) throws IOException {
	    m_bytesRead = 0L;

		if (uri == null || uri.length() == 0)
			return null;
		
		Pattern pattern = Pattern.compile("(http|https)://(\\S+):(\\S+)@([\\S&&[^:]]+)(:(\\d+))?");
		Matcher matcher = pattern.matcher(uri);

		URL url = null;
		URLConnection connection = null;
		
		if (matcher.matches()) {
			String username = matcher.group(2);
			String password = matcher.group(3);
			
			url = new URL(matcher.group(1) + "://" + matcher.group(4));
			connection = url.openConnection();
			
			if (username != null && username.length() > 0) {
				String authString = username + ":" + password;
				String authHeader = new String(Base64.encode(authString.getBytes()));
								
				connection.setRequestProperty("Authorization", "Basic " + authHeader);
			}
		} else {
			url = new URL(uri);
			connection = url.openConnection();
		}
		
		
		StringBuilder builder = new StringBuilder();
		CountingInputStream is = new CountingInputStream(connection.getInputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
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
	
		m_bytesRead = is.getCount();
		return builder.toString();
	}
	
	public long getBytesRead() {
	    return m_bytesRead;
	}
}
