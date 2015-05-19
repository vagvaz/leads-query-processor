package eu.leads.processor.infinispan;

import eu.leads.processor.common.infinispan.ClusterInfinispanManager;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.TupleMarshaller;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.interceptors.locking.ClusteringDependentLogic;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

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

  transient Logger profilerLog;
  ProfileEvent profCallable;
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
      emanager = new EnsembleCacheManager(ensembleHost);
      emanager.start();
//      emanager = createRemoteCacheManager();
//      ecache = emanager.getCache(output,new ArrayList<>(emanager.sites()),
//          EnsembleCacheManager.Consistency.DIST);
    }
    else {
      tmpprofCallable.start("Start EnsemlbeCacheManager");
      emanager = new EnsembleCacheManager(LQPConfiguration.getConf().getString("node.ip") + ":11222");
      emanager.start();
//            emanager = createRemoteCacheManager();
    }
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
    profCallable.start("Call inputCacheSize " + inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).keySet().size());
    ProfileEvent profExecute = new ProfileEvent("Execute " + this.getClass().toString(),profilerLog);
    int count=0;
    for(Object key : inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).keySet()) {
      if (!cdl.localNodeIsPrimaryOwner(key))
        continue;
      profExecute.start("GetTuple" + (count+1));
      V value = inputCache.get(key);
      profExecute.end();
      if (value != null) {
        profExecute.start("ExOn" + (++count));
        executeOn((K) key, value);
        profExecute.end();
      }
    }
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
      EnsembleCacheUtils.waitForAllPuts();
      emanager.stop();
//
//      ecache.stop();
//      outputCache.stop();
    }catch(Exception e){
        System.err.println("LEADS Base callable "+e.getClass().toString()+ " " + e.getMessage() + " cause "
                             + e.getCause().toString());
      }
    profCallable.end("finalize");
  }

  public void outputToCache(Object key, Object value){
    EnsembleCacheUtils.putToCache(outputCache,key.toString(),value  );
  }
}
