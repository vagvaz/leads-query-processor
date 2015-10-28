package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.*;
import eu.leads.processor.common.utils.PrintUtilities;
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
import org.bson.types.BasicBSONList;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.util.CloseableIterable;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.distexec.DistributedTask;
import org.infinispan.distexec.DistributedTaskBuilder;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by vagvaz on 9/26/14.
 */
public class WGSOperator extends BasicOperator {
  private JsonArray attributesArray;
  private double totalSum = -1.0f;
  private int iteration = -1;
  Cache pagerank = null;
  Cache approx_sum = null;
  private JsonObject configBody;
  private String currentInput;
  private String currentOutput;
  private String currentIntermediate;
  private String realOutput;

  public WGSOperator(Node com, InfinispanManager persistence, LogProxy log, Action action) {
    super(com, persistence, log, action);
    attributesArray = new JsonArray();
    attributesArray.add("url");
    attributesArray.add("links");
    attributesArray.add("sentiment");
    attributesArray.add("pagerank");

  }

  @Override public void init(JsonObject config) {
    super.init(conf);
    init_statistics(this.getClass().getCanonicalName());
//    String ensembleString = computeEnsembleHost();
//    EnsembleCacheManager emanager = new EnsembleCacheManager(ensembleString);
//    EnsembleCache ecache = emanager.getCache(getOutput()+"iter0", new ArrayList<>(emanager.sites()), EnsembleCacheManager.Consistency.DIST);
//    ecache.put(configBody.getString("url"),configBody.getString("url"));
  }

  public void setupMapReduceJob(String inputCacheName, String intermediateCacheName1, String outputCacheName) {
    inputCache = (Cache) manager.getPersisentCache(inputCacheName);
//    intermediateCache = (BasicCache) manager.getPersisentCache(intermediateCacheName1);
    //create Intermediate cache name for data on the same Sites as outputCache
//    intermediateDataCache = (BasicCache) manager.getPersisentCache(intermediateCacheName1 + ".data");
    //create Intermediate  keys cache name for data on the same Sites as outputCache;
    //    keysCache = (BasicCache)manager.getPersisentCache(intermediateCacheName1+".keys");
    //createIndexCache for getting all the nodes that contain values with the same key! in a mc
    //    indexSiteCache = (BasicCache)manager.getPersisentCache(intermediateCacheName1+".indexed");
    //    indexSiteCache = (BasicCache)manager.getIndexedPersistentCache(intermediateCacheName+".indexed");
    outputCache = (BasicCache) manager.getPersisentCache(outputCacheName);
//    collector = new LeadsCollector(0, intermediateCacheName1);
    //     EnsembleCacheManager mm; mm.sites();
    //     EnsembleCacheManager mm = new EnsembleCacheManager(mm.getLocalSite().)


  }

//  @Override
  public void run2() {
//  @Override public void run() {
    if(isRemote){
      super.run();
      return;
    }
    for (int count = 0; count < conf.getObject("body").getInteger("depth"); count++) {
      conf.putString("currentInput",getOutput()+"iter"+count);
      conf.putString("currentIntermediate",getOutput()+"intermediate"+count);
      conf.putString("realOuput",conf.getString("realOutput"));
      conf.putNumber("iteration",count);
      conf.putNumber("depth",conf.getObject("body").getInteger("depth"));
      if(count != conf.getObject("body").getInteger("depth")-1 ){
        conf.putString("outputEnsembleHost",getEnsembleHost(getRunningMC()));
      } else{
        Set<String> runningMc= new HashSet<>();
        runningMc.add(LQPConfiguration.getInstance().getMicroClusterName());
        conf.putString("outputEnsembleHost", getEnsembleHost(runningMc));
      }
      super.run();
    }
    super.cleanup();
  }

