package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.LeadsListener;
import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by vagvaz on 6/4/14.
 */
public class AddListenerCallable<K, V> implements DistributedCallable<K, V, Void>, Serializable {
    private static final long serialVersionUID = 8331682008912636780L;
    private final String cacheName;
    private final Object listner;
    //    private final Configuration configuration;
    private transient Cache<K, V> cache;

    public AddListenerCallable(String cacheName, Object listener) {
        this.cacheName = cacheName;
        this.listner = listener;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Void call() throws Exception {
        try {
//            System.err.println("ADDING " + listner.getClass().toString() + " to " + cache.getName());
            boolean toadd = true;
            if (cache.getCacheManager().cacheExists(cacheName)) {
                if (cache.getAdvancedCache().getRpcManager().getMembers().contains(cache.getCacheManager().getAddress())) {
                    if (listner instanceof LeadsListener) {
                        LeadsListener leadsListener = (LeadsListener) listner;
                        for (Object l : cache.getListeners()) {
                            if (l instanceof LeadsListener) {
                                LeadsListener listener = (LeadsListener) l;
                                if (listener.getClass().getCanonicalName().equals(leadsListener.getClass().getCanonicalName())) {
                                    toadd = false;
                                }
                            }
                        }
                        leadsListener.initialize(InfinispanClusterSingleton.getInstance().getManager(), null);

                    }
                    if (toadd) {
                        cache.getCacheManager().getCache(cacheName).addListener(listner);
                    }
                }
            }

            for (Object l : cache.getListeners()) {
                if (l instanceof LeadsListener) {
                    LeadsListener listener = (LeadsListener) l;
                    System.err.println(
                        listner.getClass().toString() + ": " + cache.getCacheManager().getAddress().toString() + " " + cache.getName() + " LEAD ID " + listener.getId());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
