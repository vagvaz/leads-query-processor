package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.common.utils.ProfileEvent;
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
    private ProfileEvent event;
    public SyncPutRunnable(){
        logger = LoggerFactory.getLogger(SyncPutRunnable.class);
        event = new ProfileEvent("SyncPutInit",logger);
    }
    public SyncPutRunnable(BasicCache cache,Object key,Object value){
        logger = LoggerFactory.getLogger(SyncPutRunnable.class);
        event = new ProfileEvent("SyncPutInit",logger);
        this.cache=cache;
        this.key = key;
        this.value = value;
    }
    @Override public void run() {
        event.start("SyncPut");
        if(key != null && value != null) {
            boolean done = false;
            while (!done) {
                try {

                    cache.put(key, value);
                    done = true;

                } catch (Exception e) {
                    done = false;
                    System.err.println(
                        "puting key " + key + " into " + cache.getName() + " failed for " + e
                            .getClass().toString());
                    logger.error("puting key " + key + " into " + cache.getName() + " failed for " + e
                            .getClass().toString());
                    PrintUtilities.logStackTrace(logger, e.getStackTrace());
                }

            }
        }
        EnsembleCacheUtils.addRunnable(this);
        event.end();
    }

    public void setParameters(BasicCache cache,Object key, Object value){
        this.cache=cache;
        this.key = key;
        this.value = value;
    }


}
