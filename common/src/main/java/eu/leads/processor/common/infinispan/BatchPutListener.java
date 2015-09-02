package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.LeadsListener;
import eu.leads.processor.core.Tuple;
import org.infinispan.Cache;
import org.infinispan.commons.util.concurrent.FutureListener;
import org.infinispan.commons.util.concurrent.NotifyingFuture;
import org.infinispan.context.Flag;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

/**
 * Created by vagvaz on 8/30/15.
 */
@Listener(sync = true,primaryOnly = true,clustered = false)
public class BatchPutListener implements LeadsListener {
    private transient InfinispanManager manager;
    private String compressedCache;
    private String targetCacheName;
    private transient Cache targetCache;
    private transient Map oldMap;
    private transient ConcurrentMap<NotifyingFuture<Void>,NotifyingFuture<Void>> futures;
    private transient Object mutex = null;

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
        mutex =  new Object();
        if(conf != null) {
            if (conf.containsField("target")) {
                targetCacheName = conf.getString("target");
            }
        }
        targetCache = (Cache) manager.getPersisentCache(targetCacheName);
        oldMap = null;
        futures = new ConcurrentHashMap<>();

    }

    @Override public void initialize(InfinispanManager manager) {
        this.initialize(manager,null);
    }

    @Override public String getId() {
        return BatchPutListener.class.toString();
    }

    @CacheEntryCreated
    public void created(CacheEntryCreatedEvent event){
        if(event.isPre()){
            return;
        }
        batchPut(event.getKey(),event.getValue());
    }

    private void batchPut(Object key, Object value) {
//        System.out.println("RUN BatchPut");
        byte[] b = (byte[]) value;
        if(b.length == 1 && b[0]==-1){
            waitForPendingPuts();
            return;
        }
        TupleBuffer tupleBuffer =  new TupleBuffer((byte[])value);
        Map tmpb = tupleBuffer.getBuffer();
        for(Map.Entry<Object,Tuple> entry : tupleBuffer.getBuffer().entrySet()){
            tmpb.put(entry.getKey(), entry.getValue());
            if(tmpb.size() > 10) {
                targetCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES)
                    .putAll(tmpb);//entry.getKey(), entry.getValue());
            }
        }
        if(tmpb.size() > 0){
            targetCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES)
                .putAll(tmpb);
        }

        tupleBuffer.getBuffer().clear();
        tupleBuffer=null;
//            oldMap = tupleBuffer.getBuffer();
////            synchronized (mutex) {
//                NotifyingFuture f = targetCache.getAdvancedCache().withFlags(Flag.IGNORE_RETURN_VALUES)
//                    .putAllAsync(oldMap).attachListener(new FutureListener() {
//                        @Override public void futureDone(Future future) {
////                            synchronized (mutex) {
//                                futures.remove(future);
////                            }
//                        }
//                    });
//                futures.put(f, f);
//            }
    }

    @CacheEntryModified
    public void modified(CacheEntryModifiedEvent event) {
        if(event.isPre()){
            return;
        }
        batchPut(event.getKey(),event.getValue());
    }

    public void waitForPendingPuts(){
        if(futures == null)
            return;

        boolean isok = false;
        boolean retry = false;
        while(!isok){
            try{
                for(Map.Entry<NotifyingFuture<Void>,NotifyingFuture<Void>> entry : futures.entrySet()){
                    entry.getKey().get();
                }
                if(retry){

                }
                isok = true;
            }
            catch (Exception e){
                System.err.println("Exception " +  e.getClass().toString() + " in BatchPUtListener waitForPendingPuts" + e.getMessage());
                e.printStackTrace();
                retry = true;
                isok = true;
            }
        }
    }
}
