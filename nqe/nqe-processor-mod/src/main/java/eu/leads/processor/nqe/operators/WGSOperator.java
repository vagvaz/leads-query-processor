package eu.leads.processor.nqe.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.nqe.operators.mapreduce.WGSMapper;
import eu.leads.processor.nqe.operators.mapreduce.WGSReducer;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 9/26/14.
 */
public class WGSOperator extends MapReduceOperator {
   private Cache inputCache = (Cache) manager.getPersisentCache(getInput());
   private JsonArray attributesArray;
   public WGSOperator(Node com, InfinispanManager persistence, Action action) {
      super(com,persistence,action);
      attributesArray = new JsonArray();
      attributesArray.add("url");
      attributesArray.add("links");
      attributesArray.add("sentiment");
      attributesArray.add("pagerank");
   }
   @Override
   public void init(JsonObject config) {
      super.init(config);
      init_statistics(this.getClass().getCanonicalName());
   }

   @Override
   public void run() {
      int count = 0;
      inputCache = (Cache) manager.getPersisentCache(getName()+".iter0");
      inputCache.put(conf.getString("url"),conf.getString("url"));
      for ( count = 0; count < conf.getInteger("depth"); count++) {
         inputCache = (Cache)manager.getPersisentCache(getName()+".iter"+String.valueOf(count));
         JsonObject jobConfig = new JsonObject();
         jobConfig.putString("iteration",String.valueOf(count));
         jobConfig.putString("depth",String.valueOf(conf.getInteger("depth")));
         jobConfig.putArray("attributes",attributesArray);
         if(count < conf.getInteger("depth")){
            jobConfig.putString("output",getName()+".iter"+String.valueOf(count+1));
         }
         else
         {
            jobConfig.putString("output","");
         }
         conf.putString("realOutput",getOutput());
         setMapper(new WGSMapper(jobConfig.toString()));
         setReducer(new WGSReducer(jobConfig.toString()));
         inputCacheName = inputCache.getName();
         intermediateCacheName = inputCacheName+".intermediate";
         super.init(null);
         super.run();

      }
   }

   @Override
   public void execute() {
      super.execute();
   }

   @Override
   public void cleanup() {
      super.cleanup();
   }
}
