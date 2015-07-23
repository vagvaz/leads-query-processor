package eu.leads.infext.input;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import eu.leads.infext.logging.redirect.StdLoggerRedirect;
import eu.leads.infext.proc.com.indexing.LuLeadIndexer;
import eu.leads.infext.proc.com.indexing.model.KeywordMatchInfo;

public class LeadsIndexingServer {

	/**
	 * Input for ZeroMQ:
	 *  - Document: doc [key value]*
	 *  - Keywords: key [id keywords nonmatchingwords nonmatchingchars distancebetweenwords inorder]*
	 *  
	 * Output for ZeroMQ:
	 *  - Keywords: [false|true]*
	 *  - Document: 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
        if (args.length < 2) {
            System.out.printf("I: syntax: flserver1 <loggingdir> <endpoint>\n");
            System.exit(0);
        }
        
 	    String dir = args[0];
 	    if(!dir.equals("off")) {
	 	    if(dir.startsWith("$")) dir = System.getenv(dir);
	 	    System.out.println("Logging to "+dir);
	 	    try { StdLoggerRedirect.initLogging(dir);
			} catch (Exception e1) { e1.printStackTrace(); }
 	    }
        
        LuLeadIndexer dks = LuLeadIndexer.getInstance();

        while (true) {
	        ZMQ.Context context = ZMQ.context(1);
	        // Socket to talk to clients
	        ZMQ.Socket socket = context.socket(ZMQ.REP);
	        System.err.println("P01");
	        socket.bind (args[1]);
	        
        	try {
        		while (!Thread.currentThread ().isInterrupted ()) {
	            	ZMsg msg = null;
	            	try {msg = ZMsg.recvMsg(socket);}
	            	catch(org.zeromq.ZMQException e) {
	        	        System.err.println("P02x");
	            		System.err.println("Exception during ZMsg.recvMsg(): " + e.getMessage());
	                    socket.close();
	                    Thread.sleep(200);
	                    socket = context.socket(ZMQ.REP);
	                    socket.bind (args[1]);
	            		continue;
	            	}
	            	
	    	        System.err.println("P02");
            		System.out.println(">>> Received message <<<");
	            	String messageType = null;
	            	try {messageType = msg.popString();}
	            	catch(Exception e) {
	        	        System.err.println("P03x");
	            		System.err.println("Exception during msg.popString(): " + e.getMessage());
	            		continue;
	            	}

	    	        System.err.println("P03");
					ZMsg reply = new ZMsg();
	            	
					// DOCUMENT //
	            	if(messageType.equals("doc")) {
						String url = msg.pop().toString();
				        System.err.println("P04d");
						
	            		Map<String, String> contentParts = new HashMap<>();
	            		boolean moreParts = true;
	            		while(moreParts) {
	            			ZFrame zKey = msg.pop();
	            			String value = null;
	            			if(zKey != null) {
	            				String key = zKey.toString();
	            				ZFrame zValue = msg.pop();
	            				if(zValue != null) {
		            				byte [] bValue = zValue.getData();
		            				value = new String(bValue);
		            				if(value != null)
		            					contentParts.put(key, value);
	            				}
	            			}
	            			if(value==null)
	            				moreParts = false;
	            		}
	            		
	            		if(contentParts.size() > 0) {
	            			// part -> keywords numbers
	            			Map<String, List<KeywordMatchInfo>> kMap = dks.searchDocument(url, contentParts);
							System.out.println("Map of matched: "+kMap);
				      		for(Entry<String, List<KeywordMatchInfo>> partKeywords : kMap.entrySet()) {
				      			String part         		    = partKeywords.getKey();
				      			List<KeywordMatchInfo> keywords = partKeywords.getValue();
				      			if(!keywords.isEmpty()) {
							        reply.add(part);
							        for(KeywordMatchInfo keywordNo : keywords) {
							        	reply.add(keywordNo.id.toString());
							        	reply.add(keywordNo.matched);
							        	reply.add(keywordNo.score.toString());
							        }
				      			}
				      		}	
	            		}
	            	}
	            	// KEYWORDS //
	            	else if(messageType.equals("key")) {
	        	        System.err.println("P04k");
	            		
	            		boolean moreParts = true;
	            		String poppedString = null;
	            		while(moreParts) {
	            			// id
	            			poppedString = msg.popString();
	            			if(poppedString == null) break;
	            			long id = Long.parseLong(poppedString);
	            			// keywords
	            			poppedString = msg.popString();
	            			if(poppedString == null) break;
	            			String [] keywords = poppedString.toLowerCase().split("\\s+");
	            			// nonMatchingWords
	            			poppedString = msg.popString();
	            			if(poppedString == null) break;
	            			int nonMatchingWords = Integer.parseInt(poppedString);
	            			// nonMatchingChars
	            			poppedString = msg.popString();
	            			if(poppedString == null) break;
	            			int nonMatchingChars = Integer.parseInt(poppedString);
	            			// distanceBetweenWords
	            			poppedString = msg.popString();
	            			if(poppedString == null) break;
		            		int distanceBetweenWords = Integer.parseInt(poppedString);
		            		// inOrder
	            			poppedString = msg.popString();
	            			if(poppedString == null) break;
							boolean inOrder = Boolean.parseBoolean(poppedString);
							
							Boolean succeeded = dks.addKeywords(id, keywords, nonMatchingWords, nonMatchingChars, distanceBetweenWords, inOrder);
							
							reply.add(succeeded.toString());
	            		}
	            	}

            		if(reply.isEmpty())
            			reply.add("null");
            		System.out.println(">>> Sending reply: "+reply+"\n<<<");
	                reply.send(socket);
        	        System.err.println("P05");
	            	
        		}
	        } catch(Exception e) {
	            StringWriter sw = new StringWriter();
	            PrintWriter pw = new PrintWriter(sw);
	            e.printStackTrace(pw);
	            System.out.println(sw.toString());
	        }
            socket.close();
            context.term();
        }
    }
	
	static String readFile(String path){
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
