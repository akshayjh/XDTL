package org.mmx.xdtl.services;

import junit.framework.Assert;

import org.junit.Test;

public class UriSchemeParserTest {

    @Test
    public void testGetScheme() {
        UriSchemeParser parser = new UriSchemeParser();
        Assert.assertEquals("file", parser.getScheme("file:query.xdtl"));
        Assert.assertEquals("", parser.getScheme("query.xdtl"));
        Assert.assertEquals("scheme", parser.getScheme("scheme:"));
        Assert.assertEquals("", parser.getScheme(""));
        Assert.assertEquals("", parser.getScheme(null));
        Assert.assertEquals("", parser.getScheme(":"));
        Assert.assertEquals("a", parser.getScheme("a:"));
    }
}
