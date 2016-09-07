package org.mmx.xdtl.runtime.util;

import java.util.ArrayList;
import java.util.List;
import org.mmx.xdtl.model.command.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import goldengine.java.*;

/*
 * TextParser reads from given text source, parses source text using given grammar and translates parsed result to List/Map structure in memory
 * @author Kalle Tomingas
 */

public class TextParser implements GPMessageConstants
{
	private static int nTokenCounter = 0;
	private static int nTokenMapCounter = 0;
//	private static int nTokenStopCounter = 0;
	private static int nTokenGroupCounter = 0;
	private int nParserMaxCycleDefaultCount = 5000;
//	private int nParserMaxCycleCount;
    private static String txtTokenList = "";
    private static String txtTaggedTokenList = "";
    private static JsonArray StopWordArray;
    private static JsonArray MapWordArray;
    private static JsonArray TagWordArray;
    private static String lastMapType = "";
    private static String lastStopToken = "";
    private static String curStopToken = "";
    private static String curStopRule = "";
    private static String lastParentRule = "";
    private static String groupLastMapType = "";

    //GOLDParser parser = new GOLDParser();
    private static List<String[]> resultSet ;
    
	private final static Logger Logger = LoggerFactory.getLogger(TextParser.class);

	public List<String[]> Parser(String sourceText, String compiledGrammar, Parse.Type outputFormat, String parseTemplate, int nParserMaxCycleCount) throws ParserException {

        nTokenCounter = 0;
        nTokenMapCounter = 0;
//		nTokenStopCounter = 0;
		nTokenGroupCounter = 0;
	    txtTokenList = "";
	    txtTaggedTokenList = "";
	    StopWordArray = null;
	    MapWordArray = null;
	    TagWordArray = null;
	    lastMapType = "";
	    lastStopToken = "";
	    curStopToken = "";
	    lastParentRule = "";
		Integer ParseCounter = 0;
		Integer maxParseCount = nParserMaxCycleDefaultCount;  //Default Max Parse Cycle Count
        String txtParseResult = "";
        String resultMessage  = "";
        String msgType ="";
        resultSet = new ArrayList<String[]>();
        
        //Adding extra line feed to text for parser support
        sourceText = sourceText + "\r\n";
        //Initialize Parser Engine	
        GOLDParser parser = new GOLDParser();

        Logger.debug(String.format("Parser max cycle default value: '%d'", nParserMaxCycleDefaultCount));
		//Set Max Parse Cycle Count from xdtlrt.xml config property
		if (nParserMaxCycleCount > 0) {
			maxParseCount = nParserMaxCycleCount;
			Logger.debug(String.format("Parser max cycle value set to config value: parser.maxcyclecount='%d'", nParserMaxCycleCount));
		}
		
        try
        {
            if (outputFormat == Parse.Type.MAP && parseTemplate.length() > 0 ) {
    	        //Decoding Json ParseMap
    	        JsonObject MyParseMap = (JsonObject) new JsonParser().parse(parseTemplate).getAsJsonObject().get("parse-map"); 
    	        //If then Checks here!!!
    	        StopWordArray = MyParseMap.getAsJsonArray("stopwords");
    	        MapWordArray = MyParseMap.getAsJsonArray("mapwords");
    	        TagWordArray = MyParseMap.getAsJsonArray("tagwords");
            }
            parser.clear();
            parser.loadCompiledGrammar(compiledGrammar);
            parser.openText(sourceText);
        	parser.setTrimReductions(true);
        }
        catch(ParserException parse)
        {
        	if (outputFormat == Parse.Type.MAP) {
        		Logger.error(String.format("TextParserTemplateError: '%s'", parseTemplate.toString()));
        	}
        	Logger.error(String.format("TextParserError: '%s'", parse.toString()));
            System.exit(1);
        }

        boolean done = false;
        int response = -1;
        
        while(!done)
        {
        	ParseCounter+=1;

            try
            {
            	response = parser.parse();
            }
            
             catch(ParserException parse)
            {	
            	Logger.error(String.format("TextParserError: '%s'", parse.toString()));
                System.exit(1);
            }
             
            if (ParseCounter > maxParseCount)
             {
            	Logger.debug(String.format("TextParserError: '%s'", "ParseCounter over max limit"+(ParseCounter-1)));
            	done = true;
            	resultMessage  = "gpMsgParseCounterOverflow";
            	break;
             }

            switch(response)
            {
                case gpMsgTokenRead:
                    Logger.trace(String.format("TextParser: '%s'", "gpMsgTokenRead: Name:" + parser.currentToken().toString() + "; Data:"+ parser.currentToken().getData().toString()));
                    resultMessage  = "gpMsgTokenRead";
                    break;
                case gpMsgReduction:
                    break;
                case gpMsgAccept:
                    /* The program was accepted by the parsing engine */
                    resultMessage  = "gpMsgAccept";
                	Logger.debug(String.format("TextParser: '%s'", "gpMsgAccept"));
                    done = true;
                    if (outputFormat == Parse.Type.XML) {
                    	txtParseResult = XmlParseTree(parser.currentReduction(), 0, 0,"","");
                    }
                    else if (outputFormat == Parse.Type.JSON) {
                    	txtParseResult = JsonParseTree(parser.currentReduction(), 0, 0,"","");
                    }
                    else if (outputFormat == Parse.Type.MAP) {
                    	ParseCustomMap(parser.currentReduction(), 0, -1, 0,"");
                    }
                    else {
                    	ParseMap(parser.currentReduction(), 0, 0,"");
                    }
                    break;
                case gpMsgLexicalError:
                    resultMessage  = "gpLexicalError";
                	Logger.debug(String.format("TextParser: '%s'", "gpLexicalError: Line:" + parser.currentLineNumber()));
                    parser.popInputToken();
                    break;                    
                case gpMsgNotLoadedError:
                	resultMessage  = "gpMsgNotLoadedError";
                	Logger.debug(String.format("TextParser: '%s'", "gpMsgNotLoadedError"));
                    done = true;
                    break;
                case gpMsgSyntaxError:
                	resultMessage  = "gpMsgSyntaxError";
                    Logger.debug(String.format("TextParser: '%s'", "gpMsgSyntaxError: Token not expected: "+ (String)parser.currentToken().getData()));
                    //done = true;    //Do not continue with syntax error
                    parser.popInputToken();
                    break;
                case gpMsgCommentError:
                	resultMessage  = "gpMsgCommentError";
                	Logger.debug(String.format("TextParser: '%s'", "gpMsgCommentError"));
                    done = true;
                	//parser.popInputToken();
					break;
                case gpMsgInternalError:
                	resultMessage  = "gpMsgInternalError";
                	Logger.debug(String.format("TextParser: '%s'", "gpMsgInternalError"));
                    done = true;
                    break;
            }
        }
        
        
        //Adding output message to rowset
        if (outputFormat == Parse.Type.JSON || outputFormat == Parse.Type.XML) { 
        	//Output for Message: id, groupId, token, type, pathDepth, tokenDepth, order, path, tokenRule, tokenRuleData, tokenData, message
            String[] row={"0","0",txtParseResult,outputFormat.toString(),"","","","","1",String.valueOf(nTokenCounter),resultMessage};
        	resultSet.add(0,row) ;
        }
        else {
	        if (resultMessage.equals("gpMsgAccept")){
	        	msgType = "Ok";
	        }
	        else {
	        	msgType = "Error";
	        }
        	//Output for Message: id, groupId, token, type, pathDepth, tokenDepth, order, path, tokenRule, tokenRuleData, tokenData, message
        	String[] row={"0","0","",msgType,"","","","",String.valueOf(nTokenMapCounter),String.valueOf(nTokenCounter),resultMessage};
            resultSet.add(0,row);
        }
        
        parser.clear();
        parser.closeFile();
    	return resultSet;
	}	

