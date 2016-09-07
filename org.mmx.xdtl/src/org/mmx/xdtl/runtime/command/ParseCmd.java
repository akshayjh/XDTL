package org.mmx.xdtl.runtime.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

//import org.mmx.xdtl.model.XdtlException;
import org.mmx.xdtl.model.command.Parse;
import org.mmx.xdtl.runtime.Context;
import org.mmx.xdtl.runtime.RuntimeCommand;
import org.mmx.xdtl.runtime.util.TextParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/*
 * Parse reads from given text source, parses source text using given grammar and translates parsed result to List/Map structure
 * in memory
 * 
 * @author Kalle Tomingas
 *
 */
public class ParseCmd implements RuntimeCommand {
	
	private final Logger Logger = LoggerFactory.getLogger(ParseCmd.class);
	private String m_source;
	private String m_rowset;
	private String m_target;
	private String m_grammar;
	public String m_output;
	private Parse.Type m_type;
	public String m_template;
	private int nParserMaxCycleCount;
	
	public ParseCmd(String source, String rowset, String target, String grammar, Parse.Type type, String template) {
		m_source = source;
		m_rowset = rowset;
		m_target = target;
		m_type = type;
		m_grammar = grammar;
		m_template = template;
	}

	@Override
	public void run(Context context) throws Throwable {
		Logger.trace(String.format("Parse: source='%s', rowset='%s' target='%s', grammar='%s', type='%s', template='%s'", m_source, m_rowset, m_target, m_grammar, m_type, m_template));
		
		//String content = new UrlReader().read(m_source);
		String content = m_source;
		
		//Read ParseMap from template attribute
		String parseMap = "";
		if (m_type == Parse.Type.MAP) {
			//parseMap = new UrlReader().read(m_template);
 
			//Read ParseMAP from text file
 		   BufferedReader reader = new BufferedReader(new FileReader(new File(m_template)));
 		    String line = null;
 		    StringBuilder  stringBuilder = new StringBuilder();
 		    String ls = System.getProperty("line.separator");
 		    while( ( line = reader.readLine() ) != null ) {
 		        stringBuilder.append( line );
 		        stringBuilder.append( ls );
 		    }
 		    reader.close();
 		    parseMap = stringBuilder.toString();
		}
		
      
		List<String[]> parsed = new TextParser().Parser(content, m_grammar, m_type, parseMap, nParserMaxCycleCount);		//Parser Call
		String[] statusRow = parsed.get(0);
		String parsedContent = statusRow[2].toString();
		String parsedMessage = statusRow[10].toString();
		String rowCount = statusRow[8].toString();
		String parseCount = statusRow[9].toString();
		
		//Logger.trace(String.format("Parse: target='%s', type='%s',message='%s', result='%s', template='%s'", m_target, m_type, parsedMessage, parsedContent, parseMap));
		Logger.trace(String.format("Parse: target='%s', type='%s',message='%s', result='%s'", m_target, m_type, parsedMessage, parsedContent));
		Logger.debug(String.format("Parse: rows='%s', tokens='%s', message='%s'", rowCount, parseCount, parsedMessage));

		if (parsedContent.length()>0 || parsed.size() > 0) {
			if (m_type == Parse.Type.JSON || m_type == Parse.Type.XML) {
				context.assignVariable(m_target, parsedContent);			//Output Json & Xml to Target
			}
			else {
				context.assignVariable(m_rowset, parsed);					//Assing parsed ArrayList to RowSet
				context.assignVariable(m_target, rowCount);					//Assign rowcount to target parameter 
				}
			}
		else
			Logger.debug(String.format("No Parse result!"));
			//throw new XdtlException(String.format("No Parsed result: '%s'", m_source));
	}
	
	@Inject
    protected void setParserMaxCycleSize(@Named("parser.maxcyclecount") int maxcyclecount) {
        nParserMaxCycleCount = maxcyclecount;
        Logger.debug(String.format("Parser config read: parser.maxcyclecount= '%d'", nParserMaxCycleCount ));
    }
	
}
