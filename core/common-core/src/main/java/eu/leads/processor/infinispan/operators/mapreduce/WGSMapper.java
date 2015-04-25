package eu.leads.processor.infinispan.operators.mapreduce;

import eu.leads.processor.common.infinispan.AcceptAllFilter;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.infinispan.LeadsMapper;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.plugins.pagerank.node.DSPMNode;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
import org.infinispan.distexec.mapreduce.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
  protected transient double totalSum;
    protected transient Cache pagerankCache;
    protected transient InfinispanManager imanager;
   protected transient Logger log ;
  protected transient List<String> microclouds;
   @Override
  public  void initialize() {

     imanager = InfinispanClusterSingleton.getInstance().getManager();
      isInitialized = true;
      super.initialize();
        totalSum = -1.0;
      iteration = conf.getInteger("iteration");
      depth = conf.getInteger("depth");
      webCache = (Cache) imanager.getPersisentCache(conf.getString("webCache"));
       pagerankCache = (Cache) imanager.getPersisentCache("pagerankCache");

      if(iteration < depth){
         outputCache = (Cache) imanager.getPersisentCache(conf.getString("outputCache"));
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
      LQPConfiguration.initialize();
     log = LoggerFactory.getLogger(WGSMapper.class);
     microclouds = new ArrayList<>();
     microclouds.add("hamm5");
     microclouds.add("hamm6");
     microclouds.add("dresden2");
   }

   @Override
   public void map(String key, String value, Collector<String, String> collector) {
     if(!isInitialized)
       this.initialize();
//      String jsonString = (String) webCache.get(prefix+key);
//      if (jsonString==null || jsonString.equals("")){
//         return;
//      }
      Tuple webpage = (Tuple) webCache.get(prefix+key);
//      Tuple t = new Tuple(jsonString);
      if(webpage == null)
         return;
      Tuple t = new Tuple(webpage);
      handlePagerank(t);
      JsonObject result = new JsonObject();
      result.putString("url", t.getAttribute("url"));
      result.putString("pagerank", computePagerank(result.getString("url")));
      result.putString("sentiment", t.getGenericAttribute("sentiment").toString());
//      result.putString("micro-cluster",LQPConfiguration.getInstance().getMicroClusterName());
      int mcIndex = t.getAttribute("url").hashCode() % microclouds.size();
      result.putString("micro-cluster",microclouds.get(mcIndex));
      ArrayList<Object> linksArray = (ArrayList<Object>) t.getGenericAttribute("links");
      JsonArray array = new JsonArray();
      for(Object o : linksArray){
         log.error("ADDING TO LINKS " + o.toString());
         array.add(o.toString());
      }
      result.putValue("links", array);
      collector.emit(String.valueOf(iteration),result.toString());
      if(outputCache != null){
         if (!result.getElement("links").isArray())
         {log.error("SERIOUS ERROR links is not an array WGSMAPPER");
            return;
         }
         JsonArray links = result.getArray("links");
         Iterator<Object> iterator = links.iterator();
         while(iterator.hasNext()){
            String link = (String) iterator.next();
            log.error("Inserting into next iteration cache " + outputCache.getName() + " l " + link);
            EnsembleCacheUtils.putToCache(outputCache,link, link);
         }
      }

   }

    private String computePagerank(String url) {
        double result = 0.0;
        if(totalSum < 0){
               computeTotalSum();
           }
            DSPMNode currentPagerank = (DSPMNode) pagerankCache.get(url);
            if(currentPagerank == null || totalSum <= 0)
            {

                return Double.toString(0.0f);
            }
            result = currentPagerank.getVisitCount()/totalSum;
            return Double.toString(result);

    }

    private void computeTotalSum() {
        Cache approxSumCache = (Cache) imanager.getPersisentCache("approx_sum_cache");
        CloseableIterable<Map.Entry<String, Integer>> iterable =
                approxSumCache.getAdvancedCache().filterEntries(new AcceptAllFilter());

        for (Map.Entry<String, Integer> outerEntry : iterable) {
            totalSum += outerEntry.getValue() ;
        }
        if(totalSum > 0){
            totalSum+=1;
        }
    }
}
