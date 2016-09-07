package org.mmx.xdtl.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

public class CsvSource implements Source {

    /**
     * A reader which skips empty lines.
     *
     * @author vsi
     */
    private static class MyReader extends Reader {
        private BufferedReader m_reader;
        private StringBuilder m_lineBuf;
        private int m_charsAvailable;

        public MyReader(BufferedReader reader) {
            m_reader = reader;
            m_lineBuf = new StringBuilder();
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            if (m_charsAvailable == -1) {
                return -1;
            }

            int result = 0;

            while (len > 0) {
                if (m_charsAvailable == 0) {
                    loadLine();
                }

                if (m_charsAvailable == -1) {
                    return result == 0 ? -1 : result;
                }

                int srcBegin = m_lineBuf.length() - m_charsAvailable;
                int numChars = (len > m_charsAvailable) ? m_charsAvailable : len;

                m_lineBuf.getChars(srcBegin, srcBegin + numChars, cbuf, off);

                m_charsAvailable -= numChars;
                len -= numChars;
                off += numChars;
                result += numChars;
            }

            return result;
        }

        @Override
        public void close() throws IOException {
            m_reader.close();
        }

        @Override
        public int read() throws IOException {
            if (m_charsAvailable == 0) {
                loadLine();
            }

            if (m_charsAvailable == -1) {
                return -1;
            }

            return m_lineBuf.charAt(m_lineBuf.length() - m_charsAvailable--);
        }

        private void loadLine() throws IOException {
            do {
                m_lineBuf.setLength(0);
                String line = m_reader.readLine();
                if (line != null) {
                    m_lineBuf.append(line).append('\n');
                    m_charsAvailable = m_lineBuf.length();
                } else {
                    m_charsAvailable = -1;
                }
            } while (m_charsAvailable == 1);
        }

        @Override
        public boolean ready() throws IOException {
            return (m_charsAvailable > 0) || m_reader.ready();
        }
    }

    private CSVReader m_csvReader;
    private List<Column> m_columns;

    public CsvSource(InputStream stream, String encoding, char delimiter, char quote,
            boolean header, int skip, char escape) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));

        CSVParserBuilder csvParserBuilder = new CSVParserBuilder()
                .withSeparator(delimiter)
                .withQuoteChar(quote)
                .withEscapeChar(escape)
                .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH);

        CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(new MyReader(reader))
                .withCSVParser(csvParserBuilder.build());

        m_csvReader = csvReaderBuilder.build();
        if (header) {
            loadColumns();
        }
        skipLines(reader, skip);
    }

    private void loadColumns() throws IOException {
        String[] data;
        data = m_csvReader.readNext();
        if (data != null) {
            m_columns = new ArrayList<Column>(data.length);
            for (int i = 0; i < data.length; i++) {
                m_columns.add(new Column(data[i], Types.VARCHAR, "VARCHAR"));
            }
        }
    }

    private void skipLines(Reader reader, int lineCount) throws IOException {
        for (int i = 0; i < lineCount; i++) {
            if (m_csvReader.readNext() == null) {
                throw new IOException("Unexpected end of file");
            }
        }
    }

    @Override
    public void close() throws Exception {
        m_csvReader.close();
    }

    @Override
    public void fetchRows(RowHandler rowHandler) throws Exception {
        String[] data;
        while ((data = m_csvReader.readNext()) != null) {
            rowHandler.handleRow(data, null);
        }
    }

    @Override
    public List<Column> getColumns() throws Exception {
        return m_columns;
    }
}
