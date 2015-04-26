package eu.leads.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.leads.PropertiesSingleton;
import eu.leads.datastore.DataStoreSingleton;
import eu.leads.infext.logging.redirect.StdLoggerRedirect;
import eu.leads.infext.proc.realtime.env.pojo.PageProcessingPojo;
import eu.leads.infext.python.PZSStart;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.plugins.PluginInterface;
import eu.leads.utils.LEADSUtils;

import org.apache.commons.configuration.Configuration;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vagvaz on 10/14/14.
 */
public class AdidasProcessingPlugin implements PluginInterface {
   private final String className = AdidasProcessingPlugin.class.getCanonicalName();
   private Configuration configuration;
   private InfinispanManager manager;
   private PageProcessingPojo pageProcessingPojo = null;
   private boolean isPZSStarted = false;
   @Override
   public String getId() {
      return className;
   }

   @Override
   public void setId(String id) {
      System.err.println("Cannot set ID default is the name of the class");
   }

   @Override
   public String getClassName() {
      return className;
   }

   @Override
   public void initialize(Configuration config, InfinispanManager manager) {
	  try {
	      this.configuration = config;
	      this.manager = manager;
	      
	      setLogging(configuration);
	      
	      System.out.println("%%%%% Initializing the plugin...");
	
	      // KEEP config
	      PropertiesSingleton.setConfig(config);
	      
	      // READ Configuration for Cassandra
	      DataStoreSingleton.configureDataStore(config);
	      
	      // READ Configuration for the plugin
	      PropertiesSingleton.setResourcesDir(config.getString("resources_path"));

	      if(pageProcessingPojo == null)
	    	  pageProcessingPojo = new PageProcessingPojo();
      
	      // START Python ZeroMQ Server!
	      PZSStart.start(config);
	      
      } 
      catch (Exception e) {
    	  System.out.println("Exception during initializing the plugin!");
    	  e.printStackTrace();
      }
   }
   
   private void setLogging(Configuration config) throws Exception {
	   String dir = config.getString("loggingDir");
	   System.out.println("Logging to "+dir);
	   StdLoggerRedirect.initLogging(dir);
//	   System.setOut(outputFile(dir+"/leads-java-"+(new Date().getTime())+".out"));
//	   System.setErr(outputFile(dir+"/leads-java-"+(new Date().getTime())+".err"));
   }

//   protected java.io.PrintStream outputFile(String name) throws java.io.FileNotFoundException {
//	   File file = new File(name);
//	   file.getParentFile().mkdirs();
//       return new java.io.PrintStream(new java.io.BufferedOutputStream(new java.io.FileOutputStream(file)));
//   }

   @Override
   public void cleanup() {

   }

   @Override
   public void modified(Object key, Object value, Cache<Object, Object> cache) {
	   System.out.println("XXXmodified()");
      //Have one method for processing both events
      processTuple(key,value);
   }

   @Override
   public void created(Object key, Object value, Cache<Object, Object> cache) {
	  //
	  /* TIME */ Long start = System.currentTimeMillis();
	  //
      boolean isProcessed = processTuple(key,value);
      
      if(isProcessed) {
	  //
      /* TIME */ Long finish = System.currentTimeMillis();
	  /* TIME */ System.err.println("+++ Plugin.created() time for "+key+": "+((finish-start)/1000.0)+" s");
	  //   
      }
   }

    /**
     * 
     * @param key
     * @param value
     */
    private boolean processTuple(Object key, Object value) {
	   
	    System.out.println("######## processTuple() method started");
		
		// Turn extract table:nutch_uri from key
		String [] tableUri = key.toString().split(":", 2);
		String table = tableUri[0];
		String uri = tableUri[1];
		uri = normalizeUri(uri);
		
		// Convert value into tuple
		String webpageJson = (String)value;
		Tuple webpage = new Tuple(webpageJson);
		
		// Extract content and timestamp from the tuple
		String content    = webpage.getAttribute("body");
		String timestamp  = webpage.getAttribute("published");
		Object headersObj = webpage.getGenericAttribute("headers");
		
		if(content != null && timestamp != null && headersObj != null) {
			if(isContentTypeHTML(headersObj)) {
				HashMap<String,Object> cacheColumns = new HashMap<>();
				cacheColumns.put("default.content.content", content);
			
				System.out.println("table:     " + table);
				System.out.println("uri:       " + uri);
				System.out.println("content:   " + (content.length()>80 ? content.substring(0,80)+"..." : content) );
				System.out.println("timestamp: " + timestamp);
				
				// Execute page processing
				System.out.println("Starting with the page processing...");
				System.err.println("Processing "+uri+" ...");
				pageProcessingPojo.execute(uri, timestamp, table, cacheColumns);
				return true;
			}
			else {
				System.out.println("Content-Type is none of html types. Skipping.");
			}
		}
		else {
			System.out.println("Either content, timestamp or header is null. Skipping.");
		}
		return false;
    }

    private boolean isContentTypeHTML(Object headersObj) {
    	Map<String, String> headers = (Map<String, String>) headersObj;
		String contentType = headers.get("Content-Type");
		
		String lastModified = headers.get("Last-Modified");
		if(lastModified!=null) System.out.println("Last-Modified:"+lastModified);
		
		if(contentType == null) return false;
		if(contentType.toLowerCase().contains("html")) return true;
		else return false;
	}

	/**
     * For now, we simply treat URIs with various requests values as the same one.
     * 
     * @param uri
     * @return
     */
    private String normalizeUri(String uri) {
		int requestStart = uri.indexOf("?");
		if(requestStart>0)
			return uri.substring(0, requestStart);
		else
			return uri;
	}

@Override
   public void removed(Object key, Object value, Cache<Object, Object> cache) {
      // Do Nothing probably never called.

   }

   @Override
   public Configuration getConfiguration() {
      return configuration;
   }

   @Override
   public void setConfiguration(Configuration config) {
      this.configuration = config;
   }

}
