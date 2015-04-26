package test;

import eu.leads.crawler.PersistentCrawl;
import eu.leads.processor.AdidasProcessingPlugin;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.plugins.PluginManager;
import eu.leads.processor.common.plugins.PluginPackage;
import eu.leads.processor.common.utils.storage.LeadsStorageFactory;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.plugins.EventType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class ClusteredTest {
  public static void main(String[] args) throws UnsupportedEncodingException, IOException {
	  String seed = args.length>2 ? args[3] : "http://www.bbc.com/news/uk-31545744";
	  int initPeriod 		= args.length>0 ? Integer.parseInt(args[0]) : 60;
	  int crawlingPeriod 	= args.length>0 ? Integer.parseInt(args[1]) : 15;
	  int processingPeriod 	= args.length>1 ? Integer.parseInt(args[2]) : 50;

    Properties conf = new Properties();
    conf.setProperty("prefix","/tmp/leads/");
    PluginManager.initialize(LeadsStorageFactory.LOCAL, conf);
     LQPConfiguration.initialize();
     ArrayList<InfinispanManager> cluster = new ArrayList<InfinispanManager>();
     cluster.add(InfinispanClusterSingleton.getInstance().getManager());  //must add because it is used from the rest of the system
     //Crucial for joining infinispan cluster

     //Create plugin package for upload (id,class name, jar file path, xml configuration)
        /*PluginPackage plugin = new PluginPackage();*/
     PluginPackage plugin = new PluginPackage(AdidasProcessingPlugin.class.getCanonicalName(), AdidasProcessingPlugin.class.getCanonicalName(),
                                                     "/tmp/adidas-processing-plugin/target/adidas-processing-plugin-1.0-SNAPSHOT-jar-with-dependencies.jar",
                                                     "/tmp/adidas-processing-plugin//adidas-processing-plugin-conf.xml");


     //upload plugin
     boolean uploaded = PluginManager.uploadPlugin(plugin);
     
     System.out.print("neu stuff: ");
     System.out.println(uploaded ? "###plugin uploaded!" : "###plugin not uploaded!!");

     //distributed deployment  ( plugin id, cache to install, events)
     //PluginManager.deployPlugin();
     PluginManager.deployPlugin(AdidasProcessingPlugin.class.getCanonicalName(), "default.webpages", EventType
                                                                                                .CREATEANDMODIFY,"testuser");

        /*Start putting values to the cache */
     
     //Sleep for an amount of time to initialize everything


     //Put some configuration properties for crawler

//    LQPConfiguration.getConf().setProperty("crawler.seed",
//                                            "http://www.bbc.com/news/uk-31545744"); //For some reason it is ignored news.yahoo.com is used by default
//    LQPConfiguration.getConf().setProperty("crawler.depth", 1);
//    //Set desired target cache
//    LQPConfiguration.getConf().setProperty(StringConstants.CRAWLER_DEFAULT_CACHE, "default.webpages");
//    //start crawler
//    PersistentCrawl.main(null);
//    //Sleep for an amount of time to test if everything is working fine
//    try {
//      int sleepingPeriod = 220;
//      Thread.sleep(sleepingPeriod * 1000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
     InfinispanManager menago = InfinispanClusterSingleton.getInstance().getManager();
     ConcurrentMap<Object,Object> map = menago.getPersisentCache("default.webpages");
     
     System.out.println("Putting values to cache...");
     
     String content1 = "";
     URL url = new URL("http://www.bbc.com/news/uk-31545744");
     try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
         for (String line; (line = reader.readLine()) != null;) {
             content1 += line;
         }
     }
     final String content = content1;
     
     map.put("default.webpages:http://www.bbc.com/news/uk-31545744", new Tuple() {{ setAttribute("body", content); }}.asJsonObject().toString());
     

//    try {
//      Thread.sleep(initPeriod * 1000);
//    } catch ( InterruptedException e ) {
//      e.printStackTrace();
//    }
     
     
//     //Sleep for an amount of time to have the crawling running
//     try {
//        Thread.sleep(crawlingPeriod * 1000);
//     } catch ( InterruptedException e ) {
//        e.printStackTrace();
//     }
//	 /*Stop crawling*/
//     PersistentCrawl.stop();
     
     //Sleep for an amount of time to let processing run

//     InfinispanClusterSingleton.getInstance().getManager().stopManager();
//     System.exit(0);

  }
}
