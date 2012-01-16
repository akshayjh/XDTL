package org.mmx.xdtl.runtime.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;

import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Decode;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.util.JsonDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decode reads from given hierarchical source (json, xml) and translates it to List/Map structure
 * in memory
 * 
 * @author urmo
 *
 */
public class DecodeCmd implements RuntimeCommand {
	
	private final Logger Logger = LoggerFactory.getLogger(DecodeCmd.class);
	private String m_source;
	private String m_target;
	private Decode.Type m_type;
	
	public DecodeCmd(String source, String target, Decode.Type type) {
		m_source = source;
		m_target = target;
		m_type = type;
	}

	@Override
	public void run(Context context) throws Throwable {
		Logger.info(String.format("decode: source='%s', target='%s', type='%s'",
				m_source, m_target, m_type));
		
		String content = readSource();
		
		if (m_type == Decode.Type.JSON)
			parseJson(content, context);
		else if (m_type == Decode.Type.XML)
			parseXml(content, context);
		else
			throw new XdtlException(String.format("Unknown Decode type: '%s'", m_type));
	}
	
	private void parseJson(String content, Context context) {
		List<Object> parsed = new JsonDecoder().Decode(content);
		context.assignVariable(m_target, parsed);
	}

	private void parseXml(String content, Context context) {
		// TODO Auto-generated method stub
	}

	private String readSource() throws Exception {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(openSourceForInput())); 
		
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
		
		return builder.toString();
	}
	
    private InputStream openSourceForInput() throws Exception {
        URI uri = new URI(m_source);

        if ("file:" != uri.getScheme()) {
            if (uri.isOpaque()) {
                URI curdir = new File(".").toURI();
                uri = curdir.resolve(uri.getRawSchemeSpecificPart());
                Logger.debug("resolved uri=" + uri);
            }
            
            File f = new File(uri);
            return new FileInputStream(f);
        }
        
        URLConnection cnn = uri.toURL().openConnection();
        cnn.setDoOutput(false);
        cnn.setDoInput(true);
        return cnn.getInputStream();
    }
}
