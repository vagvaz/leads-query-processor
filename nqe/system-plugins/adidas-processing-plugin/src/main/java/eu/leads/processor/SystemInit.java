package eu.leads.processor;

import org.apache.commons.configuration.Configuration;

import eu.leads.ProcessingFilterSingleton;
import eu.leads.PropertiesSingleton;
import eu.leads.datastore.DataStoreSingleton;
import eu.leads.infext.logging.redirect.StdLoggerRedirect;

public class SystemInit {

	public static void init(Configuration config) throws Exception {
		// SET logging
		setLogging(config);
		System.out.println("%%%%% Initializing the plugin...");

		// APPLY URL Filter
		setFilter(config);
		  
		// KEEP config
		PropertiesSingleton.setConfig(config);
		System.out.println("...config set");
		  
		// READ Configuration for Cassandra
		DataStoreSingleton.configureDataStore(config);
		System.out.println("...datastore configured");
		  
		// READ Configuration for the plugin
		PropertiesSingleton.setResourcesDir(config.getString("resources_path"));
		
	}
	
	private static void setFilter(Configuration config) {
		String filterString = config.getString("filter");
		String[] filter = filterString.split(";");
		ProcessingFilterSingleton.setFilter(filter);
	}
	
	private static void setLogging(Configuration config) throws Exception {
	   String dir = config.getString("loggingDir");
	   if(dir==null || dir.isEmpty() || dir.equals("off"))
		   System.out.println("Logging here");
	   else {
		   System.out.println("Logging to "+dir);
		   StdLoggerRedirect.initLogging(dir);
	   }
	}
	
}
