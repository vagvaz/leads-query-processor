package test;

import eu.leads.processor.AdidasProcessingPlugin;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.plugins.PluginManager;
import eu.leads.processor.common.plugins.PluginPackage;
import eu.leads.processor.common.utils.storage.LeadsStorageFactory;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.plugins.EventType;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class ClusteredTest {
  public static void main(String[] args) {
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
     for ( InfinispanManager manager : cluster ) {
        manager.getPersisentCache("clustered");
     }
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
     
//     LQPConfiguration.getConf().setProperty("crawler.seed", seed); //For some reason it is ignored news.yahoo.com is used by default
//     LQPConfiguration.getConf().setProperty("crawler.depth", 1);
//     //Set desired target cache
//     LQPConfiguration.getConf().setProperty(StringConstants.CRAWLER_DEFAULT_CACHE, "webpages");
//     
//     //start crawler
//     PersistentCrawl.main(null);
     InfinispanManager menago = InfinispanClusterSingleton.getInstance().getManager();
     ConcurrentMap map = menago.getPersisentCache("default.webpages");
     
     System.out.println("Putting values to cache...");
     
     map.put("earga", "syf");
     map.put("werg", "kibel");
//    try {
//      Thread.sleep(initPeriod * 1000);
//    } catch ( InterruptedException e ) {
//      e.printStackTrace();
//    }
     Set set = map.keySet();
     System.out.println(set);
     
     
//     //Sleep for an amount of time to have the crawling running
//     try {
//        Thread.sleep(crawlingPeriod * 1000);
//     } catch ( InterruptedException e ) {
//        e.printStackTrace();
//     }
//	 /*Stop crawling*/
//     PersistentCrawl.stop();
     
     //Sleep for an amount of time to let processing run
     try {
        Thread.sleep(processingPeriod * 1000);
     } catch ( InterruptedException e ) {
        e.printStackTrace();
     }     
     InfinispanClusterSingleton.getInstance().getManager().stopManager();
     System.exit(0);

  }
}
