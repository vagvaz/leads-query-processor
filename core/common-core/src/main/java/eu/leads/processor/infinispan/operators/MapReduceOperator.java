package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.LeadsCollector;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.*;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.infinispan.LeadsMapper;
import eu.leads.processor.infinispan.LeadsMapperCallable;
import eu.leads.processor.infinispan.LeadsReducerCallable;
import eu.leads.processor.infinispan.LeadsReducer;
import eu.leads.processor.core.net.Node;
import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.vertx.java.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by tr on 19/9/2014.
 */
public abstract class MapReduceOperator extends BasicOperator{
    protected transient Cache<?, ?> inputCache;
    protected transient Cache<?, List<?>> intermediateCache;
    protected transient Cache<?, ?> outputCache;
    protected transient Cache  keysCache;
    protected transient Cache intermediateDataCache;
    protected transient Cache indexSiteCache;
    protected String inputCacheName;
    protected String outputCacheName;
    protected String intermediateCacheName;
    protected LeadsMapper<?, ?, ?, ?> mapper;
    protected LeadsCollector<?, ?> collector;
    protected LeadsReducer<?,?> reducer;


    public MapReduceOperator(Node com, InfinispanManager persistence, LogProxy log, Action action) {

       super(com,persistence,log,action);
       inputCacheName = getInput();
       outputCacheName = action.getData().getObject("operator").getString("id");
       intermediateCacheName = action.getData().getObject("operator").getString("id")+".intermediate";
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
        inputCache = (Cache<?, ?>) manager.getPersisentCache(inputCacheName);
        intermediateCache = (Cache<?, List<?>>) manager
                                                          .getPersisentCache(intermediateCacheName);
        //create Intermediate cache name for data on the same Sites as outputCache
        intermediateDataCache = (Cache) manager.getPersisentCache(intermediateCacheName+".data");
        //create Intermediate  keys cache name for data on the same Sites as outputCache;
        keysCache = (Cache)manager.getPersisentCache(intermediateCacheName+".keys");
        //createIndexCache for getting all the nodes that contain values with the same key! in a mc
        indexSiteCache = (Cache)manager.getIndexedPersistentCache(intermediateCacheName+".indexed");
          outputCache = (Cache<?, ?>) manager.getPersisentCache(outputCacheName);
          collector = new LeadsCollector(0, intermediateCache);
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
       if(reducer == null)
          reducer = new LeadsReducer("");
       System.out.println("RUN MR on " + inputCache.getName());
//       MapReduceTask<String,String,String,String> task = new MapReduceTask(inputCache);
//               .reducedWith((org.infinispan.distexec.mapreduce.Reducer<String, String>) reducer);
//       task.timeout(1, TimeUnit.HOURS);
//       task.execute();
       DistributedExecutorService des = new DefaultExecutorService(inputCache);
       LeadsMapperCallable mapperCallable = new LeadsMapperCallable(inputCache,collector,mapper,
                                                                     LQPConfiguration.getInstance().getMicroClusterName());

       List<Future<?>> res = des.submitEverywhere(mapperCallable);
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

       if(reducer != null) {
          LeadsReducerCallable reducerCacllable = new LeadsReducerCallable(outputCache, reducer);
          DistributedExecutorService des_inter = new DefaultExecutorService(intermediateCache);
          List<Future<?>> reducers_res;
          res = des_inter
                        .submitEverywhere(reducerCacllable);
          try {
             if (res != null) {
                for (Future<?> result : res) {
                   result.get();
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
    }
}
