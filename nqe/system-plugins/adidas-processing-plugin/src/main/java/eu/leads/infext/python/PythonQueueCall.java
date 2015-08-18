package eu.leads.infext.python;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.CharSet;
import org.apache.commons.lang.StringUtils;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

import scala.Array;
import eu.leads.PropertiesSingleton;
import eu.leads.infext.python.JZC2;

public class PythonQueueCall {

	Configuration config = PropertiesSingleton.getConfig();
	private JZC2 jzc;
	
	private SecureRandom random;


	public PythonQueueCall() {
		random = new SecureRandom();
        jzc = new JZC2(config.getList("pzsEndpoints"));
	}
	
	public PythonQueueCall(boolean longTimeout) {
		random = new SecureRandom();
        jzc = new JZC2(config.getList("pzsEndpoints"),longTimeout);
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
	
	
	public List<Object> call(String moduleName, Object... args) {
		
		/* TIME */ long start = System.currentTimeMillis();
		
		System.out.println("PythonQueueCall for module "+moduleName);
		
		List<Object> retValue = null;
		
		List<Object> params = new ArrayList<>();
		
		List<Object> paramsList = new ArrayList<>();
		
		for (int i=0; i<args.length; i++) {
			Object arg = args[i];
			
			List<String> nextParamsList = null;
			
			if(arg == null || arg instanceof String) {
				nextParamsList = new ArrayList<>();
				nextParamsList.add((String)arg);
			} 
			else if(arg instanceof List) {
				nextParamsList = (List<String>) arg;
			}
			else
				continue;
			
			for(String param : nextParamsList) {
				if (param == null)
					param = "None";
				if (argsViaFile.contains(i) || isHtml(param) || param.contains("\n")) {
					//byte[] byteParam = param.getBytes(Charset.forName("UTF-8"));
					//paramsList.add(byteParam);
					paramsList.add(param);
				}
				else if(cutSpaces.contains(i)) {
					param = param.replaceAll("\\s","");
					paramsList.add(param);
				}
				else {
					paramsList.add(param);
				}
			}
			
		}
		String path;
		if(moduleName.contains(".")) // full package name
			path = moduleName;
		else
			path = config.getString("pythonCLIPackage")+"."+moduleName;
		params.add(path);
		params.addAll(paramsList);
		
		String paramsString = StringUtils.join(params.toArray(),' ');
		
//		System.out.println(paramsString);
        
        retValue = jzc.send(params);
        if(retValue != null)
	        System.out.println("Received reply:\n\n" + retValue);	

		/* TIME */ System.err.println("+++ PythonQueueCall.call() time: "+((System.currentTimeMillis()-start)/1000.0)+" s");
        
		return retValue;		
	}

	
	private void removeFiles(List<String> filenamesList) {
		for(String filename : filenamesList) {
			File file = new File(filename);
			file.delete();
		}
	}


	public static void main(String [] args) {
		String confPath = "/data/workspace/leads-query-processor-plugins/plugin-examples/adidas-processing-plugin/adidas-processing-plugin-conf.xml";
		XMLConfiguration config;
		try {
			config = new XMLConfiguration(confPath);
			PropertiesSingleton.setConfig(config);
			
//		      // Start Python ZeroMQ Server processes!
//		      List<String> endpoints = config.getList("pzsEndpoints");
//		      String pythonPath = "PYTHONPATH="+config.getString("pythonPath");
//		      String commandBase = "/usr/bin/python2.7 -m eu.leads.infext.python.CLAPI.pzs ";
//		      String[] envp = {pythonPath};
//		      try {
//		    	  for(int i=0; i<endpoints.size(); i++) {
//			    	  String endpoint = endpoints.get(i);
//			    	  String command  = commandBase+endpoint;
//			    	  System.out.println(command);
//			    	  Runtime.getRuntime().exec(command, envp);
//			      }
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			PythonQueueCall pythonCall = new PythonQueueCall();
			pythonCall.sendViaFile(0);
			pythonCall.call("eu.leads.infext.python.CLAPI.helloworld_clinterface","hello","world");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
