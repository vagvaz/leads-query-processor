package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.AcceptAllFilter;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.infinispan.LeadsCollector;
import eu.leads.processor.infinispan.LeadsMapperCallable;
import eu.leads.processor.infinispan.LeadsReducerCallable;
import eu.leads.processor.infinispan.operators.mapreduce.WGSMapper;
import eu.leads.processor.infinispan.operators.mapreduce.WGSReducer;
import eu.leads.processor.plugins.pagerank.node.DSPMNode;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.util.CloseableIterable;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.distexec.DistributedTask;
import org.infinispan.distexec.DistributedTaskBuilder;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by vagvaz on 9/26/14.
 */
public class WGSOperator extends MapReduceOperator {
   private JsonArray attributesArray;
   private double totalSum = -1.0f;
   Cache pagerank = null;
   Cache approx_sum = null;

   public WGSOperator(Node com, InfinispanManager persistence,LogProxy log, Action action) {
      super(com,persistence,log,action);
      attributesArray = new JsonArray();
      attributesArray.add("url");
      attributesArray.add("links");
      attributesArray.add("sentiment");
      attributesArray.add("pagerank");

   }
   @Override
   public void init(JsonObject config) {
      super.init(conf);
      init_statistics(this.getClass().getCanonicalName());
   }

   public void setupMapReduceJob(String inputCacheName,String intermediateCacheName1, String outputCacheName){
      inputCache = (Cache) manager.getPersisentCache(inputCacheName);
      intermediateCache = (BasicCache) manager.getPersisentCache(intermediateCacheName1);
      //create Intermediate cache name for data on the same Sites as outputCache
      intermediateDataCache = (BasicCache) manager.getPersisentCache(intermediateCacheName1+".data");
      //create Intermediate  keys cache name for data on the same Sites as outputCache;
      keysCache = (BasicCache)manager.getPersisentCache(intermediateCacheName1+".keys");
      //createIndexCache for getting all the nodes that contain values with the same key! in a mc
      indexSiteCache = (BasicCache)manager.getPersisentCache(intermediateCacheName1+".indexed");
//    indexSiteCache = (BasicCache)manager.getIndexedPersistentCache(intermediateCacheName+".indexed");
      outputCache = (BasicCache) manager.getPersisentCache(outputCacheName);
      collector = new LeadsCollector(0, intermediateCacheName1);
   }
   @Override
   public void run() {
//      int count = 0;
//      String currentInput = getName() +".iter0";
//      String currentOutput = "";
//      String currentIntermediate = getName()+".itermediate0";
//      inputCacheName = getName() +".iter0";
//      inputCache = (Cache) manager.getPersisentCache(inputCacheName);
//      JsonObject configBody = conf.getObject("body");
//      inputCache.put(configBody.getString("url"),configBody.getString("url"));
//      Cache realOutput = (Cache) manager.getPersisentCache(conf.getString("realOutput"));
//      for ( count = 0; count < configBody.getInteger("depth"); count++) {
//         currentInput = getName() +".iter"+count;
//         currentOutput = getName() +".iter"+(count+1);
//         currentIntermediate = getName()+".itermediate"+count;
////         inputCache = (Cache)manager.getPersisentCache(currentInput);
////         inputCache = (Cache)manager.getPersisentCache(getName()+".iter"+String.valueOf(count));
//         System.out.println("realOutput " + conf.getString("realOutput") + " \nsize" + realOutput.size());
//
//         JsonObject jobConfig = new JsonObject();
//         jobConfig.putNumber("iteration", count);
//         jobConfig.putNumber("depth", configBody.getInteger("depth"));
//         jobConfig.putArray("attributes", attributesArray);
//         if(count < configBody.getInteger("depth")){
//            jobConfig.putString("outputCache",currentOutput);
//         }
//         else
//         {
//            jobConfig.putString("outputCache","");
//         }
//         jobConfig.putString("realOutput",conf.getString("realOutput"));
//         jobConfig.putString("webCache","default.webpages");
//         setupMapReduceJob(currentInput,currentIntermediate,currentOutput);
//         executeMapReducePhase(jobConfig);
//      }
      int count = 0;
      for ( count = 0; count < conf.getObject("body").getInteger("depth"); count++) {
         findPendingMMCFromGlobal();
         findPendingRMCFromGlobal();
         createCaches(isRemote, executeOnlyMap, executeOnlyReduce);
         if(executeOnlyMap) {
            setupMapCallable();
            executeMap();
         }
         if(!failed) {
            if (executeOnlyReduce) {
               setupReduceCallable();
               executeReduce();
            }
            if (!failed) {
               cleanup();
            } else {
               failCleanup();
            }
         }
         else {
            failCleanup();
         }
      }

     cleanup();
   }
//   @Override
   public void run2() {
      int count = 0;
      inputCacheName = getName() +".iter0";
      inputCache = (Cache) manager.getPersisentCache(inputCacheName);
      JsonObject configBody = conf.getObject("body");
      inputCache.put(configBody.getString("url"),configBody.getString("url"));
      Cache realOutput = (Cache) manager.getPersisentCache(conf.getString("realOutput"));
      Cache webCache = (Cache)manager.getPersisentCache("default.webpages");
      pagerank = (Cache)manager.getPersisentCache("pagerankCache");
      approx_sum = (Cache) manager.getPersisentCache("approx_sum_cache");
      Cache approx   = (Cache)manager.getPersisentCache("approx_sum_cache");
      String prefix = webCache.getName()+":";
//      Cache currentLevel = (Cache) manager.getPersisentCache(inputCacheName+".curelevel");
//      Cache nextLevel   = (Cache) manager.getPersisentCache(inputCacheName +".nextlevel");
      HashSet<String> nextLevel = new HashSet<String>();
      JsonArray currentLevel = new JsonArray();
      nextLevel.add(configBody.getString("url"));
      for ( count = 0; count < configBody.getInteger("depth"); count++) {
         HashSet<String> newCurrent = new HashSet<>();
         for(String w : nextLevel){
            String jsonString = (String) webCache.get(prefix+w);
            if (jsonString==null || jsonString.equals("")){
               continue;
            }
            Tuple t = new Tuple(jsonString);

            JsonObject result = new JsonObject();
            result.putString("url", t.getAttribute("url"));
            result.putString("pagerank", computePagerank(result.getString("url")));
            result.putString("sentiment", t.getGenericAttribute("sentiment").toString());
            result.putValue("links",t.getGenericAttribute("links"));
            currentLevel.add(result);
            if(count < 2)
               if (!result.getElement("links").isArray())
                  continue;
               JsonArray links = result.getArray("links");
               Iterator<Object> iterator = links.iterator();
               while(iterator.hasNext()){
                  String link = (String) iterator.next();
                  newCurrent.add(link);
               }
            }

         JsonObject res = new JsonObject();
         res.putArray("result",currentLevel);
         realOutput.put(Integer.toString(count), res.toString());
         nextLevel = newCurrent;
         currentLevel = new JsonArray();
         }
      cleanup();
   }

