package eu.leads.processor.common.infinispan;

import eu.leads.processor.common.utils.ProfileEvent;
import org.infinispan.commons.api.BasicCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vagvaz on 3/7/15.
 */
public class EnsembleCacheUtils {
  static Logger profilerLog  = LoggerFactory.getLogger("###PROF###" +  EnsembleCacheUtils.class);
  static ProfileEvent profExecute = new ProfileEvent("Execute " + EnsembleCacheUtils.class,profilerLog);
   static Logger log  = LoggerFactory.getLogger(EnsembleCacheUtils.class);
   public static void putToCache(BasicCache cache, Object key, Object value){
     profExecute.start("putToCache");
      boolean isok = false;
      while(!isok) {
         try {
           if(cache != null) {
              if(key == null || value == null){
                 log.error("SERIOUS PROBLEM with key/value null key: " + (key==null) + " value " + (value==null) );
                 if(key != null)
                 {
                    log.error("key " + key.toString());
                 }
                 if(value != null){
                    log.error("value: " + value);
                 }
                 isok = true;
                 continue;
              }
             cache.put(key,value);

//              log.error("Successful " + key);
              isok = true;
           }
           else {
             log.error("CACHE IS NULL IN PUT TO CACHE for " + key.toString() + " " + value.toString());
             isok = true;
           }
         }catch (Exception e) {
            isok = false;

            log.error("PUT TO CACHE " + e.getMessage() + " " + key);
            log.error("key " + (key == null) + " value " + (value == null) + " cache " + (cache == null)
                        + " log " + (log == null));

            try {
               Thread.sleep(5);
            } catch (InterruptedException e1) {
               e1.printStackTrace();
            }
            System.err.println("PUT TO CACHE " + e.getMessage());
            e.printStackTrace();
           if(e.getMessage().startsWith("Cannot perform operations on ")){
             e.printStackTrace();
             System.exit(-1);
           }
         }
      }
     profExecute.end();
   }

  public static <KOut> void putIfAbsentToCache(BasicCache cache, KOut key, KOut value) {
      putToCache(cache,key,value);
//    boolean isok = false;
//    while(!isok) {
//      try {
//        cache.put(key, value);
//        isok =true;
//      } catch (Exception e) {
//        isok = false;
//        System.err.println("PUT TO CACHE " + e.getMessage());
//      }
//    }
  }
}
