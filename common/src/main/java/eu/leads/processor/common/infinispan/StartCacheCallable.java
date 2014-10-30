package eu.leads.processor.common.infinispan;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.TransactionMode;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by vagvaz on 5/23/14.
 */
public class StartCacheCallable<K, V> implements DistributedCallable<K, V, Void>, Serializable {
    private static final long serialVersionUID = 8331682008912636780L;
    private final String cacheName;
    //    private final Configuration configuration;
    private transient Cache<K, V> cache;


    public StartCacheCallable(String cacheName) {
        this.cacheName = cacheName;
        //        this.configuration = configuration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void call() throws Exception {
        //        cache.getCacheManager().defineConfiguration(cacheName);
        //        cache.getCacheManager().defineConfiguration(cacheName, new ConfigurationBuilder().clustering().cacheMode(CacheMode.DIST_ASYNC).async().l1().lifespan(100000L).hash().numOwners(3).build());


        EmbeddedCacheManager manager = cache.getCacheManager();
        manager.defineConfiguration(cacheName, new ConfigurationBuilder()
                .clustering()
                .cacheMode(CacheMode.DIST_SYNC)
                .hash().numOwners(3)
                .compatibility().enable().persistence().addSingleFileStore().location("/tmp/"+manager.getAddress().toString())
                .build());
        Cache cache = manager.getCache(cacheName);

//        Cache newCache = manager.getCache(cacheName,true);
//        if(newCache != null){
//            if(!cache.getCacheConfiguration().clustering().cacheMode().isClustered())
//                System.err.println("Cache " + cacheName + "is not clustered");
//                System.err.println(cache.getCacheConfiguration().clustering().cacheModeString() +  cache.getCacheConfiguration().clustering().cacheModeString());
//        }
//        if(!manager.cacheExists(cacheName)) {
//            cache.getCacheManager().defineConfiguration(cacheName, cache.getCacheManager()
//                    .getCacheConfiguration("clustered"));
//            cache.getCacheManager().getCache(cacheName); // start the cache
//            return null;
//        }
//        else{
//            Cache newCache = manager.getCache(cacheName,true);
//            if(!newCache.getAdvancedCache().getStatus().equals(ComponentStatus.RUNNING)){
//                newCache.start();
//            }
//        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnvironment(Cache<K, V> cache, Set<K> inputKeys) {
        this.cache = cache;
    }

}
