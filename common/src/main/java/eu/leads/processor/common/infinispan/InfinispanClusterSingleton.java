package eu.leads.processor.common.infinispan;


/**
 * Created by vagvaz on 6/3/14.
 */

/**
 * A simple utility class for Singleton in order to simplify the bootstrapping and use of infinispan throughout the project
 */
public class InfinispanClusterSingleton {
    private static final InfinispanClusterSingleton instance = new InfinispanClusterSingleton();
    private InfinispanCluster cluster;

    private InfinispanClusterSingleton() {
        cluster = new InfinispanCluster(CacheManagerFactory.createCacheManager());
        cluster.getManager().getPersisentCache("clustered");
    }

    public static InfinispanClusterSingleton getInstance() {
        return instance;
    }

    public InfinispanCluster getCluster() {
        return this.cluster;
    }

    public InfinispanManager getManager() {
        return instance.getCluster().getManager();
    }
}
