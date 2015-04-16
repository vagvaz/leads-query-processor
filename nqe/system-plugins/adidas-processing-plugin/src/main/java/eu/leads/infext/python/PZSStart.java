package eu.leads.infext.python;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;

import org.apache.commons.configuration.Configuration;

public class PZSStart {

	/***
	 * Start Python ZeroMQ Server processes!
	 * 
	 * @param config - system configuration
	 * @throws IOException 
	 */
	public static void start(Configuration config) throws IOException {
	      List<String> endpoints = config.getList("pzsEndpoints");
	      String pythonPath = "PYTHONPATH="+config.getString("pythonPath");
	      String commandBase = "/usr/bin/python2.7 -m eu.leads.infext.python.CLAPI.pzs ";
	      String[] envp = {pythonPath};
		  for(int i=0; i<endpoints.size(); i++) {
	    	  String endpoint = endpoints.get(i);
	    	  String [] ipPort = endpoint.split(":");
	    	  int port = Integer.parseInt(ipPort[ipPort.length-1]);
	    	  if(isPortAvailable(port)) {
		    	  String command  = commandBase+endpoint;
		    	  System.err.println("Starting command: '"+command+"' with environment: '"+envp+"'");
		    	  Process p = Runtime.getRuntime().exec(command, envp);
		    	  try { Thread.sleep(1000); } 
		    	  catch (InterruptedException e) { e.printStackTrace(); }
		    	  if(isProcessRunning(p))
		    		  System.err.println("Python ZeroMQ Server STARTED at "+endpoint);
	    	  }
	    	  else
	    		  System.err.println("Python ZeroMQ Server already RUNNING at "+endpoint);
		  }
	}
	
	private static boolean isProcessRunning(Process process) {
	    try {
	        process.exitValue();
	        return false;
	    } catch (Exception e) {
	        return true;
	    }
	}
	
	private static boolean isPortAvailable(int port) {
	    if (port < 0 || port > 100000) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }
	    return false;
	}
	
}
