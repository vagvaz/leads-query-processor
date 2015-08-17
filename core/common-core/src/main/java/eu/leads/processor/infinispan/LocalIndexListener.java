package eu.leads.processor.infinispan;

import eu.leads.processor.core.BerkeleyDBIndex;
import eu.leads.processor.common.LeadsListener;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Tuple;
import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 16/07/15.
 */
@Listener(sync = true,primaryOnly = true,clustered = false)
public class LocalIndexListener implements LeadsListener {

    transient private volatile Object mutex ;
    String cacheName;
    transient BerkeleyDBIndex index;
    transient Cache targetCache;
    transient Cache keysCache;
    transient Cache dataCache;
    transient Logger log;

    public LocalIndexListener(InfinispanManager manager, String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public BerkeleyDBIndex getIndex() {
        return index;
    }

    public void setIndex(BerkeleyDBIndex index) {
        this.index = index;
    }

    public Cache getKeysCache() {
        return keysCache;
    }

    public void setKeysCache(Cache keysCache) {
        this.keysCache = keysCache;
    }

    public Cache getDataCache() {
        return dataCache;
    }

    public void setDataCache(Cache dataCache) {
        this.dataCache = dataCache;
    }

    @CacheEntryCreated
    public void created(CacheEntryCreatedEvent event) {
        if (event.isPre()) {
            return;
        }

        //        if(event.getKey() instanceof ComplexIntermediateKey) {
            ComplexIntermediateKey key = (ComplexIntermediateKey) event.getKey();
//        System.err.println("PREKey created " + event.getKey() + " key " + key.getKey() + " " + key.getNode() + " " + key.getSite() + " " + key.getCounter());
        ((Tuple)event.getValue()).setAttribute("__complexKey",key.asString());
        index.put(key.getKey(), event.getValue());
//            synchronized (mutex){
//                mutex.notifyAll();
//            }
//        }

    }

    @CacheEntryModified
    public void modified(CacheEntryModifiedEvent event) {
//        System.err.println("local " + event.isOriginLocal() + " " + event.isCommandRetried() + " " + event.isCreated() + " " + event.isPre());
        if (event.isPre()) {
//            ComplexIntermediateKey key = (ComplexIntermediateKey) event.getKey();
//            System.err.println("PREKey modified " + event.getKey() + " key "  + key.getKey() + " " + key.getNode() + " " + key.getSite() + " " + key.getCounter());
            return;
        }
//        if(event.getKey() instanceof ComplexIntermediateKey) {
            ComplexIntermediateKey key = (ComplexIntermediateKey) event.getKey();
//            System.err.println("AFTERValue modified " + event.getKey() + " key " + key.getKey() + " " + key.getNode() + " " + key.getSite() + " " + key.getCounter());
            index.put(key.getKey(),event.getValue());
//            synchronized (mutex){
//                mutex.notifyAll();
//            }
//        }
    }

    @Override public InfinispanManager getManager() {
        return null;
    }

    @Override public void setManager(InfinispanManager manager) {

    }

    @Override public void initialize(InfinispanManager manager,JsonObject conf) {
        mutex = new Object();
        this.targetCache = (Cache) manager.getPersisentCache(cacheName);
        this.keysCache = manager.getLocalCache(cacheName+".index.keys");
        this.dataCache = manager.getLocalCache(cacheName+".index.data");
//        this.index = new IntermediateKeyIndex(keysCache,dataCache);
        this.index = new BerkeleyDBIndex(StringConstants.TMPPREFIX+"/bdb/"+ manager
            .getCacheManager().getAddress().toString(),cacheName+".index");
        log = LoggerFactory.getLogger(LocalIndexListener.class);

    }

    @Override public void initialize(InfinispanManager manager) {
        initialize(manager,null);
    }

    @Override public String getId() {
        return this.getClass().toString();
    }

    void waitForAllData(){
//        synchronized (mutex){
        System.err.println("get the size of target");
//            int size  = targetCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).size();
            int size = targetCache.getAdvancedCache().getDataContainer().size();
            System.err.println("LOCALINDEX: dataCache size " + dataCache.size() + " target Cache size local data " +size  );
            log.error("LOCALINDEX: dataCache size " + dataCache.size() + " target Cache size local data " +size  );
            while( size != dataCache.size()){
                System.err.println("LOCALINDEX: dataCache size " + dataCache.size() + " target Cache size local data " +size  );
                log.error("LOCALINDEX: dataCache size " + dataCache.size() + " target Cache size local data " +size  );
//                try {
//                    mutex.wait(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }
}
