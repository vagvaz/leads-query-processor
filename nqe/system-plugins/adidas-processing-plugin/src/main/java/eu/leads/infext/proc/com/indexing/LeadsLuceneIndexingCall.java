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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.CharSet;
import org.apache.commons.lang.StringUtils;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

import scala.Array;
import eu.leads.PropertiesSingleton;
import eu.leads.infext.python.JZC;
import eu.leads.utils.LEADSUtils;

public class LeadsLuceneIndexingCall {

	Configuration config = PropertiesSingleton.getConfig();
	private JZC jzc;
	
	private SecureRandom random;


	public LeadsLuceneIndexingCall() {
		random = new SecureRandom();
        jzc = new JZC(config.getList("lliEndpoints"));
	}
	
	
	boolean isHtml(String text) {
		//return text.matches("[\\S\\s]*\\<html[\\S\\s]*\\>[\\S\\s]*\\<\\/html[\\S\\s]*\\>[\\S\\s]*");
		return text.matches("[\\S\\s]*\\<html[\\S\\s]*\\>[\\S\\s]*"); // amazon has not html closing... o_0
	}
	
	
	private List<Integer> argsViaFile = new ArrayList<>();
	private List<Integer> cutSpaces = new ArrayList<>();

	
	public void sendViaFile(Integer... argNumber) {
		argsViaFile = Arrays.asList(argNumber);
	}
	
	public void cutSpaces(Integer... argNumber) {
		cutSpaces = Arrays.asList(argNumber);
	}
	
	
	public String paramInFile(String... params) {
		String fileExt = new java.text.SimpleDateFormat("yyMMddHHmmssSSS").format(new Date())+random.nextInt(1000);
		String fileBase = config.getString("pythonFileDirectory") + config.getString("pythonFileBasename");
		String fileName = fileBase + fileExt;
		
		File file = new File(fileName);
		if(!file.exists()) {
			try {
				file.createNewFile();
				PrintWriter out = new PrintWriter(file);
				String outputStr = "";
				for(String param : params)
					outputStr += param + "\n";
				outputStr = outputStr.substring(0, outputStr.length()-1);
				out.print(outputStr);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				fileName = null;
			}
		}
		return fileName;
	}
	
	
	public HashMap<String,Double> call(String content) {
		
		/* TIME */ long start = System.currentTimeMillis();
		
		List<Object> response = null;
		HashMap<String,Double> returnMap = new HashMap<>();
		
		List<String> params = new ArrayList<>();
		
		List<String> paramsList = new ArrayList<>();
		List<String> filenamesList = new ArrayList<>();
		

		String fileName = paramInFile(content);
		if(fileName != null) {
			params.add("file:"+fileName);
			filenamesList.add(fileName);
		}
        
        response = send(params);
        if(response != null)
	        System.out.println("Received reply:\n\n" + response);
        
        for(int i=0; i<response.size();i+=2) {
        	returnMap.put((String)response.get(i), Double.parseDouble((String)response.get(i+1)));
        }
        
        removeFiles(filenamesList);

		/* TIME */ System.err.println("+++ LeadsLuceneIndexingCall.call() time: "+((System.currentTimeMillis()-start)/1000.0)+" s");
        
		return returnMap;		
	}

	
	private List<Object> send(List<String> params) {
		List<Object> returnList = new ArrayList<Object>();
		
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);
        socket.setSendTimeOut(3000);
        socket.setReceiveTimeOut(3000);
        socket.connect (PropertiesSingleton.getConfig().getString("lliEndpoints"));
        
        byte[] reply = null;
    	ZMsg req = new ZMsg();
    	for(String param : params)
    		req.add(param);
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


	private void removeFiles(List<String> filenamesList) {
		for(String filename : filenamesList) {
			File file = new File(filename);
			file.delete();
		}
	}


	public static void main(String [] args) throws MalformedURLException, IOException {
		String confPath = "/leads/workm30/leads-query-processor/nqe/system-plugins/adidas-processing-plugin/adidas-processing-plugin-conf.xml";
		XMLConfiguration config;
		try {
			config = new XMLConfiguration(confPath);
			PropertiesSingleton.setConfig(config);
			
			String content1 = "";
//		    String uri = "http://runblogger.com/minimalist-running";
//		    String uri = "http://runblogger.com/2011/06/vibram-fivefingers-komodosport-review.html?replytocom=215625693";
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
		    System.out.println(content);
			
			LeadsLuceneIndexingCall pythonCall = new LeadsLuceneIndexingCall();
			pythonCall.sendViaFile(0);
			pythonCall.call(content);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
