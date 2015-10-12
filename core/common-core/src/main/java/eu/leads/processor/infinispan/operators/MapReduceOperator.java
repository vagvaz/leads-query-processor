package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.WebUtils;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.infinispan.*;
import eu.leads.processor.infinispan.LeadsCollector;
import eu.leads.processor.infinispan.LeadsLocalReducerCallable;
import eu.leads.processor.infinispan.LeadsMapper;
import eu.leads.processor.infinispan.LeadsMapperCallable;
import eu.leads.processor.infinispan.LeadsReducer;
import eu.leads.processor.infinispan.LeadsReducerCallable;

import eu.leads.processor.infinispan.continuous.WordCountContinuousOperator;
import eu.leads.processor.web.WebServiceClient;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.vertx.java.core.json.JsonObject;

import java.util.Set;
import java.util.UUID;

/**
 * Created by tr on 19/9/2014.
 */
public abstract class MapReduceOperator extends BasicOperator{
//  protected transient BasicCache inputCache;
  protected transient BasicCache intermediateCache;
  protected transient BasicCache  outputCache;
//  protected transient BasicCache  keysCache;
  protected transient BasicCache intermediateDataCache;
  protected transient BasicCache intermediateLocalDataCache;
//  protected transient BasicCache indexSiteCache;
  protected String inputCacheName;
  protected String outputCacheName;
  protected String intermediateCacheName;
  protected String intermediateLocalCacheName;
  protected LeadsMapper<?, ?, ?, ?> mapper;
  protected LeadsCollector<?, ?> collector;
  protected LeadsCombiner<?,?> combiner;
  protected LeadsReducer<?, ?> reducer;
  protected LeadsReducer<?, ?> localReducer;
  protected String uuid;
  protected BasicCache intermediateLocalCache;


  public MapReduceOperator(Node com, InfinispanManager persistence, LogProxy log, Action action) {

    super(com,persistence,log,action);
    inputCacheName = getInput();
    outputCacheName = action.getData().getObject("operator").getString("id");
    intermediateCacheName = action.getData().getObject("operator").getString("id")+".intermediate";
    intermediateLocalCacheName = intermediateCacheName + ".local";
    uuid = UUID.randomUUID().toString();
  }

  public void setMapper(LeadsMapper<?, ?, ?, ?> mapper) {
    this.mapper = mapper;

  }
  public void setFederationReducer(LeadsReducer<?, ?> federationReducer) {
    this.reducer = federationReducer;
  }

  public void setReducer(LeadsReducer<?, ?> reducer) {
    this.reducer = reducer;
  }

  @Override
  public void init(JsonObject config) {
    conf.putString("output",getOutput());
    inputCache = (Cache) manager.getPersisentCache(inputCacheName);
//    intermediateCache = (BasicCache) manager.getPersisentCache(intermediateCacheName);
    //create Intermediate cache name for data on the same Sites as outputCache
//    intermediateDataCache = (BasicCache) manager.getPersisentCache(intermediateCacheName+".data");
    //create Intermediate  keys cache name for data on the same Sites as outputCache;
//    keysCache = (BasicCache)manager.getPersisentCache(intermediateCacheName+".keys");
    //createIndexCache for getting all the nodes that contain values with the same key! in a mc
//    indexSiteCache = (BasicCache)manager.getPersisentCache(intermediateCacheName+".indexed");
//    indexSiteCache = (BasicCache)manager.getIndexedPersistentCache(intermediateCacheName+".indexed");
//    outputCache = (BasicCache) manager.getPersisentCache(outputCacheName);
//    reduceInputCache = (Cache) keysCache;
//    reduceInputCache = intermediateDataCache;
// vagvaz   collector = new LeadsCollector(0, intermediateCacheName);
    if (reduceLocal) {
//      intermediateLocalCache = (BasicCache) manager.getPersisentCache(intermediateLocalCacheName);
//      intermediateLocalDataCache = (BasicCache) manager.getPersisentCache(intermediateLocalCacheName
//          + ".data");
//      reduceLocalInputCache = (Cache) intermediateLocalDataCache;
      collector = new LeadsCollector(1000, intermediateLocalCacheName);  // TODO(ap0n): not sure for this
    } else {
      collector = new LeadsCollector(1000, intermediateCacheName);
    }
  }

  public String getIntermediateCacheName() {
    return intermediateCacheName;
  }

  public void setIntermediateCacheName(String intermediateCacheName) {
    this.intermediateCacheName = intermediateCacheName;
  }

  @Override
  public void cleanup() {
    inputCache = (Cache) manager.getPersisentCache(inputCacheName);
    super.cleanup();
    if(reduceLocal){
      if(!isRemote){
        WebUtils.stopCache(intermediateLocalCacheName+".data",globalConfig);
      }
//      manager.removePersistentCache(intermediateLocalCache+".data");
    }

    if(executeOnlyReduce) {
//      intermediateCache.stop();
//      indexSiteCache.stop();
//      intermediateDataCache.stop();
      if(!isRemote) {
        WebUtils.stopCache(intermediateCacheName+".data",globalConfig);
//        manager.removePersistentCache(intermediateDataCache.getName());
      }
//      keysCache.stop();
    }
  }