  @Override public void setupMapCallable() {
//    inputCacheName = currentInput;
//    intermediateCacheName = currentIntermediate;
    JsonObject configBody = new JsonObject();
    configBody.putNumber("iteration", conf.getInteger("iteration"));
    configBody.putNumber("depth", conf.getInteger("depth"));
    configBody.putArray("attributes", attributesArray);
    configBody.putString("outputEnsembleHost", conf.getString("runningMC"));
//    configBody.putNumber("iteration", iteration);
//    configBody.putNumber("depth", configBody.getInteger("depth"));
//    configBody.putArray("attributes", attributesArray);
//    configBody.putString("outputEnsembleHost", getEnsembleHost(getRunningMC()));
    if (iteration < configBody.getInteger("depth")) {
      configBody.putString("outputCache", conf.getString("currentOutput"));

    } else {
      configBody.putString("outputCache", "");
    }

//    inputCacheName = conf.getString("currentInput");
    inputCache = (Cache) manager.getPersisentCache(conf.getString("currentInput"));
//    intermediateCacheName = conf.getString("currentIntermediate");
    configBody.putString("webCache", "default.webpages");
    action.getData().getObject("operator").getObject("configuration").putObject("body", configBody);
//    setMapper(new WGSMapper(configBody.toString()));

//    super.setupMapCallable();
  }



  @Override public void setupReduceCallable() {
//    outputCacheName = conf.getString("realOutput");
    configBody.putNumber("iteration", iteration);
    configBody.putString("realOutput", conf.getString("realOutput"));
    action.getData().getObject("operator").getObject("configuration").putObject("body", configBody);
//    setReducer(new WGSReducer(configBody.toString()));
//    super.setupReduceCallable();
  }

  @Override public boolean isSingleStage() {
    return true;
  }

  @Override public void createCaches(boolean isRemote, boolean executeOnlyMap, boolean executeOnlyReduce) {

    if (iteration == 0) {
      Set<String> targetMC = getTargetMC();
      String tmpIntermediateCacheName = getOutput() + ".intermediate";
      for (int i = 0; i < configBody.getInteger("depth"); i++) {
        for (String mc : targetMC) {
          if (i == 0) { // the real output should be created only once
            createCache(mc, realOutput);
          }
          tmpIntermediateCacheName = getOutput() + ".intermediate" + i;
//          createCache(mc, tmpIntermediateCacheName + i);
          //create Intermediate cache name for data on the same Sites as outputCache
          createCache(mc, tmpIntermediateCacheName + ".data","localIndexListener:batchputListener");
          //create Intermediate  keys cache name for data on the same Sites as outputCache;
//          createCache(mc, tmpIntermediateCacheName + ".keys");
          //createIndexCache for getting all the nodes that contain values with the same key! in a mc
//          createCache(mc, tmpIntermediateCacheName + ".indexed");
        }
        String tmpInputName = getOutput() + ".iter";
        for (String inputMC : getRunningMC()) {
          //          if(i < configBody.getInteger("depth")){
          tmpInputName = getOutput() + ".iter" + i;
          createCache(inputMC, tmpInputName);
          createCache(inputMC, getOutput() + ".iter" + (i + 1));
          //          }
        }

      }
    }
  }

  @Override public String getContinuousListenerClass() {
    return null;
  }

