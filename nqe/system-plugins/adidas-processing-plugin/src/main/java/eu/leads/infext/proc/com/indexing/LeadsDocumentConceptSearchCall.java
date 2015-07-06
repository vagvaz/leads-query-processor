package eu.leads.infext.proc.com.indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.CharSet;
import org.apache.commons.lang.StringUtils;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

import scala.Array;
import eu.leads.PropertiesSingleton;
import eu.leads.infext.proc.com.maincontent.JerichoTextContentExtraction;
import eu.leads.infext.python.JZC;
import eu.leads.utils.LEADSUtils;

public class LeadsDocumentConceptSearchCall {
	
	public static void addKeywords(List<List<Object>> keywordsDef) {
		
		/* TIME */ long start = System.currentTimeMillis();
		
		List<Object> response = null;
		HashMap<String,Double> returnMap = new HashMap<>();
		
		List<String> params = new ArrayList<>();
		
		params.add("key");
		
		for(List<Object> keywordsRow : keywordsDef)
			for(Object cell : keywordsRow)
				params.add(cell.toString());
        
        response = send(params);
        if(response != null)
	        System.out.println("Received reply:\n\n" + response);

		/* TIME */ System.err.println("+++ LeadsDocumentConceptSearchCall.addKeywords() time: "+((System.currentTimeMillis()-start)/1000.0)+" s");
        
		return;	
	}
	
	
	public static HashMap<String, List<KeywordMatchInfo>> searchDocument(String url, Map<String,String> contentParts) {
		
		/* TIME */ long start = System.currentTimeMillis();
		
		List<Object> response = null;
		HashMap<String,List<KeywordMatchInfo>> returnMap = new HashMap<>();
		
		List<String> params = new ArrayList<>();
		
		params.add("doc");
		params.add(url);
		
		for(Entry<String, String> part : contentParts.entrySet()) {
			params.add(part.getKey());
			params.add(part.getValue());
		}
        
        response = send(params);
        if(response != null)
	        System.out.println("searchDocument(): Received reply:\n" + response);
        
        if(response.size() >= 4) {
        	boolean correctData = true;
	        String contentPart = (String) response.get(0);
	        List<KeywordMatchInfo> keywordsScoresList = new ArrayList<>();
	        
	        for(int i=1; i<response.size()-2;i=i+3) {
	        	Long keyId = LEADSUtils.stringToLongOrNull(response.get(i).toString());
	        	if(keyId==null) {
	        		// Then it is a new part
	        		if(keywordsScoresList.size()>0) {
	        			returnMap.put(contentPart, keywordsScoresList);
	        			keywordsScoresList = new ArrayList<>();
	        		}
	        		contentPart = response.get(i).toString();
	        		continue;
	        	}
	        	else {
	        		String matched = response.get(i+1).toString();
	        		Double score = LEADSUtils.stringToDoubleOrNull(response.get(i+2).toString());
	        		if(score==null) {
		        		correctData = false;
		        		break;
		        	}
					KeywordMatchInfo keywordMatchInfo = new KeywordMatchInfo(keyId, matched, score);
					keywordsScoresList.add(keywordMatchInfo);
	        	}
	        }
			returnMap.put(contentPart, keywordsScoresList);
	        
	        if(!correctData) returnMap = null;
        }
        System.out.println("Keywords found: " + returnMap);

		/* TIME */ System.err.println("+++ LeadsDocumentConceptSearchCall.searchDocument() time: "+((System.currentTimeMillis()-start)/1000.0)+" s");
        
		return returnMap;	
	}

	
	private static List<Object> send(List<String> params) {
		List<Object> returnList = new ArrayList<Object>();
		
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);
        socket.setSendTimeOut(3000);
        socket.setReceiveTimeOut(3000);
        socket.connect (PropertiesSingleton.getConfig().getString("lliEndpoints"));
        
        byte[] reply = null;
    	ZMsg req = new ZMsg();
    	for(String param : params) {
//    		System.out.println("Adding to send: "+param);
    		req.add(new ZFrame(param.getBytes()));
    	}
        req.send(socket);
        
        try {
        	int timeoutsCount = 0;
	        while(true) {
	            reply = socket.recv();
	            if(reply == null) {
	            	timeoutsCount++;
	            	if(timeoutsCount>=3)
	            		break;
	            	else {
	            		System.out.println("No response from server, retrying...");
	            		try {Thread.sleep(500);} 
	            		catch (InterruptedException e) {}
	            		continue;
	            	}
	            }
	            System.out.println("Received " + new String (reply));
	        	returnList.add(new String(reply));
	        }
        } catch (org.zeromq.ZMQException e) {
        } finally {
        	System.out.println("Finished Receiving.");
        }
 
        socket.setLinger(0);
        socket.close();
        context.term(); // TODO
        
        return returnList;
	}


	public static void main(String [] args) throws MalformedURLException, IOException {
		String confPath = "/leads/workm30/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/adidas-processing-plugin-conf.xml";
		XMLConfiguration config;
		try {
			config = new XMLConfiguration(confPath);
			PropertiesSingleton.setConfig(config);
			
			// INPUT for addKeywords
			List<List<Object>> keywordsDef = new ArrayList<>();
			
			List<Object> keywordRow = new ArrayList<>();
			keywordRow.add(0L);
			keywordRow.add("vibram fivefingers shoes running");
			keywordRow.add(1);
			keywordRow.add(0);
			keywordRow.add(1);
			keywordRow.add(false);
			keywordsDef.add(keywordRow);
			
			keywordRow = new ArrayList<>();
			keywordRow.add(1L);
			keywordRow.add("kirby lohff");
			keywordRow.add(0);
			keywordRow.add(0);
			keywordRow.add(0);
			keywordRow.add(true);
			keywordsDef.add(keywordRow);
			
			// INPUT for searchDocument	
			String content1 = "";
/*		    String uri = "http://runblogger.com/minimalist-running";
		    String uri = "http://runblogger.com/2011/06/vibram-fivefingers-komodosport-review.html?replytocom=215625693";*/
			String uri = "http://articles.philly.com/2011-05-04/entertainment/29508669_1_vibram-fivefingers-shoe-manufacturers-minimalist-runners";
			String nutchUri = LEADSUtils.standardUrlToNutchUrl(uri);
		    
			URLConnection connection = new URL(uri).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.connect();
		    URL url = new URL(uri);
		    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")))) {
		        for (String line; (line = reader.readLine()) != null;) {
		            content1 += line;
		        }
		    }
		    final String content = content1;
		    JerichoTextContentExtraction extractor = new JerichoTextContentExtraction();
		    final String text = extractor.extractText(content);
		    
			
			LeadsDocumentConceptSearchCall searchCall = new LeadsDocumentConceptSearchCall();
			searchCall.addKeywords(keywordsDef);
//			try { Thread.sleep(1); } 
//			catch (InterruptedException e) { 
//				e.printStackTrace(); 
//			}
			searchCall.searchDocument(uri,new HashMap<String,String>() {{ put("content",text); }});
//			try { Thread.sleep(1); } 
//			catch (InterruptedException e) { 
//				e.printStackTrace(); 
//			}
			searchCall.searchDocument(uri,new HashMap<String,String>() {{ put("content",text); }});
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