    static void ParseCustomMap(goldengine.java.Reduction TheReduction, int nGroupCounter, int nPathCounter, int nDepthCounter, String txtRulePath)
    {
   	String txtRuleName = "";
   	String txtToken = "";
   	String txtTokenName = "";
   	String txtTokenData = "";
   	String txtTokenRule = "";
   	Boolean firstIteration = false;
   	Boolean stopListMatch = false;
   	Boolean mapListMatch = false;
   	Boolean mapGroupMatch = false;
   	Boolean tagListMatch = false;
   	String mapGroup = ""; 
   	String mapType = ""; 
   	String mapToken = ""; 
   	String mapRule = "";
   	String tagToken = "";
   	String tagRule = "";
   	String localLastMapType = "";
   	
   	//Remember the first iteration
    if (nPathCounter == -1) {
    	firstIteration = true;
    	nPathCounter = 0;
    }
    
       // Do not write rules with empty items
       if (TheReduction.getTokenCount() > 0) {
       	
          nPathCounter+=1;
          txtRuleName = TheReduction.getParentRule().name();
          txtRulePath = txtRulePath + "/" + txtRuleName; 
         
          //'=== Display the children of the reduction (Token)
          for (int i = 0; i < TheReduction.getTokenCount(); i++) {

              switch(TheReduction.getToken(i).getKind())
              {
                  case goldengine.java.SymbolTypeConstants.symbolTypeNonterminal:
	                //Remember Current Rule
               	    lastParentRule = TheReduction.getToken(i).getText().toString();
               	    //Next Recursion
	                ParseCustomMap((goldengine.java.Reduction)TheReduction.getToken(i).getData(), nGroupCounter, nPathCounter, nDepthCounter, txtRulePath);
	                break;
                  default:
               	    nDepthCounter+=1;
               	    nTokenCounter +=1;
               	    
	                    txtTokenName = TheReduction.getToken(i).getName();
	                    txtTokenRule = TheReduction.getParentRule().getText().substring(0, TheReduction.getParentRule().getText().lastIndexOf("::=")-1 ).trim();
	                    //txtTokenRule = TheReduction.getParentRule().getText().substring( TheReduction.getParentRule().getText().lastIndexOf("::=")+3, TheReduction.getParentRule().getText().length() ).trim();
	                    txtTokenData = TheReduction.getParentRule().getText();
	                    txtToken = TheReduction.getToken(i).getData().toString();

                       //Lookup tokens & rules from StopWord list 
	                    for (JsonElement StopWords : StopWordArray) {
	                        String stopToken = StopWords.getAsJsonObject().get("token").getAsString();
	                        String stopRule = StopWords.getAsJsonObject().get("rule").getAsString();
	                        if (txtTokenName.equals(stopToken) && (txtTokenRule.equals(stopRule) || lastParentRule.equals(stopRule)) ) {
	                        	stopListMatch = true;
		                       	curStopToken = stopToken;
		                       	curStopRule = stopRule;
		                       	break;
	                        }
	                    }

	                    
	                    //Lookup over TagWord list 
	                    for (JsonElement TagWords : TagWordArray) {
	                    	if (TagWords.getAsJsonObject().has("token")) {
	                    		tagToken = TagWords.getAsJsonObject().get("token").getAsString();
	                    	}
	                    	if (TagWords.getAsJsonObject().has("rule")) {
	                    		tagRule = TagWords.getAsJsonObject().get("rule").getAsString();
	                    	}
	                        if (txtTokenName.equals(tagToken) || txtTokenRule.equals(tagRule)) {
	                        	tagListMatch = true;
	                        	break;
	                        }
		                }
	                    
                       if (stopListMatch) {
//                   	    nTokenStopCounter +=1;
		                    //Lookup over MapWord list 
		                    for (JsonElement MapWords : MapWordArray) {
		                    	mapGroup = MapWords.getAsJsonObject().get("group").getAsString();
		                        mapType = MapWords.getAsJsonObject().get("map").getAsString();
		                        mapToken = MapWords.getAsJsonObject().get("token").getAsString();
		                        mapRule = MapWords.getAsJsonObject().get("rule").getAsString();
		                        if (txtTokenName.equals(mapToken) && ( txtTokenRule.equals(mapRule) || lastParentRule.equals(mapRule)) ) {
		                        	mapListMatch = true;
		                        	mapGroupMatch = false;
		                        	if ( mapGroup.equals("1")) {
			                        	groupLastMapType = lastMapType;
			                        	localLastMapType = lastMapType;
		                        		mapGroupMatch = true;
			                        	nGroupCounter +=1;
			                        }
		                        	//Logger.trace(String.format("TextParser: " + nTokenCounter + "," + nGroupCounter + "," + nTokenGroupCounter + " STOP: | gLastMapType: " + groupLastMapType + " | LastMapType: " + lastMapType + " | LastStopToken: " + lastStopToken + " | CurStopToken: " + curStopToken + " | Token: " + txtToken + " | TokenType: " + txtTokenName + " | Rule: " + txtTokenRule + " | ParentRule: " + lastParentRule + " | TokenList: "+ txtTokenList));	                    
		                        	break;
		                        }
		                    }
		                    
	                        if (mapListMatch) {

	                           	//Initial empty list special case 
	                    	    if (lastStopToken.length() == 0 || txtTokenList.length() == 0) {
	                    	    	lastMapType = mapType;
	                    	    	lastStopToken = curStopToken;
			                       	lastParentRule = curStopRule;
	    	                    }
              	      
	                            if (txtTokenList.trim().length() > 0 ) {
		                    	    nTokenMapCounter +=1;

		    	                    //Add new token row to global Array
		    	                	String[] row = {String.valueOf(nTokenMapCounter),String.valueOf(nGroupCounter),txtTokenList.trim(),lastMapType,String.valueOf(nPathCounter),String.valueOf(nDepthCounter),
		    	                					String.valueOf(i),txtRulePath,txtTokenRule,txtTokenData,txtTaggedTokenList.trim()};
		    	                	resultSet.add(row) ;
		    	                	
		    	                    //If MapGroup exists
		    	                    if(mapGroupMatch) {		    	                    	
			                        	nGroupCounter +=1;
//			                        	groupLastMapType = lastMapType;
		    	                    }

		    	                    //Logger.trace(String.format("TextParser: " + nTokenCounter + "," + nGroupCounter + "," + nTokenGroupCounter + " SAVE: | gLastMapType: " + groupLastMapType + " | LastMapType: " + lastMapType + " | LastStopToken: " + lastStopToken + " | CurStopToken: " + curStopToken + " | Token: " + txtToken + " | TokenType: " + txtTokenName + " | Rule: " + txtTokenRule + " | ParentRule: " + lastParentRule + " | TokenList: "+ txtTokenList));	                    
		    	               		txtTokenList = "";
		    	               		txtTaggedTokenList = "";
		    	               		mapGroupMatch = false;
		    	               		lastMapType = mapType;
			                       	lastStopToken = curStopToken;
                  	    
	                            } //(txtTokenList.trim().length() > 0 )
                           
	                        } //if (mapListMatch)
	                        
	                        //Flush collection when token in StopList
    	               		txtTokenList = "";
    	               		txtTaggedTokenList = "";
    	               		
                        } //if (stopListMatch)
                       
                       else {
                       	//Collect non-stop tokens
                       	txtTokenList = txtTokenList + " " + txtToken; 
                       	if (tagListMatch) {
                       		//txtTaggedTokenList = txtTaggedTokenList + " " + "<"+ tagToken + ">" + txtToken + "</"+ tagToken + ">";
                    		txtTaggedTokenList = txtTaggedTokenList + " " + "<"+ tagToken + ":" + txtTokenRule.replace(">","").replace("<","") + ">"  + txtToken + "</"+ tagToken + ":" + txtTokenRule.replace(">","").replace("<","") + ">";

                       	}
                       	else {
                       		txtTaggedTokenList = txtTaggedTokenList + " " + txtToken;
                       	}
                       	
   	                    //Lookup over MapWord list without StopWord match to save single token during string build
//   	                    for (JsonElement MapWords : MapWordArray) {
//   	                    	mapGroup = MapWords.getAsJsonObject().get("group").getAsString();
//   	                        mapType = MapWords.getAsJsonObject().get("map").getAsString();
//   	                        mapToken = MapWords.getAsJsonObject().get("token").getAsString();
//   	                        mapRule = MapWords.getAsJsonObject().get("rule").getAsString();
//   	                        if (!stopListMatch && txtTokenName.equals(mapToken) && ( txtTokenRule.equals(mapRule) || lastParentRule.equals(mapRule)) ) {
//   	                    	    nTokenMapCounter +=1;
//				                    //Add new token row to global Array
//				                	String[] row = {String.valueOf(nTokenMapCounter),String.valueOf(nTokenGroupCounter),txtToken.trim(),mapType,String.valueOf(nPathCounter),String.valueOf(nDepthCounter),
//				                					String.valueOf(i),txtRulePath,txtTokenRule,txtTokenData,""};
//				                	resultSet.add(row) ;
//   	                        	//break;
//   	                        }
//   	                    }
                       	
                       } //ifelse (stopListMatch)
                       
                   mapListMatch = false;
                   stopListMatch = false;
              
              } //switch()
  
              //Save GroupChange Iteration (inside same cycle)**
              if (nGroupCounter < nTokenGroupCounter) {
                  
                  if (txtTokenList.trim().length() > 0) {
              	    nTokenMapCounter +=1;
              	    
                    //Add new token row to global Array
                	String[] row = {String.valueOf(nTokenMapCounter),String.valueOf(nTokenGroupCounter),txtTokenList.trim(),groupLastMapType,String.valueOf(nPathCounter),String.valueOf(nDepthCounter),
                					"0",txtRulePath,txtTokenRule,txtTokenData,txtTaggedTokenList.trim()};
                	resultSet.add(row) ;
                	
                	//Logger.trace(String.format("TextParser: " + nTokenCounter + "," + nGroupCounter + "," + nTokenGroupCounter + " SAVE GROUP**: | gLastMapType: " + groupLastMapType + " | LastMapType: " + lastMapType + " | LastStopToken: " + lastStopToken + " | CurStopToken: " + curStopToken + " | Token: " + txtToken + " | TokenType: " + txtTokenName + " | Rule: " + txtTokenRule + " | ParentRule: " + lastParentRule + " | TokenList: "+ txtTokenList));	                    
                	
            		lastMapType = groupLastMapType;
                 	lastStopToken = curStopToken;
                 	nTokenGroupCounter = nGroupCounter;
                 	txtTokenList="";
                 	txtTaggedTokenList = "";
                  }
              } 
              
          } //for ()

          //Save GroupChange Iteration*
          if (nGroupCounter < nTokenGroupCounter) {
              
              if (txtTokenList.trim().length() > 0) {
          	    nTokenMapCounter +=1;
          	    
                //Add new token row to global Array
            	String[] row = {String.valueOf(nTokenMapCounter),String.valueOf(nTokenGroupCounter),txtTokenList.trim(),groupLastMapType,String.valueOf(nPathCounter),String.valueOf(nDepthCounter),
            					"0",txtRulePath,txtTokenRule,txtTokenData,txtTaggedTokenList.trim()};
            	resultSet.add(row) ;
            	
            	//Logger.trace(String.format("TextParser: " + nTokenCounter + "," + nGroupCounter + "," + nTokenGroupCounter + " SAVE GROUP: | gLastMapType: " + groupLastMapType + " | LastMapType: " + lastMapType + " | LastStopToken: " + lastStopToken + " | CurStopToken: " + curStopToken + " | Token: " + txtToken + " | TokenType: " + txtTokenName + " | Rule: " + txtTokenRule + " | ParentRule: " + lastParentRule + " | TokenList: "+ txtTokenList));	                    

            	
        		lastMapType = groupLastMapType;
             	lastStopToken = curStopToken;
             	nTokenGroupCounter = nGroupCounter;
             	txtTokenList="";
             	txtTaggedTokenList = "";
              }
          } 

          //Pass the current group nbr to global variable (previous iteration)
          nTokenGroupCounter = nGroupCounter; 
          groupLastMapType = localLastMapType;
      
	        //Save last Iteration
	        if (firstIteration == true ) {

	            if (txtTokenList.trim().length() > 0) {
	        	    nTokenMapCounter +=1;
	                
	        	    //Add new token row to global Array
	            	String[] row = {String.valueOf(nTokenMapCounter),String.valueOf(nTokenGroupCounter),txtTokenList.trim(),lastMapType,String.valueOf(nPathCounter),String.valueOf(nDepthCounter),
	            					"0",txtRulePath,txtTokenRule,txtTokenData,txtTaggedTokenList.trim()};
	            	resultSet.add(row) ;
	            	//Logger.trace(String.format("TextParser: " + nTokenCounter + "," + nGroupCounter + "," + nTokenGroupCounter + " SAVE LAST: | gLastMapType: " + groupLastMapType + " | LastMapType: " + lastMapType + " | LastStopToken: " + lastStopToken + " | CurStopToken: " + curStopToken + " | Token: " + txtToken + " | TokenType: " + txtTokenName + " | Rule: " + txtTokenRule + " | ParentRule: " + lastParentRule + " | TokenList: "+ txtTokenList));	                    

	            }
	            
	       		txtTokenList = "";
	       		txtTaggedTokenList = "";
	           	firstIteration = false;
		            
	        } //if (firstIteration == true )
	        
       } //if ()
       
   //return txtParseTree;
   } 
 
