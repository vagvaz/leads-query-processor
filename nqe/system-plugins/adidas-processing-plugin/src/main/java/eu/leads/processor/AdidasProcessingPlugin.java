package eu.leads.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import eu.leads.ProcessingFilterSingleton;
import eu.leads.PropertiesSingleton;
import eu.leads.datastore.DataStoreSingleton;
import eu.leads.infext.logging.redirect.StdLoggerRedirect;
import eu.leads.infext.proc.com.indexing.KeywordsListSingletonExt;
import eu.leads.infext.proc.com.indexing.LeadsDocumentConceptSearchCall;
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
	
	      SystemInit.init(config);

	      // INIT page content processor
	      if(pageProcessingPojo == null) {
	    	  String [] strStages = config.getStringArray("processingStage");
	    	  if(strStages == null || strStages.length == 0) 
	    		  pageProcessingPojo = new PageProcessingPojo();
	    	  else
		    	  pageProcessingPojo = new PageProcessingPojo(LEADSUtils.stringArray2integerArray(strStages));
	      }
	      
	      // START Python ZeroMQ Server!
//	      PZSStart.start(config);   
      } 
      catch (Exception e) {
    	  System.out.println("Exception during initializing the plugin!");
    	  e.printStackTrace();
      }
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
			String webpageJson = (String)value;
			Tuple webpage = new Tuple(webpageJson);
		// Turn extract table:nutch_uri from key
		String [] tableUri = key.toString().split(":", 2);
		String table = tableUri[0];

//		String uri = tableUri[1].split(",")[0];
		String uri = webpage.getAttribute("default.webpages.url");
		uri = LEADSUtils.normalizeUri(uri);
		
		if(!ProcessingFilterSingleton.shouldProcess(uri)) {
			System.out.println("Skipping "+uri);
			return false;
		}
		
		// Convert value into tuple
		
		// Extract content and timestamp from the tuple
		String content    = webpage.getAttribute("default.webpages.body");
		String timestamp  = webpage.getAttribute("default.webpages.ts");
		Object headersObj = webpage.getGenericAttribute("default.webpages.headers");
		
		if(content != null && !content.trim().isEmpty() && timestamp != null && headersObj != null) {
			if(isContentTypeHTML(headersObj)) {
				HashMap<String,Object> cacheColumns = new HashMap<>();
				cacheColumns.put(DataStoreSingleton.getMapping().getProperty("leads_crawler_data-content"), content);
			
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
