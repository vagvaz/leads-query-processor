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

  protected transient String prefix;
   protected transient List<String> attributes;
   protected transient Cache webCache;
   protected transient Cache outputCache;
   protected transient int depth;
   protected transient int iteration;

  public  void initialize() {
      isInitialized = true;
      super.initialize();

      iteration = conf.getInteger("iteration");
      depth = conf.getInteger("depth");
      webCache = manager.getCache(conf.getString("webCache"));

      if(iteration < depth){
         outputCache = manager.getCache(conf.getString("outputCache"));
      }
      else{
         outputCache = null;
      }
      prefix = webCache.getName()+":";
      JsonArray array = conf.getArray("attributes");
      Iterator<Object> iterator = array.iterator();
      attributes = new ArrayList<String>(array.size());
      while(iterator.hasNext()){
         attributes.add((String) iterator.next());
      }
   }

   @Override
   public void map(String key, String value, Collector<String, String> collector) {
     if(!isInitialized)
       this.initialize();
      String jsonString = (String) webCache.get(prefix+key);
      if (jsonString==null || jsonString.equals("")){
         return;
      }
      Tuple t = new Tuple(jsonString);
      handlePagerank(t);
      JsonObject result = new JsonObject();
      result.putString("url", t.getAttribute("url"));
      result.putString("pagerank", t.getGenericAttribute("pagerank").toString());
      result.putString("sentiment", t.getGenericAttribute("sentiment").toString());
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
