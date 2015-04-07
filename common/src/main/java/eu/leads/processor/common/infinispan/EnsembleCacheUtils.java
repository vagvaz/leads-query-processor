package eu.leads.processor.common.infinispan;

import org.infinispan.commons.api.BasicCache;

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
         }catch(NullPointerException npe){
           isok = true;
           System.out.println("NPE: cache " + cache.toString() + " \nkey: " + key.toString()+" value: " +
                                value.toString()+"\n-----");
         }
         catch (Exception e) {
            isok = false;
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
