package test;

import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;

import java.util.ArrayList;
public class ClusteredTest {
    public static void main(String[] args) {
      LQPConfiguration.initialize();
        ArrayList<InfinispanManager> clusters = new ArrayList<InfinispanManager>();
        clusters.add(InfinispanClusterSingleton.getInstance().getManager());  //must add because it is used from the rest of the system

        //Crucial for joining infinispan cluster
        for(InfinispanManager cluster : clusters){
            cluster.getPersisentCache("clustered");
        }
        
         //Create plugin package for upload (id,class name, jar file path, xml configuration)

        /*PluginPackage plugin = new PluginPackage();*/

        //upload plugin
        //PluginManager.uploadPlugin(plugin);

        //distributed deployment  ( plugin id, cache to install, events)
        //PluginManager.deployPlugin();
        
        /*Start putting values to the cache */

	/*Cleanup and close....*/
	//        InfinispanClusterSingleton.getInstance().getManager().stopManager();

    }
}