  @Override
  public void run() {
    int count = 0;
    String ensembleString = getEnsembleHost(globalConfig.getObject("microclouds").getFieldNames());
    EnsembleCacheManager emanager = new EnsembleCacheManager(ensembleString);

    EnsembleCacheUtilsSingle single = new EnsembleCacheUtilsSingle();
    single.initialize(emanager);
    Map<String,String> mcMap = new HashMap<>();
    for(String mc : globalConfig.getObject("microclouds").getFieldNames()){
      mcMap.put(globalConfig.getObject("componentsAddrs").getArray(mc).get(0).toString(),mc);
    }

//    inputCacheName = getOutput() + ".iter0";
//    EnsembleCache inputCache = (EnsembleCache) emanager.getCache(inputCacheName, new ArrayList<>(emanager.sites()),
//        EnsembleCacheManager.Consistency.DIST);
//    EnsembleCache inputCache = (EnsembleCache) emanager.getPersisentCache(inputCacheName);
    inputCache = (Cache) manager.getPersisentCache(getOutput()+"iter0");
    JsonObject configBody = conf.getObject("body");
    inputCache.put(configBody.getString("url"), configBody.getString("url"));
    Cache realOutput = (Cache) manager.getPersisentCache(conf.getString("realOutput"));
    EnsembleCache webCache = (EnsembleCache) emanager.getCache("default.webpages", new ArrayList<>(emanager.sites()),
        EnsembleCacheManager.Consistency.DIST);
//    pagerank = (Cache) emanager.getPersisentCache("pagerankCache");
//    approx_sum = (Cache) emanager.getPersisentCache("approx_sum_cache");
//    Cache approx = (Cache) emanager.getPersisentCache("approx_sum_cache");
    String prefix = webCache.getName() + ":";
    //      Cache currentLevel = (Cache) manager.getPersisentCache(inputCacheName+".curelevel");
    //      Cache nextLevel   = (Cache) manager.getPersisentCache(inputCacheName +".nextlevel");
    HashSet<String> nextLevel = new HashSet<String>();
    JsonArray currentLevel = new JsonArray();
    nextLevel.add(configBody.getString("url"));
    for (count = 0; count < configBody.getInteger("depth"); count++) {
      HashSet<String> newCurrent = new HashSet<>();
      for (String w : nextLevel) {
//        String jsonString = (String) webCache.get(prefix + w);
//        if (jsonString == null || jsonString.equals("")) {
//          continue;
//        }
//        Tuple t = new Tuple(jsonString);


//        EnsembleCacheManager ensembleCacheManager = new EnsembleCacheManager(ensembleString);
        Tuple t  = (Tuple) EnsembleCacheUtils.getFromCache(webCache, prefix + w);
        if(t==null)
          continue;
        JsonObject result = new JsonObject();
        result.putString("url", t.getAttribute("default.webpages.url"));
        result.putString("pagerank", computePagerank(result.getString("url")));
        result.putString("sentiment", t.getGenericAttribute("default.webpages.sentiment").toString());
        BasicBSONList linksList = (BasicBSONList) t.getGenericAttribute("default.webpages.links");
        JsonArray linksarray = new JsonArray();
        Iterator<Object> it = linksList.iterator();
        while(it.hasNext()){
          String link = (String)it.next();
          linksarray.add(link);
        }
        result.putValue("links",linksarray);
        String mc = single.decideMC(result.getString("url"));
        result.putString("micro-cluster", mcMap.get(mc));
        currentLevel.add(result);
        if (count < 2)
          if (!result.getElement("links").isArray())
            continue;
        JsonArray links = result.getArray("links");
        Iterator<Object> iterator = links.iterator();
        int counter = 0;
        while (iterator.hasNext()) {
          String link = (String) iterator.next();
          newCurrent.add(link);
          counter++;


//          if(counter > 15){
//            break;
//          }
        }
      }

      JsonObject res = new JsonObject();
      res.putArray("result", currentLevel);
      realOutput.put(Integer.toString(count), res.toString());
      nextLevel = newCurrent;
      currentLevel = new JsonArray();
    }
    cleanup();
  }

  private String computePagerank(String url) {
    double result = 0.0;
//    if (totalSum < 0) {
//      computeTotalSum();
//    }
//    DSPMNode currentPagerank = (DSPMNode) pagerank.get(url);
//    if (currentPagerank == null || totalSum <= 0) {
//
//      return Double.toString(0.0f);
//    }
//    result = currentPagerank.getVisitCount() / totalSum;
    result = (1.0/url.length()) / 1000;
    if(url.contains("yahoo") || url.contains("google") || url.contains("twitter") || url.contains("linkedin") ){
      result *= 2;
    }
    return Double.toString(result);

  }

