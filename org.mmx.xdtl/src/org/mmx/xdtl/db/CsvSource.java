package org.mmx.xdtl.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import au.com.bytecode.opencsv.CSVReader;

public class CsvSource implements Source {
    private CSVReader m_csvReader;
    
    public CsvSource(InputStream stream, String encoding, char delimiter, char quote,
            boolean header, int skip) throws Exception {
        Reader reader = new BufferedReader(new InputStreamReader(stream, encoding));
        if (header) ++skip;
        skipLines(reader, skip);
        m_csvReader = new CSVReader(reader, delimiter, quote);        
    }
    
    private void skipLines(Reader reader, int lineCount) throws IOException {
        for (int i = 0; i < lineCount; i++) {
            skipLine(reader);
        }
    }

    private void skipLine(Reader reader) throws IOException {
        int c;
        
        do {
            c = reader.read();
            if (c == -1) throw new IOException("Unexpected end of file");
        } while (c != 0x0a);
    }

    @Override
    public String[] readNext() throws IOException {
        return m_csvReader.readNext();
    }

    @Override
    public void close() throws Exception {
        m_csvReader.close();
    }
}
