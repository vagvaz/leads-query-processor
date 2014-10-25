package eu.leads.processor.common.utils;

import eu.leads.processor.common.infinispan.InfinispanCluster;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import junit.framework.TestCase;

import java.util.concurrent.ConcurrentMap;

public class InfinispanClusterTest extends TestCase {
    public static void main(String[] args) {
        LQPConfiguration.initialize();
        //        InfinispanCluster cluster = WeldContext.INSTANCE.getBean(InfinispanCluster.class);
        //
        ////        cluster.initialize();
        //        InfinispanManager man = cluster.getManager();
        //
        //        ConcurrentMap map = man.getPersisentCache("testCache");
        //        map.put("1","11");
        //        map.put("2","22");
        //        InfinispanCluster cluster2 = WeldContext.INSTANCE.getBean(InfinispanCluster.class);
        //
        //        PrintUtilities.printMap(map);
        //        ConcurrentMap map2 = cluster2.getManager().getPersisentCache("testCache");
        //        map2.put("3","33");
        //        PrintUtilities.printMap(map2);
        //        System.out.println("cl");
        //        PrintUtilities.printList(cluster.getManager().getMembers());
        //        System.out.println("cl2");
        //        PrintUtilities.printList(cluster2.getManager().getMembers());
        InfinispanManager manager = InfinispanClusterSingleton.getInstance().getManager();
        ConcurrentMap map = manager.getPersisentCache("testCache");
        map.put("1","1");
        map.put("2","2");
        PrintUtilities.printMap(map);
        manager.stopManager();
    }

    public void testGetManager() throws Exception {
        //        LQPConfiguration.initialize();
        //        InfinispanCluster cluster = WeldContext.INSTANCE.getBean(InfinispanCluster.class);
        //
        ////        cluster.initialize();
        //        InfinispanManager man = cluster.getManager();
        //
        //        ConcurrentMap map = man.getPersisentCache("testCache");
        //        map.put("1","11");
        //        map.put("2","22");
        //        InfinispanCluster cluster2 = WeldContext.INSTANCE.getBean(InfinispanCluster.class);
        //
        //        PrintUtilities.printMap(map);
        //        ConcurrentMap map2 = cluster2.getManager().getPersisentCache("testCache");
        //        map2.put("3","33");
        //        PrintUtilities.printMap(map2);
        //        System.out.println("cl");
        //        PrintUtilities.printList(cluster.getManager().getMembers());
        //        System.out.println("cl2");
        //        PrintUtilities.printList(cluster2.getManager().getMembers());
    }
}
