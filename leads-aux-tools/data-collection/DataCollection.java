package eu.leads.processor.test;

import eu.leads.crawler.PersistentCrawl;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.CacheManagerFactory;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.plugins.EventType;
import eu.leads.processor.plugins.PluginManager;
import eu.leads.processor.plugins.PluginPackage;
import org.infinispan.Cache;

import java.util.ArrayList;

/**
 * Created by vagvaz on 9/25/14.
 */
public class DataCollection {

   public static void main(String[] args) {
      long sleepingPeriod = 5;
      String webCacheName = "default.webpages";//StringConstants.CRAWLER_DEFAULT_CACHE;

      //Important Call to initialize System Configuration
      LQPConfiguration.initialize();


      //Put some configuration properties for crawler
      LQPConfiguration.getConf().setProperty("crawler.seed",
                                                    "http://www.bbc.co.uk"); //For some reason it is ignored news.yahoo.com is used by default
      LQPConfiguration.getConf().setProperty("crawler.depth", 3);
     String sentimentPluginClassName = "eu.leads.processor.plugins.pagerank.PagerankPlugin";
     String jarFile = "/home/vagvaz/Projects/idea/leads-query-processor/nqe/system-plugins/pagerank-plugin/target/pagerank-plugin-1.0-SNAPSHOT-jar-with-dependencies.jar";
     String confFile = "/home/vagvaz/Projects/idea/leads-query-processor/nqe/system-plugins/pagerank-plugin/pagerank-conf.xml";


      //Set desired target cache
      LQPConfiguration.getConf().setProperty(StringConstants.CRAWLER_DEFAULT_CACHE, webCacheName);
      //Create Infinispan Cluster of 3 infinispan local nodes...
      ArrayList<InfinispanManager> clusters = new ArrayList<InfinispanManager>();

     clusters.add(InfinispanClusterSingleton.getInstance().getManager());

//      for (InfinispanManager cluster : clusters) {
//         cluster.getPersisentCache(StringConstants.CRAWLER_DEFAULT_CACHE);
//      }
     PluginPackage sentimentPlugin = new PluginPackage(sentimentPluginClassName,sentimentPluginClassName,jarFile,confFile);
     PluginManager.uploadPlugin(sentimentPlugin);
     PluginManager.deployPlugin(sentimentPluginClassName,webCacheName, EventType.CREATEANDMODIFY);
      Cache pages = (Cache) clusters.get(0).getPersisentCache(webCacheName);
      System.out.println("pages size is " + pages.size());

      //start crawler

      //Sleep for an amount of time to test if everything is working fine
      boolean stop = pages.size() >= 5;
      PersistentCrawl.main(null);
      try {
         while(!stop)
         {
            Thread.sleep(sleepingPeriod * 1000);
            System.out.println("pages size is " + pages.size());
            if(pages.size() >= 5)
            {
               PersistentCrawl.stop();
               stop = true;
            }
         }

      } catch (InterruptedException e) {
         e.printStackTrace();
      }


      //PrintUtilities.saveMapToFile(pages,"/home/vagvaz/test/webpagesdata.json");
//      for(InfinispanManager cluster : clusters){
//         cluster.stopManager();
//      }

   }

}