    static void ParseMap(goldengine.java.Reduction TheReduction, int nPathCounter, int nDepthCounter, String txtRulePath)
    {
   	String txtRuleName = "";
   	String txtTokenData = "";
   	String txtTokenRule = "";
	String txtTokenRuleData = "";
   	
       // Do not write rules with empty items
       if (TheReduction.getTokenCount() > 0) {
       	
          nPathCounter+=1;
          txtRuleName = TheReduction.getParentRule().name();
          txtRulePath = txtRulePath + "/" + txtRuleName; 
   
           // Display the children of the reduction (Token)
           for (int i = 0; i < TheReduction.getTokenCount(); i++) {

               switch(TheReduction.getToken(i).getKind())
               {
                   case goldengine.java.SymbolTypeConstants.symbolTypeNonterminal:
	                   	//Next Recursion
	                   	ParseMap((goldengine.java.Reduction)TheReduction.getToken(i).getData(), nPathCounter, nDepthCounter, txtRulePath);
	                   	break;
                   default:
                	    nDepthCounter +=1;
                	    nTokenCounter +=1;
                	    nTokenMapCounter = nTokenCounter;
                	    txtTokenRule = TheReduction.getParentRule().getText().substring(0, TheReduction.getParentRule().getText().lastIndexOf("::=")-1 ).trim();
                	    txtTokenRuleData = TheReduction.getParentRule().getText().substring( TheReduction.getParentRule().getText().lastIndexOf("::=")+3, TheReduction.getParentRule().getText().length() ).trim();
	                    txtTokenData = TheReduction.getParentRule().getText();
	                    //Add new token row to global Array
	                	String[] row = {String.valueOf(nTokenCounter),
	                					"0",
	                					TheReduction.getToken(i).getData().toString(),
	                					txtTokenRuleData,
	                					String.valueOf(nPathCounter),
	                					String.valueOf(nDepthCounter),
	                					String.valueOf(i),
	                					txtRulePath,
	                					txtTokenRule,
	                					txtTokenData,
	                					txtTaggedTokenList.trim()
	                					};
	                	resultSet.add(row) ;

               }
           }
       }
   } 
   
