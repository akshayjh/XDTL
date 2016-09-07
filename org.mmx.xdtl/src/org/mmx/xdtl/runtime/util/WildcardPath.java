package org.mmx.xdtl.runtime.util;

import java.io.File;

public class WildcardPath {
	private String base;
	private String[] tokens;

	public WildcardPath(String base, String[] tokens) {
		this.base = base;
		this.tokens = tokens;
	}
	
	public String getBase() {
		return base;
	}
	
	public String[] getTokens() {
		return tokens;
	}
	
	public static WildcardPath parse(String path) {
		if (path == null || path.length() == 0) {
			return new WildcardPath("", new String[0]);
		}
		
		int firstWildcard = path.indexOf("*");
		
		if (firstWildcard == -1) {
			File file = new File(path);
			return new WildcardPath(file.getParent(), new String[] { file.getName() });
		}
		
		int lastBaseEnd = firstWildcard;
		while (lastBaseEnd > 0 && path.charAt(lastBaseEnd) != '\\' && path.charAt(lastBaseEnd) != '/') {
			lastBaseEnd--;
		}
	
		String pathBase = "";
		String[] tokens = null;
		
		if (lastBaseEnd == 0 && (path.charAt(0) == '\\' || path.charAt(0) == '/') || lastBaseEnd > 0) {
			pathBase = path.substring(0, lastBaseEnd);
			tokens = path.substring(lastBaseEnd+1).split("\\\\|\\/");
		} else {
			tokens = path.split("\\\\|\\/");
		}
		return new WildcardPath(pathBase, tokens);
	}
}