   private String computePagerank(String url) {
      double result = 0.0;
      if(totalSum < 0){
         computeTotalSum();
      }
      DSPMNode currentPagerank = (DSPMNode) pagerank.get(url);
      if(currentPagerank == null || totalSum <= 0)
      {

         return Double.toString(0.0f);
      }
      result = currentPagerank.getVisitCount()/totalSum;
      return Double.toString(result);

   }

   private void computeTotalSum() {

      CloseableIterable<Map.Entry<String, Integer>> iterable =
              approx_sum.getAdvancedCache().filterEntries(new AcceptAllFilter());

      for (Map.Entry<String, Integer> outerEntry : iterable) {
         totalSum += outerEntry.getValue() ;
      }
      if(totalSum > 0){
         totalSum+=1;
      }
   }

  private void executeMapReducePhase(JsonObject jobConfig) {
//     MapReduceTask task = new MapReduceTask(inputCache);
//     task.mappedWith(new WGSMapper(jobConfig.toString()));
//     task.reducedWith(new WGSReducer(jobConfig.toString()));
//     task.timeout(1, TimeUnit.HOURS);
//     task.execute();





    DistributedExecutorService des = new DefaultExecutorService((Cache<?, ?>) inputCache);
//    intermediateCacheName = inputCache.getName()+".intermediate";

    LeadsMapperCallable mapperCallable = new LeadsMapperCallable((Cache) inputCache,collector,new WGSMapper
                                                                                        (jobConfig.toString()),
                                                                  LQPConfiguration.getInstance().getMicroClusterName());
     DistributedTaskBuilder builder =des.createDistributedTaskBuilder(mapperCallable);
     builder.timeout(1, TimeUnit.HOURS);
     DistributedTask task = builder.build();
     List<Future<?>> res = des.submitEverywhere(task);
     try {
        if (res != null) {
           for (Future<?> result : res) {
              result.get();
           }
           System.out.println("mapper Execution is done");
        }
        else
        {
           System.out.println("mapper Execution not done");
        }
     } catch (InterruptedException e) {
        e.printStackTrace();
     } catch (ExecutionException e) {
        e.printStackTrace();
     }
     System.err.println("keysCache " + keysCache.size());
     System.err.println("dataCache " + intermediateDataCache.size());
     System.err.println("indexedCache " + indexSiteCache.size());
////    //Reduce
////
    LeadsReducerCallable reducerCacllable = new LeadsReducerCallable(outputCache.getName(), new WGSReducer(jobConfig.toString()),intermediateCache.getName());
     DistributedExecutorService des_inter = new DefaultExecutorService((Cache<?, ?>) keysCache);
     DistributedTaskBuilder reduceTaskBuilder = des_inter.createDistributedTaskBuilder(reducerCacllable);
     reduceTaskBuilder.timeout(1,TimeUnit.HOURS);
     DistributedTask reduceTask = reduceTaskBuilder.build();
     List<Future<?>> reducers_res= des_inter
                                           .submitEverywhere(reduceTask);
     try {
        if (reducers_res != null) {
           for (Future<?> result : reducers_res) {
              System.err.println("wait " + System.currentTimeMillis());
              System.err.println(result.get());
              System.err.println("wait end" + System.currentTimeMillis());
           }
           System.out.println("reducer Execution is done");
        } else {
           System.out.println("reducer Execution not done");
        }
     } catch (InterruptedException e) {
        e.printStackTrace();
     } catch (ExecutionException e) {
        e.printStackTrace();
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
