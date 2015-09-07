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
        System.err.println("\n\n\n\n\n\nTry to remove " + cacheName + " from " +cache.getCacheManager().getAddress().toString());
        if(cache.getCacheManager().cacheExists(cacheName))
        {
            System.err.println(
                "Removing " + cacheName + " from " + cache.getCacheManager().getAddress().toString());
            cache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).clear();
            //           cache.getAdvancedCache().withFlags(Flag.)
            if(cache.getCacheManager().cacheExists(cache.getName()+".compressed")){
                System.err.println("Stopping " + cache.getName()+".compressed");
                Cache compressed = cache.getCacheManager().getCache(cache.getName()+".compressed");
                compressed.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).clear();
                for(Object l : compressed.getListeners()){
                    //                  if(l instanceof PluginHandlerListener){
                    //                      compressed.removeListener(l);
                    //                  }
                    if(l instanceof LeadsListener){
                        ((LeadsListener)l).close();
                        l = null;
                    }
                }
//                ComponentStatus status = compressed.getStatus();
//                if(!status.isTerminated()){

                    try {
                        if(cache.getCacheManager().isRunning(compressed.getName())) {
                            compressed.getAdvancedCache().stop();
                            cache.getCacheManager().removeCache(compressed.getName());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

            }else{
                System.err.println("Cache " + cache.getName() +".compressed EXISTS?=" + cache
                    .getCacheManager().cacheExists(cache.getName() + ".compressed"));
            }
//            ComponentStatus status = cache.getStatus();
//            if(!status.isTerminated()){
                //              System.err.println("Clear " + cacheName + " from " + cache.getCacheManager().getAddress().toString());

                System.err
                    .println("Stopped " + cacheName + " from " + cache.getCacheManager().getAddress()
                        .toString() + "\n\n\n\n\n\n");
                //              cache.getCacheManager().removeCache(cache.getName());
                try{
                    if(cache.getCacheManager().isRunning(cacheName)) {
                        cache.getAdvancedCache().stop();
                        cache.getCacheManager().removeCache(cacheName);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
//            else{
//                System.err.println("Stopping " + cacheName + " was not allowed in " + cache.getCacheManager().getAddress().toString()+"\n\n\n\n\n\n");
//            }


        //        PrintUtilities.printCaches(cache.getCacheManager());
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
