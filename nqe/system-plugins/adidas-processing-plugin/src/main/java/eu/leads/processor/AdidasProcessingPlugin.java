package eu.leads.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import eu.leads.PropertiesSingleton;
import eu.leads.datastore.DataStoreSingleton;
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
	      
	      System.out.println("%%%%% Initializing the plugin");
	
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
   
   private void setLogging(Configuration config) throws FileNotFoundException {
	   String dir = config.getString("loggingDir");
	   System.out.println("Logging to "+dir);
	   System.setOut(outputFile(dir+"/leads-java.out"));
	   System.setErr(outputFile(dir+"/leads-java.err"));
	   System.out.println("Let's start the party!");
	   System.err.println("Let's start the party!");
   }

   protected java.io.PrintStream outputFile(String name) throws java.io.FileNotFoundException {
	   File file = new File(name);
	   file.getParentFile().mkdirs();
       return new java.io.PrintStream(new java.io.BufferedOutputStream(new java.io.FileOutputStream(file)));
   }

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
	   System.out.println("XXXcreated()");
      processTuple(key,value);
   }

   /**
 * @param key
 * @param value
 */
   private void processTuple(Object key, Object value) {
//	   try {
		   System.out.println("######## processTuple YEAH");
		   System.out.println("CONTENT:\n"+value);
		   String [] tableUri = key.toString().split(":", 2);
		   String table = tableUri[0];
		   String uri = LEADSUtils.standardUrlToNutchUrl(tableUri[1]);
			String webpageJson = (String)value;
			Tuple webpage = new Tuple(webpageJson);
			
			String content = webpage.getAttribute("body");
			String timestamp = new Long(System.currentTimeMillis()).toString();
			HashMap<String,Object> cacheColumns = new HashMap<>();
			cacheColumns.put("default.content.content", content);
			
			// Here Do the heavy processing stuff
			System.out.println("########:"+getClassName().toString() + " calls a processing POJO on a key " + key);
			pageProcessingPojo.execute(uri, timestamp, table, cacheColumns);
			
/*			// ZEROMQ PYTHON CALL CHECK
 * 			PythonQueueCall pythonCall = new PythonQueueCall();
 *			pythonCall.call("eu.leads.infext.python.CLAPI.helloworld_clinterface","hello","world");
 *          System.out.println("Python called, no exceptions.");
 */
			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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
