package test;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.CacheManagerFactory;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.plugins.PluginManager;
import eu.leads.processor.common.plugins.PluginPackage;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.common.utils.storage.LeadsStorage;
import eu.leads.processor.common.utils.storage.LeadsStorageFactory;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.plugins.NutchTransformPlugin;
import org.infinispan.Cache;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class ClusteredTest {
    static LeadsStorage storage = null;
    public static void main(String[] args) {
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
        System.out.println("PLUGIN ID: " + NutchTransformPlugin.class.getCanonicalName());
        PluginPackage plugin = new PluginPackage(NutchTransformPlugin.class.getCanonicalName(),
                                                        NutchTransformPlugin.class.getCanonicalName(),
                                                        "/home/vagvaz/Projects/idea/nutch-transform-plugin/target/nutch-transform-plugin-1.0-SNAPSHOT-jar-with-dependencies.jar",
                                                        "/home/vagvaz/Projects/idea/nutch-transform-plugin/nutch-transform-plugin-conf.xml" );
        Properties conf = new Properties();
        conf.setProperty("prefix","/tmp/leads/");
        PluginManager.initialize(LeadsStorageFactory.LOCAL, conf);
        storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.LOCAL,conf);
        //upload plugin
//        PluginManager.uploadPlugin(plugin);
        String jarFileName = plugin.getJarFilename();
        String jarTarget = "plugins/"+plugin.getId()+"/";
        if(!uploadJar("vagvaz",jarFileName,jarTarget)){
            System.out.println("ERROR");
        }
        plugin.setJarFilename(jarTarget);

        if(!PluginManager.uploadInternalPlugin(plugin))
        {
            System.err.println("ERR");
        }

        //distributed deployment  ( plugin id, cache to install, events)
        //PluginManager.deployPlugin();
//        PluginManager.deployPluginListener(TransformPlugin.class.getCanonicalName(), "default.webpages",
//
//                                      EventType.CREATEANDMODIFY,"defaultUser");
        plugin.setUser("vagvaz");
        PluginManager.addPluginToCache(plugin, 7, (Cache) clusters.get(0).getPersisentCache(StringConstants
                                                                                                    .PLUGIN_ACTIVE_CACHE),
                                              "WebPage");
        try {
            PluginManager.deployPluginListener(plugin.getId(), "WebPage", "vagvaz", clusters.get(0),
                                                      storage);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    /*Start putting values to the cache */

        System.out.println("wait for input");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Iterate through local cache entries to ensure things went as planned
        Cache cache = (Cache) InfinispanClusterSingleton.getInstance().getManager()
                                      .getPersisentCache("default.webpages");
        PrintUtilities.printMap(cache);
        System.out
                .println("Local cache " + cache.entrySet().size() + " --- global --- " + cache.size());
        //PersistentCrawl.stop();

        System.out.print("STARTING NEW MANAGER");
        clusters.add(CacheManagerFactory.createCacheManager());

        System.exit(0);
    }

    public static boolean uploadJar(String username,String jarPath,String prefix){
        try {
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(jarPath));
            ByteArrayOutputStream array = new ByteArrayOutputStream();
            byte[] buffer = new byte[20*1024*1024];
            byte[] toWrite = null;
            int size = input.available();
            int counter = -1;
            while( size > 0){
                counter++;

                int readSize = input.read(buffer);
                toWrite = Arrays.copyOfRange(buffer, 0, readSize);
                if(!uploadData(username,toWrite,prefix+"/"+counter)) {
                    return false;
                }
                size = input.available();
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  false;
    }

    public static boolean uploadData(String username, byte[] data, String target){
        storage.writeData(target,data);
        return true;
    }
}
