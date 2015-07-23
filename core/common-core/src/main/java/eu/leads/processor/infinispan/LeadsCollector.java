package eu.leads.processor.infinispan;


import eu.leads.processor.common.infinispan.ClusterInfinispanManager;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class LeadsCollector<KOut, VOut> implements Collector<KOut, VOut>,
                                                     Serializable {

  private static final long serialVersionUID = -602082107893975415L;
  private final AtomicInteger emitCount;
  private final int maxCollectorSize;
  private transient BasicCache storeCache;
  protected transient BasicCache  keysCache;
  protected transient BasicCache intermediateDataCache;
  protected transient BasicCache indexSiteCache;
  protected transient  Cache counterCache;
  private transient InfinispanManager imanager;
  private transient EmbeddedCacheManager manager;
  private transient EnsembleCacheManager emanager;
  private transient Logger log = null;
  private boolean onMap = true;
  private String site;
  private String node;
  private String cacheName;
  private ComplexIntermediateKey baseIntermKey;
  private IndexedComplexIntermediateKey  baseIndexedKey;


  public LeadsCollector(int maxCollectorSize,
                         String collectorCacheName) {
    super();

    emitCount = new AtomicInteger();
    this.maxCollectorSize = maxCollectorSize;
    cacheName = collectorCacheName;
  }

  public LeadsCollector(int maxCollectorSize, String cacheName,InfinispanManager manager){
    this.maxCollectorSize = maxCollectorSize;
    emitCount = new AtomicInteger();
    this.imanager = manager;
    this.cacheName = cacheName;
    storeCache = (BasicCache) emanager.getCache(cacheName,new ArrayList<>(emanager.sites()),
        EnsembleCacheManager.Consistency.DIST);
//    storeCache = (BasicCache) this.imanager.getPersisentCache(cacheName);
  }
  public Cache getCounterCache() {
    return counterCache;
  }

  public void setCounterCache(Cache counterCache) {
    this.counterCache = counterCache;
  }

  public BasicCache getIndexSiteCache() {
    return indexSiteCache;
  }

  public void setIndexSiteCache(BasicCache indexSiteCache) {
    this.indexSiteCache = indexSiteCache;
  }

  public BasicCache getIntermediateDataCache() {
    return intermediateDataCache;
  }

  public void setIntermediateDataCache(BasicCache intermediateDataCache) {
    this.intermediateDataCache = intermediateDataCache;
  }

  public BasicCache getKeysCache() {
    return keysCache;
  }

  public void setKeysCache(EnsembleCache keysCache) {
    this.keysCache = keysCache;
  }

  public BasicCache getCache() {
    return storeCache;
  }

  public EnsembleCacheManager getEmanager() {
    return emanager;
  }

  public void setEmanager(EnsembleCacheManager emanager) {
    this.emanager = emanager;
  }

  public EmbeddedCacheManager getManager() {
    return manager;
  }

  public void setManager(EmbeddedCacheManager manager) {
    this.manager = manager;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public String getNode() {
    return node;
  }

  public void setNode(String node) {
    this.node = node;
  }
  //  private LeadsCombiner combiner;

  public String getCacheName() {
    return cacheName;
  }

  public void setCacheName(String cacheName) {
    this.cacheName = cacheName;
  }

  public boolean isOnMap() {
    return onMap;
  }

  public void setOnMap(boolean onMap) {
    this.onMap = onMap;
  }

  public InfinispanManager getImanager() {
    return imanager;
  }

  public void setImanager(InfinispanManager imanager) {
    this.imanager = imanager;
  }

  public void initializeCache(String inputCacheName,InfinispanManager imanager){
    this.imanager = imanager;
    log = LoggerFactory.getLogger(LeadsCollector.class);
    storeCache = emanager.getCache(cacheName,new ArrayList<>(emanager.sites()),
        EnsembleCacheManager.Consistency.DIST);
    if(onMap) {
      intermediateDataCache = (BasicCache) emanager.getCache(storeCache.getName() + ".data",new ArrayList<>(emanager.sites()),
          EnsembleCacheManager.Consistency.DIST);
      //create Intermediate  keys cache name for data on the same Sites as outputCache;
      keysCache = (BasicCache) emanager.getCache(storeCache.getName() + ".keys",new ArrayList<>(emanager.sites()),
          EnsembleCacheManager.Consistency.DIST);
      //createIndexCache for getting all the nodes that contain values with the same key! in a mc
      indexSiteCache = (BasicCache) emanager.getCache(storeCache.getName() + ".indexed",new ArrayList<>(emanager.sites()),
          EnsembleCacheManager.Consistency.DIST);
      counterCache = manager.getCache(storeCache.getName()+"."+inputCacheName+"."+manager.getAddress().toString()
                                        + ".counters");
      baseIndexedKey = new IndexedComplexIntermediateKey(site, manager.getAddress().toString(),inputCacheName);
      baseIntermKey = new ComplexIntermediateKey(site, manager.getAddress().toString(),inputCacheName);
    }
  }
  public void emit(KOut key, VOut value) {

    if(onMap) {

      Integer currentCount = (Integer) counterCache.get(key);
      if(currentCount == null)
      {
        currentCount = new Integer(0);
        baseIndexedKey.setKey(key.toString());
//        if(LQPConfiguration.getInstance().getConfiguration().getBoolean("processor.validate.intermediate")){
//          IndexedComplexIntermediateKey ik = new IndexedComplexIntermediateKey(baseIndexedKey.getSite(),baseIndexedKey.getNode(),baseIndexedKey.getCache(),key.toString());
//          Object o = indexSiteCache.get(ik.getUniqueKey());
//          assert (o.equals(baseIndexedKey));
//        }
      }
      else{
        currentCount = currentCount+1;
      }
      counterCache.put(key.toString(), currentCount);
      baseIntermKey.setKey(key.toString());
      baseIntermKey.setCounter(currentCount);
      ComplexIntermediateKey newKey = new ComplexIntermediateKey(baseIntermKey.getSite(),baseIntermKey.getNode(),key.toString(),baseIntermKey.getCache(),currentCount);
      EnsembleCacheUtils.putToCache(intermediateDataCache,newKey,value);
      if(LQPConfiguration.getInstance().getConfiguration().getBoolean("processor.validate.intermediate")){
        ComplexIntermediateKey v = new ComplexIntermediateKey(baseIntermKey.getSite(),baseIntermKey.getNode(),key.toString(),baseIntermKey.getCache(),currentCount);
        Object o = intermediateDataCache.get(v);
        assert(o.equals(value));
      }
    }
    else{
      EnsembleCacheUtils.putToCache(storeCache, key, value);
    }
  }


  public void initializeCache(EmbeddedCacheManager manager) {
    imanager = new ClusterInfinispanManager(manager);
    storeCache = (Cache) imanager.getPersisentCache(cacheName);
  }


  public void reset() {
    storeCache.clear();
    emitCount.set(0);
  }

  public boolean isOverflown() {
    return emitCount.get() > maxCollectorSize;
  }

}