  @Override
  public void failCleanup(){
    inputCache = (Cache) manager.getPersisentCache(inputCacheName);
    super.failCleanup();
    if(reduceLocal){
      WebUtils.stopCache(intermediateLocalCacheName+".data",globalConfig);
//      manager.removePersistentCache(intermediateLocalCache+".data");
    }

    if(executeOnlyReduce) {
      //      intermediateCache.stop();
      //      indexSiteCache.stop();
      //      intermediateDataCache.stop();
      WebUtils.stopCache(intermediateCacheName+".data",globalConfig);
//      manager.removePersistentCache(intermediateDataCache.getName());
      //      keysCache.stop();
    }
  }

  @Override
  public boolean isSingleStage() {
    return false;
  }

  @Override
  public void createCaches(boolean isRemote, boolean executeOnlyMap, boolean executeOnlyReduce) {
    Set<String> targetMC = getTargetMC();
    if(!isRemote) {
      for (String mc : targetMC) {
        createCache(mc, getOutput(),"batchputListener");
        //      createCache(mc, intermediateCacheName);
        //create Intermediate cache name for data on the same Sites as outputCache
        if(!conf.containsField("skipMap")) {
//          if(!conf.getBoolean("skipMap")){
          if(!isRecCompReduce) {
            createCache(mc, intermediateCacheName + ".data", "localIndexListener:batchputListener");
          }else{
            createCache(mc, intermediateCacheName + ".data", "batchputListener");
          }
          if (reduceLocal) {
            System.out.println("REDUCE LOCAL DETECTED CREATING CACHE");
            if(!isRecCompReduceLocal) {
              createCache(mc, intermediateLocalCacheName + ".data", "localIndexListener:batchputListener");
            }else{
              createCache(mc, intermediateLocalCacheName + ".data", "batchputListener");
            }
          }
          else{
            System.out.println("NO REDUCE LOCAL");
          }
        }
        else{
          if(!conf.getBoolean("skipMap")) {
            createCache(mc, intermediateCacheName + ".data", "localIndexListener:batchputListener");
          }
        }
        //create Intermediate  keys cache name for data on the same Sites as outputCache;
        //      createCache(mc,intermediateCacheName+".keys");
        //createIndexCache for getting all the nodes that contain values with the same key! in a mc
        //      createCache(mc,intermediateCacheName+".indexed");
        //    indexSiteCache = (BasicCache)manager.getIndexedPersistentCache(intermediateCacheName+".indexed");

      }
    }
  }
  @Override
  public void setupMapCallable(){
  //    conf.putString("output",getOutput());
      inputCache = (Cache) manager.getPersisentCache(inputCacheName);
  //    intermediateCache = (BasicCache) manager.getPersisentCache(intermediateCacheName);
      //create Intermediate cache name for data on the same Sites as outputCache
  //    intermediateDataCache = (BasicCache) manager.getPersisentCache(intermediateCacheName+".data");
      //create Intermediate  keys cache name for data on the same Sites as outputCache;
  //    keysCache = (BasicCache)manager.getPersisentCache(intermediateCacheName+".keys");
      //createIndexCache for getting all the nodes that contain values with the same key! in a mc
  //    indexSiteCache = (BasicCache)manager.getPersisentCache(intermediateCacheName+".indexed");
      //    indexSiteCache = (BasicCache)manager.getIndexedPersistentCache(intermediateCacheName+".indexed");
  //    outputCache = (BasicCache) manager.getPersisentCache(outputCacheName);
  //    reduceInputCache = (Cache) keysCache;
  //vagvaz    collector = new LeadsCollector(0, intermediateCacheName);
  //vagvaz    mapperCallable = new LeadsMapperCallable((Cache) inputCache,collector,mapper,
  //vagvaz                                   LQPConfiguration.getInstance().getMicroClusterName());
      if (reduceLocal) {
        collector = new LeadsCollector(1000, intermediateLocalCacheName);
        if(isRecCompReduceLocal){
          JsonObject reduceLocalConf = getContinuousReduceLocal();
          String ensembleString = computeEnsembleHost(false);
          String inputListener = intermediateLocalCacheName+".data";
          String outputListener = intermediateCacheName;
          String window = "sizeBased";
          int windowSize = LQPConfiguration.getInstance().getConfiguration().getInt("node.continuous.windowSize",1000);
          int parallelism = LQPConfiguration.getInstance().getConfiguration().getInt("node.engine.parallelism",4);
          reduceLocalConf.putString("cache",inputListener);
          reduceLocalConf.getObject("conf").putString("window",window);
          reduceLocalConf.getObject("conf").putNumber("windowSize",windowSize);
          reduceLocalConf.getObject("conf").putNumber("parallelism", parallelism);
          reduceLocalConf.getObject("conf").putString("input",inputListener);
          reduceLocalConf.getObject("conf").putString("ensembleHost",ensembleString);
          reduceLocalConf.getObject("conf").getObject("operator").putString("ensembleString",ensembleString);
          reduceLocalConf.getObject("conf").getObject("operator").putString("output",outputListener);
          reduceLocalConf.getObject("conf").getObject("operator").putNumber("parallelism", parallelism);
          WebUtils.addListener(inputListener,getContinuousListenerClass(),reduceLocalConf,globalConfig);
        }
      } else {
        collector = new LeadsCollector(1000, intermediateCacheName);
      }
    if(isRecCompReduce){
      JsonObject reduceConf = getContinuousReduce();
      String ensembleString = computeEnsembleHost(false);
      String inputListener = intermediateCacheName+".data";
      String outputListener = getOutput();
      String window = "sizeBased";
      int windowSize = LQPConfiguration.getInstance().getConfiguration().getInt("node.continuous.windowSize",1000);
      int parallelism = LQPConfiguration.getInstance().getConfiguration().getInt("node.engine.parallelism",4);
      reduceConf.putString("cache",inputListener);
      reduceConf.getObject("conf").putString("window",window);
      reduceConf.getObject("conf").putNumber("windowSize",windowSize);
      reduceConf.getObject("conf").putNumber("parallelism", parallelism);
      reduceConf.getObject("conf").putString("input",inputListener);
      reduceConf.getObject("conf").putString("ensembleHost",ensembleString);
      reduceConf.getObject("conf").getObject("operator").putString("output",outputListener);
      reduceConf.getObject("conf").getObject("operator").putString("ensembleString",ensembleString);
      reduceConf.getObject("conf").getObject("operator").putNumber("parallelism", parallelism);
      WebUtils.addListener(inputListener,getContinuousListenerClass(),reduceConf,globalConfig);
    }

    mapperCallable = new LeadsMapperCallable((Cache) inputCache, collector, mapper,
        LQPConfiguration.getInstance()
            .getMicroClusterName());

//    if(combiner != null && action.getData().getObject("operator").containsField("combine")){
//      ((LeadsMapperCallable)mapperCallable).setCombiner(combiner);
//    }

  }
  @Override
  public void setupReduceCallable(){
    conf.putString("output", getOutput());
//    intermediateCache = (BasicCache) manager.getPersisentCache(intermediateCacheName);
//    log.error("ReducerIntermediate " + intermediateCache.size());
    //create Intermediate cache name for data on the same Sites as outputCache
    intermediateDataCache = (BasicCache) manager.getPersisentCache(intermediateCacheName+".data");
//    log.error("ReducerIntermediateData " + intermediateDataCache.size());
    //create Intermediate  keys cache name for data on the same Sites as outputCache;
//    keysCache = (BasicCache)manager.getPersisentCache(intermediateCacheName+".keys");
//    log.error("ReducerIntermediateKeys " + keysCache.size());
    //createIndexCache for getting all the nodes that contain values with the same key! in a mc
//    indexSiteCache = (BasicCache)manager.getPersisentCache(intermediateCacheName+".indexed");
    //    indexSiteCache = (BasicCache)manager.getIndexedPersistentCache(intermediateCacheName+".indexed");
//    log.error("ReducerIntermediateSite " + indexSiteCache.size());
    outputCache = (BasicCache) manager.getPersisentCache(outputCacheName);
//    reduceInputCache = (Cache) keysCache;
    collector = new LeadsCollector(0, outputCache.getName());
    inputCache = (Cache) intermediateDataCache;
    reduceInputCache = inputCache;
//    inputCache = (Cache) keysCache;
    reducerCallable =  new LeadsReducerCallable(outputCache.getName(), reducer,
                                                                         intermediateCacheName);

  }

  @Override
  public void setupReduceLocalCallable() {
    // TODO(ap0n): conf.putString("output", getOutput());
    intermediateLocalDataCache = (BasicCache) manager.getPersisentCache(intermediateLocalCacheName
        + ".data");
    log.error("ReducerIntermediateLocalData " + intermediateLocalDataCache.size());
//    outputCache = (BasicCache) manager.getPersisentCache(intermediateCacheName);
    reduceLocalInputCache = (Cache) intermediateLocalDataCache;

    collector = new LeadsCollector(1000, intermediateCacheName);

    reducerLocalCallable = new LeadsLocalReducerCallable(intermediateCacheName, localReducer,
        intermediateLocalCacheName, LQPConfiguration
        .getInstance().getMicroClusterName());

    System.err.println("reduInput " + reduceLocalInputCache.size());
    combiner = null;
  }

}
