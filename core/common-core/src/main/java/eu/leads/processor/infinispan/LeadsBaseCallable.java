package eu.leads.processor.infinispan;

import eu.leads.processor.common.infinispan.ClusterInfinispanManager;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.context.Flag;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.interceptors.locking.ClusteringDependentLogic;
import org.infinispan.manager.EmbeddedCacheManager;
import org.vertx.java.core.json.JsonObject;

import java.io.Serializable;
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

  public LeadsBaseCallable(String configString, String output){
    this.configString = configString;
    this.output = output;
  }

  public static RemoteCacheManager createRemoteCacheManager() {
    ConfigurationBuilder builder = new ConfigurationBuilder();
    builder.addServer().host(LQPConfiguration.getConf().getString("node.ip")).port(11222);
    return new RemoteCacheManager(builder.build());
  }
  @Override public void setEnvironment(Cache<K, V> cache, Set<K> inputKeys) {
    embeddedCacheManager = cache.getCacheManager();
    imanager = new ClusterInfinispanManager(embeddedCacheManager);
//    outputCache = (Cache) imanager.getPersisentCache(output);
    keys = inputKeys;
    this.inputCache = cache;
    if(ensembleHost != null && !ensembleHost.equals("")) {
      emanager = new EnsembleCacheManager(ensembleHost);
//      emanager = createRemoteCacheManager();
      ecache = emanager.getCache(output);
    }
    else {
      LQPConfiguration.initialize();
      emanager = new EnsembleCacheManager(LQPConfiguration.getConf().getString("node.ip") + ":11222");
//            emanager = createRemoteCacheManager();
    }

      ecache = emanager.getCache(output);
      outputCache =  emanager.getCache(output);

    initialize();
  }


  @Override public String call() throws Exception {
    if(!isInitialized){
      initialize();
    }
    final ClusteringDependentLogic cdl = inputCache.getAdvancedCache().getComponentRegistry().getComponent
                                                                                       (ClusteringDependentLogic.class);
    for(Object key : inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).keySet()) {
      if (!cdl.localNodeIsPrimaryOwner(key))
        continue;
      V value = inputCache.get(key);
      if (value != null) {
        executeOn((K)key, value);
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

	if(ecache!=null) {
    ecache.stop();
  }
	if(outputCache!=null )
      	  outputCache.stop();
	if(emanager!=null)
	  emanager.stop();
    }catch(Exception e){
        System.err.println("LEADS Base callable "+e.getClass().toString());
      e.printStackTrace();
      }
  }

  public void outputToCache(Object key, Object value){
    EnsembleCacheUtils.putToCache(outputCache,key.toString(),value  );
  }
}
