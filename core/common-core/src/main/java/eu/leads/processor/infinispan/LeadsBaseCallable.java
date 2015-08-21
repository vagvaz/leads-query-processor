package eu.leads.processor.infinispan;

import eu.leads.processor.common.infinispan.ClusterInfinispanManager;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.infinispan.SyncPutRunnable;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.conf.LQPConfiguration;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
import org.infinispan.context.Flag;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.filter.KeyValueFilter;
import org.infinispan.interceptors.locking.ClusteringDependentLogic;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by vagvaz on 2/18/15.
 */
public  abstract class LeadsBaseCallable <K,V> implements LeadsCallable<K,V>,

  DistributedCallable<K, V, String>, Serializable {
  protected String configString;
  protected String output;
  transient protected JsonObject conf;
  transient protected boolean isInitialized;
  transient protected EmbeddedCacheManager embeddedCacheManager;
  transient protected InfinispanManager imanager;
  transient protected Set<K> keys;
  transient protected  Cache<K,V> inputCache;
  transient protected EnsembleCache outputCache;
  protected String ensembleHost;
//  transient protected RemoteCache outputCache;
//  transient protected RemoteCache ecache;
//  transient protected RemoteCacheManager emanager;
  transient protected EnsembleCacheManager emanager;
  transient protected EnsembleCache ecache;
  transient protected ThreadPoolExecutor executor;
  transient protected ConcurrentLinkedDeque<ExecuteRunnable> runnables;
  transient protected volatile Object runableMutex;
  transient Logger profilerLog;
  protected ProfileEvent profCallable;
  transient protected int threadBatch;
  public LeadsBaseCallable(String configString, String output){
    this.configString = configString;
    this.output = output;
    profilerLog  = LoggerFactory.getLogger("###PROF###" +  this.getClass().toString());
    profCallable = new ProfileEvent("Callable Construct" + this.getClass().toString(),profilerLog);
  }


  public String getEnsembleHost() {
    return ensembleHost;
  }

  public void setEnsembleHost(String ensembleHost) {
    this.ensembleHost = ensembleHost;
  }


//  public static RemoteCacheManager createRemoteCacheManager() {
//    ConfigurationBuilder builder = new ConfigurationBuilder();
//    builder.addServer().host(LQPConfiguration.getConf().getString("node.ip")).port(11222);
//    return new RemoteCacheManager(builder.build());
//  }
  @Override public void setEnvironment(Cache<K, V> cache, Set<K> inputKeys) {
    profilerLog  = LoggerFactory.getLogger("###PROF###" +  this.getClass().toString());
    profCallable.setProfileLogger(profilerLog);
    if(profCallable!=null) {
      profCallable.end("setEnv");
      profCallable.start("setEnvironment Callable ");
    }else
      profCallable = new ProfileEvent("setEnvironment Callable " + this.getClass().toString(),profilerLog);
    embeddedCacheManager = cache.getCacheManager();
    runableMutex = new Object();
    imanager = new ClusterInfinispanManager(embeddedCacheManager);
//    outputCache = (Cache) imanager.getPersisentCache(output);
    keys = inputKeys;
    this.inputCache = cache;
    ProfileEvent tmpprofCallable = new ProfileEvent("setEnvironment manager " + this.getClass().toString(),profilerLog);
    tmpprofCallable.start("Start LQPConfiguration");

    LQPConfiguration.initialize();
    tmpprofCallable.end();
    EnsembleCacheUtils.initialize();
    if(ensembleHost != null && !ensembleHost.equals("")) {
      tmpprofCallable.start("Start EnsemlbeCacheManager");
      profilerLog.error("EnsembleHost EXIST " + ensembleHost);
      System.err.println("EnsembleHost EXIST " + ensembleHost);
      emanager = new EnsembleCacheManager(ensembleHost);
//      emanager.start();
//      emanager = createRemoteCacheManager();
//      ecache = emanager.getCache(output,new ArrayList<>(emanager.sites()),
//          EnsembleCacheManager.Consistency.DIST);
    }
    else {
      profilerLog.error("EnsembleHost NULL");
      System.err.println("EnsembleHost NULL");
      tmpprofCallable.start("Start EnsemlbeCacheManager");
      emanager = new EnsembleCacheManager(LQPConfiguration.getConf().getString("node.ip") + ":11222");
//      emanager.start();
//            emanager = createRemoteCacheManager();
    }
    emanager.start();

    tmpprofCallable.end();
    tmpprofCallable.start("Get cache ");
    ecache = emanager.getCache(output,new ArrayList<>(emanager.sites()),
          EnsembleCacheManager.Consistency.DIST);
    tmpprofCallable.end();
      outputCache = ecache;
//outputCache =  emanager.getCache(output,new ArrayList<>(emanager.sites()),
//          EnsembleCacheManager.Consistency.DIST);

    initialize();
    profCallable.end("end_setEnv");
    threadBatch = LQPConfiguration.getInstance().getConfiguration().getInt(
        "node.ensemble.threads",64);
    long start = System.currentTimeMillis();
    executor = new ThreadPoolExecutor(threadBatch,30*threadBatch,5000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
    runnables = new ConcurrentLinkedDeque<>();
    for (int i = 0; i <= 30*threadBatch; i++) {
      runnables.add(new ExecuteRunnable(this));
    }
    long end  = System.currentTimeMillis();
    System.err.println("runnables created in " + (end-start));
  }

  public  ExecuteRunnable getRunnable(){
    ExecuteRunnable result = null;
//    synchronized (runableMutex){
      result = runnables.poll();
      while(result == null){
//        try {
//         Thread.sleep(1);
//        Thread.sleep(0,3000);
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
        Thread.yield();
        result = runnables.poll();
      }
//    }

    return result;
  }
  public  void addRunnable(ExecuteRunnable runnable){
//    synchronized (runableMutex){
      runnables.add(runnable);
//      runableMutex.notify();
//    }
  }


  @Override public String call() throws Exception {
    profCallable.end("call");
    if(!isInitialized){
      initialize();
    }
    profCallable.start("Call getComponent ()");
    final ClusteringDependentLogic cdl = inputCache.getAdvancedCache().getComponentRegistry().getComponent
                                                                                    (ClusteringDependentLogic.class);
    profCallable.end();
    profCallable.start("Iterate Over Local Data");
    ProfileEvent profExecute = new ProfileEvent("GetIteratble " + this.getClass().toString(),profilerLog);
    int count=0;
//    for(Object key : inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).keySet()) {
//      if (!cdl.localNodeIsPrimaryOwner(key))
//        continue;
    Object filter = new LocalDataFilter<K,V>(cdl);
    CloseableIterable iterable = inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).filterEntries(
        (KeyValueFilter<? super K, ? super V>) filter);
//        .converter((Converter<? super K, ? super V, ?>) filter);
    profExecute.end();
    profExecute.start("ISPNIter");
    try {
      for (Object object : iterable) {
        profExecute.end();
        Map.Entry<K, V> entry = (Map.Entry<K, V>) object;

        //      V value = inputCache.get(key);
        K key = (K) entry.getKey();
        V value = (V) entry.getValue();

        if (value != null) {
//          profExecute.start("ExOn" + (++count));
          ExecuteRunnable runable = this.getRunnable();
          runable.setKeyValue(key, value);
          executor.submit(runable);
//          executeOn((K) key, value);
//          profExecute.end();
        }
        profExecute.start("ISPNIter");
      }
      iterable.close();
    }
    catch(Exception e){
        iterable.close();
      profilerLog.error("Exception in LEADSBASEBACALLABE " + e.getClass().toString());
      PrintUtilities.logStackTrace(profilerLog,e.getStackTrace());
      }
    profCallable.end();
    finalizeCallable();
    return embeddedCacheManager.getAddress().toString();
  }

  public void initialize(){
    if(isInitialized)
      return;
    isInitialized = true;
    if(configString != null || configString.length() > 0)
      conf = new JsonObject(configString);
  }

  @Override public void finalizeCallable(){
    try {
      profCallable.start("finalizeBaseCallable");

      EnsembleCacheUtils.waitForAllPuts();
      while(executor.getActiveCount() > 1){
        Thread.sleep(100);
      }
      executor.shutdown();
//      emanager.stop();
//
//      ecache.stop();
//      outputCache.stop();
    }catch(Exception e){
        System.err.println("LEADS Base callable "+e.getClass().toString()+ " " + e.getMessage() + " cause ");
      profilerLog.error(("LEADS Base callable "+e.getClass().toString()+ " " + e.getMessage() + " cause "));
       PrintUtilities.logStackTrace(profilerLog,e.getStackTrace());
      }
    profCallable.end("finalizeBaseCallable");
  }

  public void outputToCache(Object key, Object value){
    EnsembleCacheUtils.putToCache(outputCache,key.toString(),value  );
  }
}