  private void computeTotalSum() {

    CloseableIterable<Map.Entry<String, Integer>> iterable =
        approx_sum.getAdvancedCache().filterEntries(new AcceptAllFilter());

    for (Map.Entry<String, Integer> outerEntry : iterable) {
      totalSum += outerEntry.getValue();
    }
    iterable.close();
    if (totalSum > 0) {
      totalSum += 1;
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

//    LeadsMapperCallable mapperCallable =
//        new LeadsMapperCallable((Cache) inputCache, collector, new WGSMapper(jobConfig.toString()),
//            LQPConfiguration.getInstance().getMicroClusterName());
    DistributedTaskBuilder builder = des.createDistributedTaskBuilder(mapperCallable);
    builder.timeout(1, TimeUnit.HOURS);
    DistributedTask task = builder.build();
    List<Future<?>> res = des.submitEverywhere(task);
    try {
      if (res != null) {
        for (Future<?> result : res) {
          result.get();
        }
        System.out.println("mapper Execution is done");
      } else {
        System.out.println("mapper Execution not done");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

    ////    //Reduce
    ////
//    LeadsReducerCallable reducerCacllable =
//        new LeadsReducerCallable(outputCache.getName(), new WGSReducer(jobConfig.toString()),
//            intermediateCache.getName());
//    DistributedExecutorService des_inter = new DefaultExecutorService((Cache<?, ?>) intermediateDataCache);
//    DistributedTaskBuilder reduceTaskBuilder = des_inter.createDistributedTaskBuilder(reducerCacllable);
//    reduceTaskBuilder.timeout(1, TimeUnit.HOURS);
//    DistributedTask reduceTask = reduceTaskBuilder.build();
//    List<Future<?>> reducers_res = des_inter.submitEverywhere(reduceTask);
//    try {
//      if (reducers_res != null) {
//        for (Future<?> result : reducers_res) {
//          System.err.println("wait " + System.currentTimeMillis());
//          System.err.println(result.get());
//          System.err.println("wait end" + System.currentTimeMillis());
//        }
//        System.out.println("reducer Execution is done");
//      } else {
//        System.out.println("reducer Execution not done");
//      }
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    } catch (ExecutionException e) {
//      e.printStackTrace();
//    }


  }


  @Override public void cleanup() {
    super.cleanup();
  }



}


//    if (!isRemote) {
//      int count = 0;
//      currentInput = getOutput() + ".iter0";
//      currentOutput = getOutput() + ".iter1";
//      currentIntermediate = getOutput() + ".intermediate0";
//
//      //           inputCacheName = getName() +".iter0";
//      //           inputCache = (Cache) manager.getPersisentCache(inputCacheName);
//      configBody = conf.getObject("body");
//      realOutput = conf.getString("realOutput");
//      //           inputCache.put(configBody.getString("url"),configBody.getString("url"));
//      //           Cache realOutput = (Cache) manager.getPersisentCache(conf.getString("realOutput"));
//
//      for (count = 0; count < conf.getObject("body").getInteger("depth"); count++) {
//
//        PrintUtilities.printMap(manager.getPersisentCache(realOutput));
//        findPendingMMCFromGlobal();
//        findPendingRMCFromGlobal();
//        iteration = count;
//
//        createCaches(isRemote, executeOnlyMap, executeOnlyReduce);
//        currentInput = getOutput() + ".iter" + count;
//        currentOutput = getOutput() + ".iter" + (count + 1);
//        currentIntermediate = getOutput() + ".intermediate" + count;
//        if (executeOnlyMap) {
//          setupMapCallable();
//          executeMap();
//        }
//        if (!failed) {
//          if (executeOnlyReduce) {
//            setupReduceCallable();
//            executeReduce();
//          }
//          if (!failed) {
//            //            cleanup();
//            unsubscribeToMapActions("execution." + com.getId() + "." + action.getId());
//          } else {
//            failCleanup();
//          }
//        } else {
//          failCleanup();
//        }
//      }
//    } else {
//      findPendingMMCFromGlobal();
//      findPendingRMCFromGlobal();
//      iteration = conf.getObject("body").getInteger("iteration");
//
//      //      createCaches(isRemote, executeOnlyMap, executeOnlyReduce);
//      currentInput = getOutput() + ".iter" + iteration;
//      currentOutput = getOutput() + ".iter" + (iteration + 1);
//      currentIntermediate = getOutput() + ".intermediate" + iteration;
//      if (executeOnlyMap) {
//        setupMapCallable();
//        executeMap();
//      }
//      if (!failed) {
//        if (executeOnlyReduce) {
//          setupReduceCallable();
//          executeReduce();
//        }
//        if (!failed) {
//          cleanup();
//        } else {
//          failCleanup();
//        }
//      } else {
//        failCleanup();
//      }
//    }
//    cleanup();
