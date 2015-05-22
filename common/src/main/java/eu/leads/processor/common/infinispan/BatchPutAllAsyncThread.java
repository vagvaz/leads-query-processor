package eu.leads.processor.common.infinispan;

import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.util.concurrent.NotifyingFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by vagvaz on 20/05/15.
 */
public class BatchPutAllAsyncThread extends Thread{

    private final Map<String, Map<Object, Object>> objects;
    private final Map<String, BasicCache> caches;
    private List<NotifyingFuture> futures;

    public BatchPutAllAsyncThread(Map<String, BasicCache> caches,
        Map<String, Map<Object, Object>> objects) {
        this.caches = caches;
        this.objects = objects;
        futures = new ArrayList<>();
    }

    @Override public void run() {
//        super.run();
        for(Map.Entry<String,Map<Object,Object>> entry : objects.entrySet()){
            BasicCache cache = caches.get(entry.getKey());
            futures.add(cache.putAllAsync(entry.getValue()));
        }

        caches.clear();
        objects.clear();
        for(NotifyingFuture future : futures){
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