	static String JsonParseTree(goldengine.java.Reduction TheReduction, int counter, int ident, String parent, String txtParseTree)
	     {
			String txtTokenName = "";	
			String txtRuleName = "";
	    	String txtRuleElement = "";
	    	
	        // Do not write rules with empty items
	        if (TheReduction.getTokenCount() > 0) {
	           nTokenCounter +=1;
	           txtRuleName = TheReduction.getParentRule().name();
	           txtRuleElement = txtRuleName.toLowerCase().replace("<","").replace(">","");
	           //txtTokenRule = TheReduction.getParentRule().getText().substring(0, TheReduction.getParentRule().getText().lastIndexOf("::=")-1 ).trim();
	           //txtTokenRuleData = TheReduction.getParentRule().getText().substring( TheReduction.getParentRule().getText().lastIndexOf("::=")+3, TheReduction.getParentRule().getText().length() ).trim();
	           if(txtParseTree.length()>0 && txtParseTree.endsWith("}")){
	        		 txtParseTree = txtParseTree + ","; 
	        	}
	           txtParseTree = txtParseTree +  "\r\n" + (String.format("%"+(nTokenCounter)+"s", " ")) + " {\"" + txtRuleElement + "\":" ;  //element start
	         //  txtParseTree = txtParseTree + (String.format("%"+(nTokenCounter+1)+"s", " ")) + "txt=\"" + txtTokenRuleData + "\"\r\n";  
	    
	            // Display the children of the reduction (Token)
	            for (int i = 0; i < TheReduction.getTokenCount(); i++) {

	                switch(TheReduction.getToken(i).getKind())
	                {
	                    case goldengine.java.SymbolTypeConstants.symbolTypeNonterminal:
	                    	//Next Recursion
	                    	txtParseTree = JsonParseTree((goldengine.java.Reduction)TheReduction.getToken(i).getData(), nTokenCounter, ident, "RuleParent", txtParseTree);
	                    	break;
	                    default:
	                    	 ident+=1;
	                    	 txtTokenName = TheReduction.getToken(i).getName();
	                    	 if(i>0){
	                    		 txtParseTree = txtParseTree + ","; 
	                    	 }
	                    	 txtParseTree = txtParseTree + "\r\n" + (String.format("%"+(nTokenCounter+ident) + "s", " "))
				         	 				//+ ",\"" + TheReduction.getParentRule().name().replace("<","").replace(">","") + "\":"
	                    	 				+ "{\"Token\":\"" + TheReduction.getToken(i).getData() + "\",\"Type\":\"" + txtTokenName + "\"}";
	                }
	            }
	            txtParseTree = txtParseTree + (String.format("%"+(nTokenCounter)+"s", " ")) + "} \r\n";		//element close
	        }
	    return txtParseTree;
	    } 
	
