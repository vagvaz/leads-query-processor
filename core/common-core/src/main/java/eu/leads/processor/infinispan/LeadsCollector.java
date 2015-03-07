package eu.leads.processor.infinispan;


import eu.leads.processor.common.infinispan.ClusterInfinispanManager;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import org.infinispan.Cache;
import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.manager.EmbeddedCacheManager;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class LeadsCollector<KOut, VOut> implements Collector<KOut, VOut>,
                                                     Serializable {

  private static final long serialVersionUID = -602082107893975415L;
  private final AtomicInteger emitCount;
  private final int maxCollectorSize;
  private transient Cache storeCache;
  protected transient Cache  keysCache;
  protected transient Cache intermediateDataCache;
  protected transient Cache indexSiteCache;
  protected transient  Cache counterCache;
  private transient InfinispanManager imanager;
   private transient EmbeddedCacheManager manager;
  private boolean onMap = true;
   private String site;
   private String node;
  private String cacheName;
   private ComplexIntermediateKey baseIntermKey;
   private IndexedComplexIntermediateKey  baseIndexedKey;


  public LeadsCollector(int maxCollectorSize,
                         Cache<KOut, List<VOut>> collectorCache) {
    super();

    emitCount = new AtomicInteger();
    this.maxCollectorSize = maxCollectorSize;
    storeCache = collectorCache;
    cacheName = collectorCache.getName();
  }

  public LeadsCollector(int maxCollectorSize, String cacheName,InfinispanManager manager){
    this.maxCollectorSize = maxCollectorSize;
    emitCount = new AtomicInteger();
    this.imanager = manager;
    this.cacheName = cacheName;
    storeCache = (Cache<KOut, List<VOut>>) this.imanager.getPersisentCache(cacheName);
  }

  public Cache<KOut, List<VOut>> getCache() {
    return storeCache;
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
      storeCache = (Cache) imanager.getPersisentCache(cacheName);
      if(onMap) {
         intermediateDataCache = (Cache) imanager.getPersisentCache(storeCache.getName() + ".data");
         //create Intermediate  keys cache name for data on the same Sites as outputCache;
         keysCache = (Cache) imanager.getPersisentCache(storeCache.getName() + ".keys");
         //createIndexCache for getting all the nodes that contain values with the same key! in a mc
         indexSiteCache = (Cache) imanager.getIndexedPersistentCache(storeCache.getName() + ".indexed");
         counterCache = manager.getCache(manager.getAddress().toString() + ".counters");
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
          EnsembleCacheUtils.putToCache(keysCache,baseIndexedKey.getUniqueKey(), baseIndexedKey.getUniqueKey());
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
      storeCache.put(key,value);
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

  public void emit(Map<KOut, List<VOut>> combined) {
    for (Entry<KOut, List<VOut>> e : combined.entrySet()) {
      KOut k = e.getKey();
      List<VOut> values = e.getValue();
      for (VOut v : values) {
        emit(k, v);
      }
    }
  }

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
