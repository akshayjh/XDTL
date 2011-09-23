package org.mmx.xdtl.runtime.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class PathList {
    private static ArrayList<File> m_directories = new ArrayList<File>();
    private static final String PATH_SEPARATOR = ";";
    
    public PathList(String basePath, String paths) {
        if (paths == null || paths.length() == 0) return;

        String[] pathArr = paths.split(PATH_SEPARATOR);
        for (int i = 0; i < pathArr.length; i++) {
            m_directories.add(new File(basePath, pathArr[i]));
        }
    }
    
    public void forEachFile(FilenameFilter filter, ForEachCallback callback) {
        for (File directory: m_directories) {
            if (!directory.exists() || !directory.isDirectory()) continue;
            File[] files = directory.listFiles(filter);
            Arrays.sort(files);
            
            for (int i = 0; i < files.length; i++) {
                callback.execute(files[i]);
            }
        }
    }
    
    public interface ForEachCallback {
        void execute(File file);
    }
}
