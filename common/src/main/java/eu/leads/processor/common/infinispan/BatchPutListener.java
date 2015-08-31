package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.LeadsListener;
import org.infinispan.Cache;
import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

/**
 * Created by vagvaz on 8/30/15.
 */
@Listener(sync = false,primaryOnly = true,clustered = false)
public class BatchPutListener implements LeadsListener {
    private transient InfinispanManager manager;
    private String compressedCache;
    private String targetCacheName;
    private transient Cache targetCache;
    private transient Map oldMap;
    private transient NotifyingFuture<Void> future;

    public BatchPutListener(String compressedCache,String targetCacheName){
        this.compressedCache = compressedCache;
        this.targetCacheName = targetCacheName;
    }
    @Override public InfinispanManager getManager() {
        return manager;
    }

    @Override public void setManager(InfinispanManager manager) {
        this.manager = manager;
    }

    @Override public void initialize(InfinispanManager manager, JsonObject conf) {
        this.manager = manager;
        if(conf != null) {
            if (conf.containsField("target")) {
                targetCacheName = conf.getString("target");
                targetCache = (Cache) manager.getPersisentCache(targetCacheName);
            }
        }
        oldMap = null;
        future = null;

    }

    @Override public void initialize(InfinispanManager manager) {
        this.initialize(manager,null);
    }

    @Override public String getId() {
        return BatchPutListener.class.toString();
    }

    @CacheEntryCreated
    public void created(CacheEntryCreatedEvent event){
        if(!event.isPre()){
            return;
        }
        batchPut(event.getKey(),event.getValue());
    }

    private void batchPut(Object key, Object value) {
        TupleBuffer tupleBuffer = (TupleBuffer) value;
        if(future == null) {
            oldMap = tupleBuffer.getBuffer();
            future = targetCache.putAllAsync(oldMap);
        }else{
            boolean isok = false;
            boolean retry = false;
            waitForPendingPuts();
            oldMap = tupleBuffer.getBuffer();
            future = targetCache.putAllAsync(oldMap);
        }
    }

    @CacheEntryModified
    public void modified(CacheEntryModifiedEvent event) {
        if(!event.isPre()){
            return;
        }
        batchPut(event.getKey(),event.getValue());
    }

    public void waitForPendingPuts(){
        if(future == null)
            return;

        boolean isok = false;
        boolean retry = false;
        while(!isok){
            try{
                if(retry){
                    future =  targetCache.putAllAsync(oldMap);
                }
                future.get();
                isok = true;
            }
            catch (Exception e){
                System.err.println("Exception " +  e.getClass().toString() + " in BatchPUtListener waitForPendingPuts" + e.getMessage());
                e.printStackTrace();
                retry = true;
                isok = false;
            }
        }
    }
}
