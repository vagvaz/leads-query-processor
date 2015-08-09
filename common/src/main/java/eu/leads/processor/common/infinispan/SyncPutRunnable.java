package eu.leads.processor.common.infinispan;

import org.infinispan.commons.api.BasicCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vagvaz on 09/08/15.
 */
public class SyncPutRunnable implements Runnable {
    private BasicCache cache;
    private Object key;
    private Object value;
    private Logger logger;
    public SyncPutRunnable(){
        logger = LoggerFactory.getLogger(SyncPutRunnable.class);
    }

    public SyncPutRunnable(BasicCache cache,Object key,Object value){
        logger = LoggerFactory.getLogger(SyncPutRunnable.class);
        this.cache=cache;
        this.key = key;
        this.value = value;
    }
    @Override public void run() {
        if(key != null && value != null){
            boolean done = false;
            try{
                while(!done){
                    cache.put(key,value);
                    done = true;
                }
            }catch (Exception e){
                done = false;
                System.err.println("puting key " + key + " into " + cache.getName() + " failed for " + e.getClass().toString());
                logger.error("puting key " + key + " into " + cache.getName() + " failed for " + e.getClass().toString());
            }

        }
        EnsembleCacheUtils.addRunnable(this);
    }

    public void setParameters(BasicCache cache,Object key, Object value){
        this.cache=cache;
        this.key = key;
        this.value = value;
    }


}
