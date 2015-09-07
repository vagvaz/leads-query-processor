package eu.leads.processor.common.infinispan;

/**
 * Created by vagvaz on 5/23/14.
 */

import eu.leads.processor.common.LeadsListener;
import eu.leads.processor.common.utils.PrintUtilities;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.lifecycle.ComponentStatus;

import java.io.Serializable;
import java.util.Set;

public class StopCacheCallable<K, V> implements DistributedCallable<K, V, Void>, Serializable {
    private static final long serialVersionUID = 8331682008912636781L;
    private final String cacheName;
    private transient Cache<K, V> cache;


    public StopCacheCallable(String cacheName) {
        this.cacheName = cacheName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void call() throws Exception {
        System.err.println("\nTry to clear stop " + cacheName + " from " +cache.getCacheManager().getAddress().toString());
        if(cache.getCacheManager().cacheExists(cacheName))
        {
            System.err.println(
                "StopCallable " + cacheName + " from " + cache.getCacheManager().getAddress()
                    .toString());
            if(cache.getCacheManager().cacheExists(cache.getName()+".compressed")){
                System.err.println("Stopping " + cache.getName() + ".compressed");
                Cache compressed = cache.getCacheManager().getCache(cache.getName()+".compressed");
                for(Object l : compressed.getListeners()){
                    //                  if(l instanceof PluginHandlerListener){
                    //                      compressed.removeListener(l);
                    //                  }
                    if(l instanceof LeadsListener){
                        ((LeadsListener)l).close();
                        compressed.removeListener(l);
                        l = null;
                    }
                }
                    try {
                        compressed.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).clear();

                        compressed.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).stop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

            }else{
                System.err.println("Cache " + cache.getName() +".compressed EXISTS?=" + cache
                    .getCacheManager().cacheExists(cache.getName() + ".compressed"));
            }
                System.err
                    .println(
                        "Stopped " + cacheName + " from " + cache.getCacheManager().getAddress()
                            .toString() + "\n");
                try{
                    cache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).clear();
                    cache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).stop();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnvironment(Cache<K, V> cache, Set<K> inputKeys) {
        this.cache = cache.getCacheManager().getCache(cacheName);
    }

}
