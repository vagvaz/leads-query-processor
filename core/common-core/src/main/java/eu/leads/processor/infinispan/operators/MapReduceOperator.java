package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.infinispan.*;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.distexec.DistributedTask;
import org.infinispan.distexec.DistributedTaskBuilder;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by tr on 19/9/2014.
 */
public abstract class MapReduceOperator extends BasicOperator{
  protected transient BasicCache inputCache;
  protected transient BasicCache intermediateCache;
  protected transient BasicCache  outputCache;
  protected transient BasicCache  keysCache;
  protected transient BasicCache intermediateDataCache;
  protected transient BasicCache indexSiteCache;
  protected String inputCacheName;
  protected String outputCacheName;
  protected String intermediateCacheName;
  protected LeadsMapper<?, ?, ?, ?> mapper;
  protected LeadsCollector<?, ?> collector;
  protected LeadsReducer<?,?> reducer;
  protected String uuid;


  public MapReduceOperator(Node com, InfinispanManager persistence, LogProxy log, Action action) {

    super(com,persistence,log,action);
    inputCacheName = getInput();
    outputCacheName = action.getData().getObject("operator").getString("id");
    intermediateCacheName = action.getData().getObject("operator").getString("id")+".intermediate";
    uuid = UUID.randomUUID().toString();
  }

  public void setMapper(LeadsMapper<?, ?, ?, ?> mapper) {
    this.mapper = mapper;

  }

  public void setReducer(LeadsReducer<?, ?> reducer) {
    this.reducer = reducer;
  }

  @Override
  public void init(JsonObject config) {
    conf.putString("output",getOutput());
    inputCache = (BasicCache) manager.getPersisentCache(inputCacheName);
    intermediateCache = (BasicCache) manager.getPersisentCache(intermediateCacheName);
    //create Intermediate cache name for data on the same Sites as outputCache
    intermediateDataCache = (BasicCache) manager.getPersisentCache(intermediateCacheName+".data");
    //create Intermediate  keys cache name for data on the same Sites as outputCache;
    keysCache = (BasicCache)manager.getPersisentCache(intermediateCacheName+".keys");
    //createIndexCache for getting all the nodes that contain values with the same key! in a mc
    indexSiteCache = (BasicCache)manager.getPersisentCache(intermediateCacheName+".indexed");
//    indexSiteCache = (BasicCache)manager.getIndexedPersistentCache(intermediateCacheName+".indexed");
    outputCache = (BasicCache) manager.getPersisentCache(outputCacheName);
    reduceInputCache = (Cache) keysCache;
    collector = new LeadsCollector(0, intermediateCacheName);
  }

//  @Override
  public void run2() {
    long startTime = System.nanoTime();
    if(reducer == null)
      reducer = new LeadsReducer("");
    System.out.println("RUN MR on " + inputCache.getName());
    //       MapReduceTask<String,String,String,String> task = new MapReduceTask(inputCache);
    //               .reducedWith((org.infinispan.distexec.mapreduce.Reducer<String, String>) reducer);
    //       task.timeout(1, TimeUnit.HOURS);
    //       task.execute();

    DistributedExecutorService des = new DefaultExecutorService((Cache<?, ?>) inputCache);

    LeadsMapperCallable mapperCallable = new LeadsMapperCallable((Cache) inputCache,collector,mapper,
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
    if(reducer != null) {

      LeadsReducerCallable reducerCacllable = new LeadsReducerCallable(outputCache.getName(), reducer,
                                                                        intermediateCacheName);
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
    //Store Values for statistics
    updateStatistics(inputCache,null,outputCache);
    cleanup();
  }


  @Override /// Example do not use
  public void execute() {
    super.start();
  }

  @Override
  public void cleanup() {
    super.cleanup();
    intermediateCache.stop();
    indexSiteCache.stop();
    intermediateDataCache.stop();
    keysCache.stop();
  }

  @Override
  public boolean isSingleStage() {
    return false;
  }
  @Override
  public void createCaches(boolean isRemote, boolean executeOnlyMap, boolean executeOnlyReduce) {
    Set<String> targetMC = getTargetMC();
    for(String mc : targetMC){
      createCache(mc,getOutput());
      createCache(mc, intermediateCacheName);
      //create Intermediate cache name for data on the same Sites as outputCache
      createCache(mc,intermediateCacheName+".data");
      //create Intermediate  keys cache name for data on the same Sites as outputCache;
      createCache(mc,intermediateCacheName+".keys");
      //createIndexCache for getting all the nodes that contain values with the same key! in a mc
      createCache(mc,intermediateCacheName+".indexed");
//    indexSiteCache = (BasicCache)manager.getIndexedPersistentCache(intermediateCacheName+".indexed");

    }
  }
  @Override
  public void setupMapCallable(){
    mapperCallable = new LeadsMapperCallable((Cache) inputCache,collector,mapper,
                                   LQPConfiguration.getInstance().getMicroClusterName());
  }
  @Override
  public void setupReduceCallable(){
    reducerCallable =  new LeadsReducerCallable(outputCache.getName(), reducer,
                                                                         intermediateCacheName);
  }
}