    static String XmlParseTree(goldengine.java.Reduction TheReduction, int counter, int ident, String parent, String txtParseTree)
     {
    	String txtTokenName = "";
    	String txtRuleName = "";
    	String txtRuleElement = "";
    	//String txtTokenRule = "";
    	String txtTokenRuleData = "";
    	
    	if (txtParseTree.length() == 0) {
    		txtParseTree = "<?xml version=\"1.0\"?>\r\n";
    		}
    	
        // Do not write rules with empty items
        if (TheReduction.getTokenCount() > 0) {
           nTokenCounter+=1;
           txtRuleName = TheReduction.getParentRule().name();
           txtRuleElement = txtRuleName.replace("<","").replace(">","").replace(" ","_");
           //txtTokenRule = TheReduction.getParentRule().getText().substring(0, TheReduction.getParentRule().getText().lastIndexOf("::=")-1 ).trim();
           txtTokenRuleData = TheReduction.getParentRule().getText().substring( TheReduction.getParentRule().getText().lastIndexOf("::=")+3, TheReduction.getParentRule().getText().length() ).trim().replace("<","&lt;").replace(">","&gt;");
           txtParseTree = txtParseTree + (String.format("%"+(nTokenCounter)+"s", " ")) + " <" + txtRuleElement + " type=\"" + txtTokenName + "\"" + " txt=\"" + txtTokenRuleData + "\">\r\n"; 
    

            // Display the children of the reduction (Token)
            for (int i = 0; i < TheReduction.getTokenCount(); i++) {

                switch(TheReduction.getToken(i).getKind())
                {
                    case goldengine.java.SymbolTypeConstants.symbolTypeNonterminal:
                    	//Next Recursion
                    	txtParseTree = XmlParseTree((goldengine.java.Reduction)TheReduction.getToken(i).getData(), nTokenCounter, ident, "RuleParent", txtParseTree);
                    	break;
                    default:
                    	 ident+=1;
                    	 txtTokenName = TheReduction.getToken(i).getName();
                    	 txtParseTree = txtParseTree + (String.format("%"+(nTokenCounter+ident)+"s", " "))
                    	 				+ "<" + TheReduction.getParentRule().name().replace("<","").replace(">","").replace(" ","_") + ">"
                    	 				+ TheReduction.getToken(i).getData()
                    	 				+ "</" + TheReduction.getParentRule().name().replace("<","").replace(">","").replace(" ","_") + ">\r\n";
                }
            }
            txtParseTree = txtParseTree + (String.format("%"+(nTokenCounter)+"s", " ")) + " </" + txtRuleElement + ">\r\n";
        }
    return txtParseTree;
    } 

}
