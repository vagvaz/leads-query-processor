package test;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.plugins.PluginManager;
import eu.leads.processor.common.plugins.PluginPackage;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.common.utils.storage.LeadsStorage;
import eu.leads.processor.common.utils.storage.LeadsStorageFactory;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.plugins.grep.GrepPlugin;
import org.infinispan.Cache;

import java.util.ArrayList;
import java.util.Properties;

//import eu.leads.processor.common.infinispan.CacheManagerFactory;
//import eu.leads.processor.plugins.sentiment.SentimentAnalysisPlugin;

/**
 * Created by vagvaz on 6/8/14.
 */
public class ClusteredHadoopTest {
    static LeadsStorage storage = null;

    public static void main(String[] args) {
        Class pluginClass = GrepPlugin.class;

        LQPConfiguration.initialize();
        ArrayList<InfinispanManager> clusters = new ArrayList<InfinispanManager>();
        clusters.add(InfinispanClusterSingleton.getInstance()
                .getManager());  //must add because it is used from the rest of the system
        //clusters.add(CacheManagerFactory.createCacheManager());

        //Crucial for joining infinispan cluster
        for (InfinispanManager cluster : clusters) {
            cluster.getPersisentCache("clustered");
        }

        //Create plugin package for upload (id,class name, jar file path, xml configuration)
        /*PluginPackage plugin = new PluginPackage();*/
        System.out.println("PLUGIN ID: " + pluginClass.getCanonicalName());
        PluginPackage plugin = new PluginPackage(pluginClass.getCanonicalName(),
                pluginClass.getCanonicalName(),
                "/home/trs/Projects/LEADS/leads-query-processor/nqe/system-plugins/sentiment-plugin/target/sentiment-plugin-1.0-SNAPSHOT-jar-with-dependencies.jar"
              , "/home/trs/Projects/LEADS/leads-query-processor/nqe/system-plugins/sentiment-plugin/sentiment-conf.xml"); //"/home/vagvaz/Projects/idea/transform-plugin/grep-plugin-conf.xml" );

        //plugin.calculate_MD5();
        Properties conf = new Properties();

        conf.setProperty("hdfs.url", "hdfs://snf-618466.vm.okeanos.grnet.gr:8020");
        conf.setProperty("fs.defaultFS", "hdfs://snf-618466.vm.okeanos.grnet.gr:8020");
        conf.setProperty("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.setProperty("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        conf.setProperty("prefix", "/user/vagvaz/");
        conf.setProperty("hdfs.user", "vagvaz");
        conf.setProperty("postfix", "0");

        //conf.setProperty("prefix","/tmp/leads/");
        PluginManager.initialize(LeadsStorageFactory.HDFS, conf);

        storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.HDFS, conf);
        //upload plugin
//        PluginManager.uploadPlugin(plugin);


//        if (!uploadJar("vagvaz", jarFileName, jarTarget)) {
//            System.out.println("ERROR");
//        }
        System.out.println("upload Plugin");
        if (!PluginManager.uploadPlugin(plugin)) {
            System.out.println("ERROR");
        }
        
        System.out.println("upload internal Plugin");

        if (!PluginManager.uploadInternalPlugin(plugin)) {
            System.err.println("ERR");
        }
        String jarTarget = "plugins/" + plugin.getId() + "/";
        plugin.setJarFilename(jarTarget);

        //distributed deployment  ( plugin id, cache to install, events)
        //PluginManager.deployPlugin();
//        PluginManager.deployPluginListener(GrepPlugin.class.getCanonicalName(), "default.webpages",
//
//                                      EventType.CREATEANDMODIFY,"defaultUser");
        plugin.setUser("vagvaz");
        System.out.println(" addPluginToCache");

        PluginManager.addPluginToCache(plugin, 7, (Cache) clusters.get(0).getPersisentCache(StringConstants
                        .PLUGIN_ACTIVE_CACHE),
                "default.webpages");

        System.out.println(" deployPluginListener");

        try {
            PluginManager.deployPluginListener(plugin.getId(), "default.webpages", "vagvaz", clusters.get(0),
                    storage);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    /*Start putting values to the cache */

        //Put some configuration properties for crawler
//        LQPConfiguration.getConf().setProperty("crawler.seed",
//                "http://www.bbc.co.uk"); //For some reason it is ignored news.yahoo.com is used by default
//        LQPConfiguration.getConf().setProperty("crawler.depth", 3);
//        //Set desired target cache
//        LQPConfiguration.getConf().setProperty(StringConstants.CRAWLER_DEFAULT_CACHE, "default.webpages");
        //start crawler
//        PersistentCrawl.main(null);
//        //Sleep for an amount of time to test if everything is working fine
//        try {
//            int sleepingPeriod = 32;
//            Thread.sleep(sleepingPeriod * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //Iterate through local cache entries to ensure things went as planned
        Cache cache = (Cache) InfinispanClusterSingleton.getInstance().getManager()
                .getPersisentCache("default.entities");

        Cache cache2= (Cache) InfinispanClusterSingleton.getInstance().getManager()
                .getPersisentCache("default.webpages");

        PrintUtilities.printMap(cache);
        System.out.println("Local cache " + cache.entrySet().size() + " --- global --- " +cache2.entrySet().size());
//        //PersistentCrawl.stop();
//
//        System.out.print("STARTING NEW MANAGER");
//        clusters.add(CacheManagerFactory.createCacheManager());
//
//        for (InfinispanManager cluster : clusters) {
//            cluster.getPersisentCache("clustered");
//        }

        // PersistentCrawl.main(null);
        System.out.println(" put data into webpages");
        int sleepingPeriod = 60;
        int i=0;
        while(i++<sleepingPeriod)
        try {
            System.out.print(" " + i + " ");
            Thread.sleep(1000);
            System.out
                    .println("Local cache " + cache.entrySet().size() + " --- global --- " + cache2.entrySet().size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.print("Print with NEW MANAGER");

        PrintUtilities.printMap(cache);
        /*Cleanup and close....*/
        //PersistentCrawl.stop();
        System.out
                .println("Local cache " + cache.entrySet().size() + " --- global --- " + cache2.entrySet().size());
        try {
            System.out.println(" " + i + " ");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InfinispanClusterSingleton.getInstance().getManager().stopManager();
        System.exit(0);
    }

//    public static boolean uploadJar(String username, String jarPath, String prefix) {
//        try {
//            BufferedInputStream input = new BufferedInputStream(new FileInputStream(jarPath));
//            byte[] buffer = new byte[10 * 1024 * 1024];
//            byte[] toWrite = null;
//            int size = input.available();
//            int counter = -1;
//            while (size > 0) {
//                counter++;
//
//                int readSize = input.read(buffer);
//                toWrite = Arrays.copyOfRange(buffer, 0, readSize);
//                if (!uploadData(username, toWrite, prefix + "/" + counter)) {
//                    return false;
//                }
//                size = input.available();
//            }
//            return true;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public static boolean uploadData(String username, byte[] data, String target) {
//        return storage.writeData(target, data);
//        //return true;
//    }
}
