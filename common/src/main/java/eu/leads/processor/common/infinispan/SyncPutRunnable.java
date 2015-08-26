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
//    File file = new File("before_put.txt");
//    FileOutputStream fos;
//    PrintStream ps = new PrintStream(fos);
//    File file_after = new File("after_put.txt");
//    FileOutputStream fos_after;
//    PrintStream ps_after = new PrintStream(fos_after);

    public SyncPutRunnable(){
        logger = LoggerFactory.getLogger(SyncPutRunnable.class);
        event = new ProfileEvent("SyncPutInit",logger);
//        try {
//            fos = new FileOutputStream(file);
//            fos_after = new FileOutputStream(file_after);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }
    public SyncPutRunnable(BasicCache cache,Object key,Object value){
        logger = LoggerFactory.getLogger(SyncPutRunnable.class);
        event = new ProfileEvent("SyncPutInit",logger);
        this.cache=cache;
        this.key = key;
        this.value = value;
//        try {
//        fos = new FileOutputStream(file);
//        fos_after = new FileOutputStream(file_after);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }
    @Override public void run() {
        event.start("SyncPut");
        if(key != null && value != null) {
            boolean done = false;
            while (!done) {
                try {
//                    System.setOut(ps);
//                    System.err.println("BEF PUT-----Key: " + key + "--Size:" + value.toString().length());
                    System.out.println("BEF PUT-----Key: " + key + "--Size:" + value.toString().length());
                    cache.put(key, value);
//                    System.setOut(ps_after);
//                    System.err.println("AFT PUT-----Key: " + key + "--Size:" + value.toString().length());
                    System.out.println("AFT PUT-----Key: " + key + "--Size:" + value.toString().length());
                    done = true;
                } catch (Exception e) {
                    done = false;
                    System.err.println(
                        "puting key " + key + " into " + cache.getName() + " failed for " + e
                            .getClass().toString() + " " + e.getMessage());
                    logger.error("puting key " + key + " into " + cache.getName() + " failed for " + e
                            .getClass().toString() + " " + e.getMessage());
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
