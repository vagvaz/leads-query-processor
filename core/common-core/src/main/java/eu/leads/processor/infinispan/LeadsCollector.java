package eu.leads.processor.infinispan;


import eu.leads.processor.common.infinispan.ClusterInfinispanManager;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.manager.EmbeddedCacheManager;

import java.io.Serializable;
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
    storeCache = (BasicCache) emanager.getCache(cacheName);
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

  public void initializeCache(InfinispanManager imanager){
    this.imanager = imanager;
//    storeCache = (Cache) imanager.getPersisentCache(cacheName);
    storeCache = emanager.getCache(cacheName);
    if(onMap) {
      intermediateDataCache = (BasicCache) emanager.getCache(storeCache.getName() + ".data");
      //create Intermediate  keys cache name for data on the same Sites as outputCache;
      keysCache = (BasicCache) emanager.getCache(storeCache.getName() + ".keys");
      //createIndexCache for getting all the nodes that contain values with the same key! in a mc
      indexSiteCache = (BasicCache) emanager.getCache(storeCache.getName() + ".indexed");
      counterCache = manager.getCache(cacheName+"."+manager.getAddress().toString() + ".counters");
      baseIndexedKey = new IndexedComplexIntermediateKey(site, manager.getAddress().toString());
      baseIntermKey = new ComplexIntermediateKey(site, manager.getAddress().toString());
    }
  }
  public void emit(KOut key, VOut value) {

    if(onMap) {
      //      List<VOut> list = (List<VOut>) storeCache.get(key);

      //      if (list == null) {
      //        list = new LinkedList<>();
      //        //storeCache.put(key, list);
      //
      //      }
      //      list.add(value);
      //      emitCount.incrementAndGet();
      Integer currentCount = (Integer) counterCache.get(key);
      if(currentCount == null)
      {
        currentCount = new Integer(1);
        baseIndexedKey.setKey(key.toString());
        EnsembleCacheUtils.putIfAbsentToCache(keysCache, key, key);
        EnsembleCacheUtils.putToCache(indexSiteCache,baseIndexedKey.getUniqueKey(), baseIndexedKey);
      }
      else{
        currentCount = currentCount+1;
      }
      counterCache.put(key.toString(),currentCount);
      baseIntermKey.setKey(key.toString());
      baseIntermKey.setCounter(currentCount);
      EnsembleCacheUtils.putToCache(intermediateDataCache,baseIntermKey,value);
    }
    else{
      EnsembleCacheUtils.putToCache(storeCache, key, value);
      emitCount.incrementAndGet();
    }
    // if (isOverflown() && mcc.hasCombiner()) {
    // combine(mcc, this);
    // }
    Set<Object> keys = new HashSet<>();
    keys.add(key);
  }


  public void initializeCache(EmbeddedCacheManager manager) {
    imanager = new ClusterInfinispanManager(manager);
    storeCache = (Cache) imanager.getPersisentCache(cacheName);
  }


  public void reset() {
    storeCache.clear();
    emitCount.set(0);
  }

  //  public void emit(Map<KOut, List<VOut>> combined) {
  //    for (Entry<KOut, List<VOut>> e : combined.entrySet()) {
  //      KOut k = e.getKey();
  //      List<VOut> values = e.getValue();
  //      for (VOut v : values) {
  //        emit(k, v);
  //      }
  //    }
  //  }

  public boolean isOverflown() {
    return emitCount.get() > maxCollectorSize;
  }

  //  public void setCombiner(eu.leads.processor.infinispan.LeadsCombiner combiner) {
  //    this.combiner = combiner;
  //  }
  //
  //  public LeadsCombiner getCombiner() {
  //    return combiner;
  //  }
}