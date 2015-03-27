package eu.leads.processor.common.infinispan;

import org.infinispan.commons.api.BasicCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vagvaz on 3/7/15.
 */
public class EnsembleCacheUtils {

   static Logger log  = LoggerFactory.getLogger(EnsembleCacheUtils.class);
   public static void putToCache(BasicCache cache, Object key, Object value){
      boolean isok = false;
      while(!isok) {
         try {
           if(cache != null) {
             cache.put(key, value);
             isok = true;
           }
           else {
             log.error("CACHE IS NULL IN PUT TO CACHE for " + key.toString() + " " + value.toString());
             isok = true;
           }
         }catch(NullPointerException npe){
           isok = false;
           log.error("NPE: cache " + cache.toString() + " \nkey: " + key.toString() + " value: " +
                       value.toString() + "\n-----");
           System.err.println("NPE: cache " + cache.toString() + " \nkey: " + key.toString()+" value: " +
                                value.toString()+"\n-----");
         }
         catch (Exception e) {
            isok = false;
            log.error("PUT TO CACHE " + e.getMessage());
            System.err.println("PUT TO CACHE " + e.getMessage());
         }
      }
   }

  public static <KOut> void putIfAbsentToCache(BasicCache cache, KOut key, KOut value) {
    boolean isok = false;
    while(!isok) {
      try {
        cache.put(key, value);
        isok =true;
      } catch (Exception e) {
        isok = false;
        System.err.println("PUT TO CACHE " + e.getMessage());
      }
    }
  }
}
