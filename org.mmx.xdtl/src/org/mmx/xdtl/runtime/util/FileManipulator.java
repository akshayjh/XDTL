package org.mmx.xdtl.runtime.util;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManipulator {
	/**
	 * Deletes files/folders according to given pattern. Supports
	 * both Windows and Unix style paths.
	 * 
	 * @param target Path, for example c:\windows\*.* or /etc/ 
	 */
	public static void delete(String target) throws IOException {
		// empty target, empty actions
		if (target == null || target.length() == 0) {
			return;
		}
		
		WildcardPath path = WildcardPath.parse(target);
		deleteWildcardTarget(new File(path.getBase()), path.getTokens(), 0);
	}
	
	
	/**
	 * Moves files/folders to target file/folder
	 * 
	 * @param source File, folder or wildcard expression
	 * @param target File or folder path. If source is set to wildcard, target can only be folder
	 */
	public static void move(String source, String target, boolean overwrite) throws IOException {
		// nothing to do, exit silently
		if (source == null || source.length() == 0) {
			return;
		}
		
		// target not given, exit silently?
		if (target == null || target.length() == 0) {
			return;
		}
		
		File s = new File(source);
		File t = new File(target);
		boolean hasWildcards = source.indexOf('*') >= 0;
		
		// no directory -> file overwrite
		if (t.exists() && !t.isDirectory() && s.exists() && s.isDirectory()) {
			throw new IOException(
					String.format("Cannot overwrite non-directory '{0}' with directory '{1}", 
					target, source)
			);
		}
		
		// no wildcard expression -> file overwrite
		if (t.exists() && !t.isDirectory() && hasWildcards) {
			throw new IOException(String.format("Target '{0}' is not a directory",target));
		}
		
		// rename source to target
		if (!t.exists() && s.exists()) {
			s.renameTo(t);
			return;
		}
		
		// move file/directory under target
		if (t.exists() && t.isDirectory() && s.exists()) {
			s.renameTo(new File(t, s.getName()));
			return;
		}
		
		// move file with possible overwrite 
		if (t.exists() && t.isFile() && s.isFile()) {
			if (!overwrite) {
				throw new IOException(String.format("Target '{0}' exists", target));
			}
			t.delete();
			s.renameTo(t);
			return;
		}
	}
	
	protected static void deleteWildcardTarget(File base, String[] tokens, int tokenIndex) throws IOException {
		// if something like /existingfile.txt/*.txt is passed, we cannot proceed
		if (base.isFile()) {
			return;
		}
		
		String pattern = tokens[tokenIndex]
				.replace(".", "\\.")
				.replace("?", "\\?")
				.replace("+", "\\+")
				.replace("(", "\\(")
				.replace(")", "\\)")
				.replace("[", "\\[")
				.replace("]", "\\]")
				.replace("-", "\\-")
				.replace("$", "\\$")
				.replace("^", "\\^")
				.replace("*", ".*");
		
		Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		boolean endOfTokenList = tokenIndex == tokens.length - 1;
		
		for (String item : base.list()) {
			Matcher matcher = regex.matcher(item);
			if (!matcher.matches()) continue;
			
			File subfile = new File(base, item);
			
			if (endOfTokenList) {
				deleteSimpleTarget(subfile.getPath());
			} else {
				deleteWildcardTarget(subfile, tokens, tokenIndex + 1);
			}
		}
	}
	
	protected static void deleteSimpleTarget(String target) throws IOException {
		File file = new File(target);

		// if there is no target file to delete, exit silently
		if (!file.exists()) {
			return;
		}
		
		if (file.isDirectory()) {
			String[] children = file.list();
			for (String child : children) {
				deleteSimpleTarget(new File(file, child).getPath());
			}
		}
		
		file.delete();
	}
}
