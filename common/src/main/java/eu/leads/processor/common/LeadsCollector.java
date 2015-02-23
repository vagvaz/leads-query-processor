package eu.leads.processor.common;


import eu.leads.processor.common.infinispan.ClusterInfinispanManager;
import eu.leads.processor.common.infinispan.InfinispanManager;
import org.infinispan.Cache;
import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.manager.EmbeddedCacheManager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class LeadsCollector<KOut, VOut> implements Collector<KOut, VOut>,
        Serializable {

    private static final long serialVersionUID = -602082107893975415L;
    private final AtomicInteger emitCount;
    private final int maxCollectorSize;
    private transient Cache storeCache;
    private transient InfinispanManager imanager;
    private boolean onMap = true;
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

  private String cacheName;


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

    public void emit(KOut key, VOut value) {

        if(onMap) {
          List<VOut> list = (List<VOut>) storeCache.get(key);

          if (list == null) {

            storeCache.put(key, list);
            storeCache.getAdvancedCache().applyDelta();
          }
          list.add(value);
          emitCount.incrementAndGet();
        }
      else{
          storeCache.put(key,value);
          emitCount.incrementAndGet();
        }
        // if (isOverflown() && mcc.hasCombiner()) {
        // combine(mcc, this);
        // }
    }


    public void initializeCache(EmbeddedCacheManager manager) {
       imanager = new ClusterInfinispanManager(manager);
        storeCache = (Cache) imanager.getPersisentCache(cacheName);
    }

  public void initializeCache(InfinispanManager imanager){
    this.imanager = imanager;
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
