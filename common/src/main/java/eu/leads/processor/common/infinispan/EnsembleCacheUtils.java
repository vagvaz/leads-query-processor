package eu.leads.processor.common.infinispan;

import java.util.Map;

/**
 * Created by vagvaz on 3/7/15.
 */
public class EnsembleCacheUtils {

   public static void putToCache(Map cache, Object key, Object value){
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
