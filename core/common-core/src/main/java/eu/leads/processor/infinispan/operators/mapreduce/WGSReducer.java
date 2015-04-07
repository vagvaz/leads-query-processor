package eu.leads.processor.infinispan.operators.mapreduce;


import eu.leads.processor.infinispan.LeadsCollector;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.infinispan.LeadsReducer;
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
      imanager = InfinispanClusterSingleton.getInstance().getManager();
      depth = conf.getInteger("depth");
      iteration = conf.getInteger("iteration");
      outputCache = (Cache<String, String>) imanager.getPersisentCache(conf.getString("realOutput"));
   }
   @Override
   public void reduce(String reducedKey, Iterator<String> iter,LeadsCollector collector) {
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

         collector.emit(Integer.toString(iteration),result.toString());
//         outputCache.put(Integer.toString(iteration),result.toString());
//      System.err.println(Integer.toString(iteration) + " ------------------------- cache mememres ------------------------ ");
//       PrintUtilities.printList(outputCache.getAdvancedCache().getRpcManager().getMembers());
//
//       System.err.println(Integer.toString(iteration) + " -++++++++++++++++++++++++++++man mememres ------------------------ ");
//        PrintUtilities.printList(imanager.getMembers());

//       System.err.println(Integer.toString(iteration) + " ==========================================");
//       System.err.println("Just written " + outputCache.get(Integer.toString(iteration)));
//      System.out.println(outputCache.getName() + " new depths " + outputCache.size() + " with " + resultArray.size() + "links");
//      return null;
   }
}
