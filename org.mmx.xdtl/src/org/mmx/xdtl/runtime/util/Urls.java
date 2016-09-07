package org.mmx.xdtl.runtime.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.mmx.xdtl.model.XdtlException;

public class Urls {
    private static final URL DEFAULT_BASE_URL;

    static {
        try {
            DEFAULT_BASE_URL = new URL("file:");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes ref part from URL if present.
     *
     * @param urlSpec The URL.
     * @return An URL without ref part.
     */
    public static String removeRef(String urlSpec) {
        URL url = toURL(urlSpec);
        String ref = url.getRef();

        if (ref == null) {
            return urlSpec;
        }

        return urlSpec.substring(0, urlSpec.length() - ref.length() - 1);
    }

    public static URL toURL(String urlSpec) {
        try {
            return new URL(DEFAULT_BASE_URL, urlSpec);
        } catch (MalformedURLException e) {
            throw new XdtlException(e);
        }
    }
}
