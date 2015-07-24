package eu.leads.processor.infinispan;

import eu.leads.processor.common.LeadsListener;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.infinispan.IntermediateKeyIndex;
import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.vertx.java.core.json.JsonObject;

import java.io.FileWriter;
import java.util.Map;

/**
 * Created by vagvaz on 16/07/15.
 */
@Listener(sync = true,primaryOnly = true,clustered = false)
public class LocalIndexListener implements LeadsListener {

    String cacheName;
    transient IntermediateKeyIndex index;
    transient Cache keysCache;
    transient Cache dataCache;
    public LocalIndexListener(InfinispanManager manager, String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public IntermediateKeyIndex getIndex() {
        return index;
    }

    public void setIndex(IntermediateKeyIndex index) {
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
        if(event.getKey() instanceof ComplexIntermediateKey) {
            ComplexIntermediateKey key = (ComplexIntermediateKey) event.getKey();
            index.put(key.getKey(),event.getValue());
        }

    }

    @CacheEntryModified
    public void modified(CacheEntryModifiedEvent event) {
        if (event.isPre()) {
            return;
        }

        if(event.getKey() instanceof ComplexIntermediateKey) {
            ComplexIntermediateKey key = (ComplexIntermediateKey) event.getKey();
            System.err.println("Value modified key " + key.getKey() + " " + key.getNode() + " " + key.getSite() + " " + key.getCounter());
            index.put(key.getKey(),event.getValue());
        }
    }

    @Override public InfinispanManager getManager() {
        return null;
    }

    @Override public void setManager(InfinispanManager manager) {

    }

    @Override public void initialize(InfinispanManager manager,JsonObject conf) {
        this.keysCache = manager.getLocalCache(cacheName+".index.keys");
        this.dataCache = manager.getLocalCache(cacheName+".index.data");
        this.index = new IntermediateKeyIndex(keysCache,dataCache);
    }

    @Override public void initialize(InfinispanManager manager) {
        initialize(manager,null);
    }

    @Override public String getId() {
        return this.getClass().toString();
    }
}
