package org.mmx.xdtl.runtime.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream extends FilterInputStream {
    private long m_count;
    
    public CountingInputStream(InputStream in) {
        super(in);
    }

    public long getCount() {
        return m_count;
    }

    @Override
    public int read() throws IOException {
        int result = super.read();
        if (result != -1) {
            m_count++;
        }
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return count(super.read(b, off, len));
    }

    @Override
    public int read(byte[] b) throws IOException {
        return count(super.read(b));
    }
    
    private int count(int readResult) {
        if (readResult != -1) {
            m_count += readResult;
        }
        return readResult;
    }
}
