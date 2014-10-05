package eu.leads.processor.infinispan.operators.mapreduce;


import eu.leads.processor.common.infinispan.ClusterInfinispanManager;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.LeadsReducer;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Iterator;

/**
 * Created by vagvaz on 9/26/14.
 */
public class WGSReducer extends LeadsReducer<String, String> {
   private Cache<String, String> outputCache;
   private Integer depth;
   private Integer iteration;
   transient protected InfinispanManager imanager;
   public WGSReducer(String configString) {
      super(configString);
   }

   public void initialize(){
      isInitialized = true;
      super.initialize();
      imanager = new ClusterInfinispanManager(manager);
      depth = conf.getInteger("depth");
      iteration = conf.getInteger("iteration");
      outputCache = (Cache<String, String>) imanager.getPersisentCache(conf.getString("realOutput"));
   }
   @Override
   public String reduce(String reducedKey, Iterator<String> iter) {
      if(!isInitialized)
         initialize();
      progress();
      JsonArray resultArray = new JsonArray();
      while(iter.hasNext())
      {
         resultArray.add(iter.next());
      }

      JsonObject result = new JsonObject();

      result.putArray("result",resultArray);

         outputCache.put(Integer.toString(iteration),result.toString());

      System.out.println("new depths " + outputCache.size() + " with " + resultArray.size() + "links");
      return null;
   }
}
