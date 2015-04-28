package eu.leads.infext.input;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import eu.leads.PropertiesSingleton;
import eu.leads.datastore.DataStoreSingleton;
import eu.leads.datastore.datastruct.UrlTimestamp;
import eu.leads.infext.proc.com.indexing.DocumentKeywordSearch;

public class LeadsIndexingServer {

	public static void main(String[] args) throws ConfigurationException {
        if (args.length < 1) {
            System.out.printf("I: syntax: flserver1 <endpoint>\n");
            System.exit(0);
        }
        
		Configuration config = new XMLConfiguration(
//				"/home/ubuntu/.adidas/test/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/adidas-processing-plugin-conf.xml");
				"/leads/workm30/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/adidas-processing-plugin-conf.xml");
        // KEEP config
        PropertiesSingleton.setConfig(config);
        // READ Configuration for datastore
        DataStoreSingleton.configureDataStore(config);
        
        DocumentKeywordSearch dks = new DocumentKeywordSearch();
		
        List<String> kList = new ArrayList<>();//KeywordsListSingleton.getInstance().getKeywordsList();
        kList.add("boston marathon");
        kList.add("vibram");
        kList.add("kaciak");
		
        ZMQ.Context context = ZMQ.context(1);
        // Socket to talk to clients
        ZMQ.Socket socket = context.socket(ZMQ.REP);
        socket.bind (args[0]);
        try {
            while (!Thread.currentThread ().isInterrupted ()) {
            	ZMsg msg = null;
            	try {msg = ZMsg.recvMsg(socket);}
            	catch(org.zeromq.ZMQException e) {
            		e.printStackTrace();
                    socket.close();socket = context.socket(ZMQ.REP);
                    socket.bind (args[0]);
            		continue;
            	}
            	
            	String fileName = msg.popString();
                System.out.println("Received "+fileName);

	            String part 	= "";
	            String partId 	= "";
	            String content 	= null;
                if(fileName.startsWith("file:")) {
                	fileName = fileName.split(":")[1];
                	System.out.println("Filename "+fileName);
                	content = readFile(fileName);
                }
                
                if(content != null) {
		            dks.addDocument(part, partId, content);
	
	                ZMsg reply = new ZMsg();
	
		      		for(String keywords : kList) {
			  			String [] keywordsArray = keywords.split("\\s+");
			            HashMap<UrlTimestamp, Double> foundKeywords = dks.searchKeywords(keywordsArray);
			            Double relevanceFound = foundKeywords.get(new UrlTimestamp("", ""));
			            if(relevanceFound!=null) {
				            reply.add(keywords);
				            reply.add(relevanceFound.toString());
			            }
		      		}                
	                reply.send(socket);
	                
	                dks.refresh();
                }
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
