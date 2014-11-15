package test;

import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.plugins.EventType;
import eu.leads.processor.plugins.NutchTransformPlugin;
import eu.leads.processor.plugins.PluginManager;
import eu.leads.processor.plugins.PluginPackage;

import java.io.IOException;
import java.util.ArrayList;

public class ClusteredTest {
    public static void main(String[] args) {
        LQPConfiguration.initialize();
        ArrayList<InfinispanManager> clusters = new ArrayList<InfinispanManager>();
        clusters.add(InfinispanClusterSingleton.getInstance()
                         .getManager());  //must add because it is used from the rest of the system

        //Crucial for joining infinispan cluster
        for (InfinispanManager cluster : clusters) {
            cluster.getPersisentCache("clustered");
        }
        String id = NutchTransformPlugin.class.getCanonicalName();
        String jarPath = "/home/vagvaz/Projects/idea/leads-query-processor/nqe/system-plugins/nutch-transform-plugin/target/nutch-transform-plugin-1.0-SNAPSHOT-jar-with-dependencies.jar";
        String confPaht = "/home/vagvaz/Projects/idea/leads-query-processor/nqe/system-plugins/nutch-transform-plugin/nutch-conf.xml";
        PluginPackage ntplugin = new PluginPackage(id,id,jarPath,confPaht);
        //Create plugin package for upload (id,class name, jar file path, xml configuration)

        /*PluginPackage plugin = new PluginPackage();*/

        //upload plugin
        PluginManager.uploadPlugin(ntplugin);

        //distributed deployment  ( plugin id, cache to install, events)
        PluginManager.deployPlugin(id,"defaultCache", EventType.ALL);

        /*Start putting values to the cache */
        System.out.println("waiting to finish with nutch press enter");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
	/*Cleanup and close....*/
                InfinispanClusterSingleton.getInstance().getManager().stopManager();

    }
}
