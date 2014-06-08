package test;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.plugins.EventType;
import eu.leads.processor.plugins.PluginManager;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.Map;

/**
 * Created by vagvaz on 6/4/14.
 */
public class LocalTest {

    public static void main(String[] args) {
        String targetCache = "mycache";
        int sleepingPeriod = 20;
        //Important Call to initialize System Configuration
        LQPConfiguration.initialize();

        //Set CacheMode to get LcoalImplementation only
        LQPConfiguration.getConf().setProperty("processor.infinispan.mode","local");
	
	/*Configuration for plugin*/

	/*deploy plugin locally*/
        /*PluginManager.deployLocalPlugin(plugin,config,"mytargetCacheName",
					  EventType.CREATEANDMODIFY,
					 InfinispanClusterSingleton.getInstance().getManager());*/

	
	/*Start putting values to the cache */

	/*Cleanup and close....*/
	//        InfinispanClusterSingleton.getInstance().getManager().stopManager();
    }
}