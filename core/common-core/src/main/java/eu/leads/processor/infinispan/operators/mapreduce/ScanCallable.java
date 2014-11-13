package eu.leads.processor.infinispan.operators.mapreduce;

import eu.leads.processor.common.infinispan.AcceptAllFilter;
import eu.leads.processor.common.infinispan.ClusterInfinispanManager;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.math.FilterOperatorTree;
import eu.leads.processor.plugins.pagerank.node.DSPMNode;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
import org.infinispan.context.Flag;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.interceptors.locking.ClusteringDependentLogic;
import org.infinispan.versioning.VersionedCache;
import org.infinispan.versioning.impl.VersionedCacheTreeMapImpl;
import org.infinispan.versioning.utils.version.Version;
import org.infinispan.versioning.utils.version.VersionScalar;
import org.infinispan.versioning.utils.version.VersionScalarGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.io.Serializable;
import java.util.*;

/**
 * Created by vagvaz on 9/25/14.
 */
public class ScanCallable <K,V> implements

        DistributedCallable<K, V, String>, Serializable {
   transient protected Cache<K, V> inputCache;
    transient protected VersionedCache versionedCache;
   transient protected Cache outputCache;
    transient  protected Cache pageRankCache;
   transient protected FilterOperatorTree tree;
   transient protected JsonObject inputSchema;
   transient protected JsonObject outputSchema;
   transient protected Map<String, String> outputMap;
   transient protected Map<String, JsonObject> targetsMap;
   transient protected JsonObject conf;
   transient protected double totalSum;
   transient protected Cache approxSumCache;
   protected String configString;
   protected String output;
   protected String qualString;
   transient protected InfinispanManager manager;
   protected Logger log;
   public ScanCallable(String configString, String output) {
      this.configString = configString;
      this.output = output;
   }

   @Override
   public void setEnvironment(Cache<K, V> cache, Set<K> inputKeys) {
      inputCache = cache;
     versionedCache = new VersionedCacheTreeMapImpl(cache,new VersionScalarGenerator(),cache.getName());

       manager =  new ClusterInfinispanManager(cache.getCacheManager());
      log = LoggerFactory.getLogger("###### ScanCallable: " + manager.getMemberName().toString());
      log.info("--------------------    get output cache ------------------------");
      outputCache = (Cache) manager.getPersisentCache(output);
      log.info("--------------------    get pagerank cache ------------------------");
      pageRankCache = (Cache) manager.getPersisentCache("pagerankCache");
      log.info("--------------------    get approxSum cache ------------------------");
       approxSumCache = (Cache) manager.getPersisentCache("approx_sum_cache");
      totalSum = -1f;

      conf = new JsonObject(configString);
      if(conf.getObject("body").containsField("qual"))
      {
         tree = new FilterOperatorTree(conf.getObject("body").getObject("qual"));
      }
      else{
         tree =null;
      }

      outputSchema = conf.getObject("body").getObject("outputSchema");
      inputSchema = conf.getObject("body").getObject("inputSchema");
      targetsMap = new HashMap();
      outputMap = new HashMap<>();
         JsonArray targets = conf.getObject("body").getArray("targets");
      Iterator<Object> targetIterator = targets.iterator();
      while (targetIterator.hasNext()) {
         JsonObject target = (JsonObject) targetIterator.next();
         targetsMap.put(target.getObject("expr").getObject("body").getObject("column").getString("name"), target);
      }
   }

   @Override
   public String call() throws Exception {
      log.info("--------------------   Iterate over values... ------------------------");

     final ClusteringDependentLogic cdl = inputCache.getAdvancedCache().getComponentRegistry().getComponent(ClusteringDependentLogic.class);

//      for (Map.Entry<K, V> entry : inputCache.getAdvancedCache().getDataContainer().entrySet()) {
     for(Object key : inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).keySet()){
          if(!cdl.localNodeIsPrimaryOwner(key))
            continue;
//         System.err.println(manager.getCacheManager().getAddress().toString() + " "+ entry.getKey() + "       " + entry.getValue());
          //VERSIONING
//          String versionedKey = (String) key;
//          String ikey = pruneVersion(versionedKey);
//          Version latestVersion = versionedCache.getLatestVersion(ikey);
//          if(latestVersion == null){
//           continue;
//           }
//          Version currentVersion = getVersion(versionedKey);
//       Object objectValue = versionedCache.get(ikey);
//       String value = (String)objectValue;
//END VERSIONING
       //NONVERSIONING
       String ikey = (String)key;
       String value = (String) inputCache.get(ikey);
//ENDNONVERSIONDING
          //         String value = (String) entry.getValue();

//          String value = (String) entry.getValue();
//          String value = (String)inputCache.get(key);
          Tuple tuple = new Tuple(value);
//          namesToLowerCase(tuple);
          renameAllTupleAttributes(tuple);
         if (tree != null) {
            if(tree.accept(tuple)) {
               tuple = prepareOutput(tuple);
//               log.info("--------------------    put into output with filter ------------------------");
               outputCache.put(key.toString(), tuple.asString());
            }
         }
         else{
            tuple = prepareOutput(tuple);
//            log.info("--------------------    put into output without tree ------------------------");
            outputCache.put(key.toString(), tuple.asString());
         }

      }
      return inputCache.getCacheManager().getAddress().toString();
   }
   private Version getVersion(String versionedKey) {
           Version result = null;
        String stringVersion = versionedKey.substring(versionedKey.lastIndexOf(":") + 1);
                result = new VersionScalar(Long.parseLong(stringVersion));
                return result;
            }

                private String pruneVersion(String versionedKey) {
                String result = versionedKey.substring(0,versionedKey.lastIndexOf(":"));
                return result;
            }


  private void namesToLowerCase(Tuple tuple) {
    Set<String> fieldNames  =  new HashSet<>(tuple.getFieldNames());
    for(String field : fieldNames){
      tuple.renameAttribute(field,field.toLowerCase());
    }
  }

  private void renameAllTupleAttributes(Tuple tuple) {
       JsonArray fields = inputSchema.getArray("fields");
        Iterator<Object> iterator = fields.iterator();
        String columnName = null;
        while(iterator.hasNext()){
            JsonObject tmp = (JsonObject) iterator.next();
            columnName = tmp.getString("name");
            int lastPeriod = columnName.lastIndexOf(".");
            String attributeName = columnName.substring(lastPeriod+1);
            tuple.renameAttribute(attributeName,columnName);
        }

        handlePagerank(columnName.substring(0,columnName.lastIndexOf(".")),tuple);
    }

    protected Tuple prepareOutput(Tuple tuple) {
      if (outputSchema.toString().equals(inputSchema.toString())) {
         return tuple;
      }

      JsonObject result = new JsonObject();
       //WARNING
//       System.err.println("out: " + tuple.asString());

       if(targetsMap.size() == 0)
       {
//          System.err.println("s 0 ");
          return tuple;

       }
//       System.err.println("normal");

       //END OF WANRING
      List<String> toRemoveFields = new ArrayList<String>();
      Map<String,String> toRename = new HashMap<String,String>();
      for (String field : tuple.getFieldNames()) {
         JsonObject ob = targetsMap.get(field);
         if (ob == null)
            toRemoveFields.add(field);
         else {
            toRename.put(field, ob.getObject("column").getString("name"));
         }
      }
      tuple.removeAtrributes(toRemoveFields);
      tuple.renameAttributes(toRename);
      return tuple;
   }

   protected void handlePagerank(String substring, Tuple t) {
       if(conf.getObject("body").getObject("tableDesc").getString("tableName").equals("default.webpages")){
            if(totalSum < 0){
                computeTotalSum();
            }
            String url = t.getAttribute("default.webpages.url");
           DSPMNode currentPagerank = (DSPMNode) pageRankCache.get(url);
            if(currentPagerank == null || totalSum <= 0)
            {
                t.setAttribute("default.webpages.pagerank",0f);
                return;
            }
//            t.setNumberAttribute("default.webpages.pagerank",0.032342);
            t.setNumberAttribute("default.webpages.pagerank",currentPagerank.getVisitCount()/totalSum);

           //READ PAGERANK FROM PAGERANK CACHE;
           //READ TOTAL ONCE
           //compute value update it to tuple



//      if (t.hasField("default.webpages.pagerank")) {
//         if (!t.hasField("url"))
//            return;
//         String pagerankStr = t.getAttribute("pagerank");
//            Double d = Double.parseDouble(pagerankStr);
//            if (d < 0.0) {
//
//                try {
////                    d = LeadsPrGraph.getPageDistr(t.getAttribute("url"));
//                    d = (double) LeadsPrGraph.getPageVisitCount(t.getAttribute("url"));
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                t.setAttribute("pagerank", d.toString());
//        }
      }
   }

    private void computeTotalSum() {
       log.info("--------------------   Creating iterable over approx sum entries ------------------------");
        CloseableIterable<Map.Entry<String, Integer>> iterable =
                approxSumCache.getAdvancedCache().filterEntries(new AcceptAllFilter());
       log.info("--------------------    Iterating over approx sum entries cache ------------------------");
        for (Map.Entry<String, Integer> outerEntry : iterable) {
            totalSum += outerEntry.getValue() ;
        }
        if(totalSum > 0){
            totalSum+=1;
        }
    }
}
