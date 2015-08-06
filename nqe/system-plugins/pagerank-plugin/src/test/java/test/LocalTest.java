package test;

import comm.Worker;
import eu.leads.crawler.PersistentCrawl;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.plugins.PluginManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.plugins.EventType;
import eu.leads.processor.plugins.PluginInterface;
import eu.leads.processor.plugins.pagerank.PagerankPlugin;
import eu.leads.processor.plugins.pagerank.node.DSPMNode;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.Iterator;
import java.util.Map;

public class LocalTest {

    public static void main(String[] args) {

        String webCacheName = "webpages";
        int sleepingPeriod = 20;
        //Important Call to initialize System Configuration
        LQPConfiguration.initialize();
        //Set CacheMode to get LcoalImplementation only
        LQPConfiguration.getConf().setProperty("processor.infinispan.mode", "local");
        //Put some configuration properties for crawler
        LQPConfiguration.getConf().setProperty("crawler.seed", "http://www.economist.com/");
        LQPConfiguration.getConf().setProperty("crawler.depth", 3);
        //Set desired target cache
        LQPConfiguration.getConf().setProperty(StringConstants.CRAWLER_DEFAULT_CACHE, webCacheName);

        XMLConfiguration config = new XMLConfiguration();
        //Set plugin configuration (could be loaded from file)
        config.setProperty("cache", "pagerankCache");
        config.setProperty("vc_cache","approx_sum_cache");

        config.setProperty("attributes","links");
        config.setProperty("R","5");
        config.setProperty("rseed","11");
        config.setProperty("input","/home/dell/Desktop/web-NotreDameWSL.txt");
        config.setProperty("tempPath","temp.txt");

        PluginInterface plugin = new PagerankPlugin();
        //deploy plugin to local cache
        PluginManager.deployLocalPlugin(plugin, config, webCacheName, EventType.CREATEANDMODIFY,
                InfinispanClusterSingleton.getInstance().getManager());

        //start crawler
       PersistentCrawl.main(null);

        //Sleep for an amount of time to test if everything is working fine
        try {
            Thread.sleep(sleepingPeriod * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // retrieval of visit count of each cache's value, by calling : webpage.getVisitCount();
        Map cache1 = InfinispanClusterSingleton.getInstance().getManager().getPersisentCache("pagerankCache");
        Map cache2 = InfinispanClusterSingleton.getInstance().getManager().getPersisentCache("approx_sum_cache");
        printVCs(cache1, cache2);

        PersistentCrawl.stop();
        InfinispanClusterSingleton.getInstance().getManager().stopManager();
        System.exit(0);
    }

    /**
     * Use of entrySet iteration, trust this operation only in local mode.
     */
    private static void printVCs(Map cache1, Map cache2){

        //int global_vc = (Integer) InfinispanClusterSingleton.getInstance().getManager().getPersisentCache("approx_sum_cache").get(Const.GLOBAL_SUM);
        int realSum = 0;
        Iterator<Map.Entry> my_iterator = cache1.entrySet().iterator();
        Map.Entry my_entry;
        while (my_iterator.hasNext()) {

            my_entry = my_iterator.next();
            realSum += ((DSPMNode) my_entry.getValue()).getVisitCount();
            // System.out.println(my_entry.getKey()+"->"+(double) ((DSPMNode) my_entry.getValue()).getVisitCount()/* / (double) global_vc*/ );
        }

        // exchange messages with the coordinator in order to retrieve its global sum
        my_iterator = cache2.entrySet().iterator();
        my_entry = my_iterator.next();
        Worker w = (Worker) my_entry.getValue();
//        w.getChannel().sentTo(Node.COORDINATOR, new Message(w.getId(),"requestGlobalSum",0));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Accurate global sum: "+realSum+", e-approximate global sum: "+w.getLatestReceivedGlobalSum());
    }
}
