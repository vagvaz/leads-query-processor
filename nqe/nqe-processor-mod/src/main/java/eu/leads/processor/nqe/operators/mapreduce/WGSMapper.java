package eu.leads.processor.nqe.operators.mapreduce;

import eu.leads.processor.core.LeadsMapper;
import eu.leads.processor.core.Tuple;
import org.infinispan.Cache;
import org.infinispan.distexec.mapreduce.Collector;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vagvaz on 9/26/14.
 */
public class WGSMapper extends LeadsMapper<String, String, String, String> {
   public WGSMapper(String configString) {
      super(configString);
   }

   protected transient List<String> attributes;
   protected transient Cache inputCache;
   protected transient Cache outputCache;
   protected transient int depth;
   protected transient int iteration;
   private void intialize() {
      isInitialized = true;
      super.initialize();
      iteration = conf.getInteger("iteration");
      depth = conf.getInteger("depth");
      inputCache = manager.getCache(conf.getString("inputCache"));
      if(iteration < depth){
         outputCache = manager.getCache(conf.getString("outputCache"));
      }
      else{
         outputCache = null;
      }

      JsonArray array = conf.getArray("attributes");
      Iterator<Object> iterator = array.iterator();
      attributes = new ArrayList<String>(array.size());
      while(iterator.hasNext()){
         attributes.add((String) iterator.next());
      }
   }

   @Override
   public void map(String key, String value, Collector<String, String> collector) {
      String jsonString = (String) inputCache.get(key);
      if (jsonString==null || jsonString.equals("")){
         return;
      }
      Tuple t = new Tuple(jsonString);
      handlePagerank(t);
      JsonObject result = new JsonObject();
      result.putString("url", t.getAttribute("url"));
      result.putString("pagerank", t.getAttribute("pagerank"));
      result.putString("sentiment", t.getAttribute("sentiment"));
      result.putValue("links",t.getGenericAttribute("links"));
      collector.emit(String.valueOf(iteration),result.toString());
      if(outputCache != null){
         JsonArray links = result.getArray("links");
         Iterator<Object> iterator = links.iterator();
         while(iterator.hasNext()){
            String link = (String) iterator.next();
            outputCache.put(link,link);
         }
      }

   }
}
