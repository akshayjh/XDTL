package org.mmx.xdtl.runtime.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.services.UriSchemeParser;

import com.google.inject.Inject;

public class UrlStreamFactory {
    private UriSchemeParser m_uriSchemeParser;

    @Inject
    public UrlStreamFactory(UriSchemeParser uriSchemeParser) {
        m_uriSchemeParser = uriSchemeParser;
    }

    public InputStream getInputStream(String sourceUrl) {
        try {
            URLConnection cnn = new URL(sourceUrl).openConnection();
            return cnn.getInputStream();
        } catch (Exception e) {
            throw new XdtlException(e);
        }
    }

    public OutputStream getOutputStream(String targetUrl, boolean overwrite) {
        String scheme = m_uriSchemeParser.getScheme(targetUrl);
        if (scheme.length() == 0) {
            throw new XdtlException("Target URL '" + targetUrl + "' is without scheme");
        }

        return getOutputStream(scheme, targetUrl, overwrite);
    }

    public OutputStream getOutputStream(String scheme, String targetUrl, boolean overwrite) {
        try {
            if (scheme.equals("file")) {
                String filePath;
                filePath = targetUrl.substring(scheme.length() + 1);
                if (filePath.startsWith("//")) filePath = filePath.substring(2);

                File f = new File(filePath);
                if (f.isDirectory()) {
                    throw new XdtlException("'" + targetUrl + "' is a directory");
                }

                boolean append = false;
                if (!overwrite && f.exists()) {
                    append = true;
                }

                return new FileOutputStream(f, append);
            }

            URLConnection cnn = new URL(targetUrl).openConnection();
            cnn.setDoOutput(true);
            cnn.setDoInput(false);
            return cnn.getOutputStream();
        } catch (IOException e) {
            throw new XdtlException(e);
        }
    }
}
