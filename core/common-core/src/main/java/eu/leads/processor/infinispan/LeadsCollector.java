package eu.leads.processor.infinispan;


import eu.leads.processor.common.infinispan.EnsembleCacheUtilsSingle;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.plan.LeadsNodeType;
import eu.leads.processor.infinispan.operators.JoinMapper;
import eu.leads.processor.infinispan.operators.mapreduce.GroupByMapper;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class LeadsCollector<KOut, VOut> implements Collector<KOut, VOut>, Serializable {

  private static final long serialVersionUID = -602082107893975415L;
  private int emitCount = 0;
  private int maxCollectorSize;
  private transient BasicCache storeCache;
  protected transient BasicCache intermediateDataCache;
  protected transient Cache inputCache;



  //  protected transient  Cache counterCache;
  private transient Integer counter = 0;
  private transient InfinispanManager imanager;
  private transient EmbeddedCacheManager manager;
  private transient EnsembleCacheManager emanager;
  private transient Logger log = null;
  private transient EnsembleCacheUtilsSingle ensembleCacheUtilsSingle;
  private boolean onMap = true;
  private String site;
  private String node;
  private String cacheName;
  private ComplexIntermediateKey baseIntermKey;
  //  private ComplexIntermediateKey currentKey;
  private transient volatile Object mutex;
  private transient LeadsBaseCallable nextCallable;
  private String ensembleHost;
  private boolean isReduceLocal;
  //  private IndexedComplexIntermediateKey  baseIndexedKey;



  public LeadsCollector(int maxCollectorSize, String collectorCacheName) {
    //    emitCount = new AtomicInteger();
    this.maxCollectorSize = maxCollectorSize;
    cacheName = collectorCacheName;
  }

  public LeadsCollector(LeadsCollector other) {
    //    this.counterCache = other.counterCache;
    //    this.counter = other.counter;
    this.imanager = other.imanager;
    this.manager = other.manager;
    this.emanager = other.emanager;
    this.log = other.log;
    this.onMap = other.onMap;
    this.site = other.site;
    this.node = other.node;
    this.cacheName = other.cacheName;
    this.ensembleHost = other.ensembleHost;
    this.counter = 0;
    this.isReduceLocal = other.isReduceLocal;
    //    this.combiner = other.combiner;
    this.baseIntermKey = new ComplexIntermediateKey(other.baseIntermKey);
    this.maxCollectorSize = other.maxCollectorSize;
    //    this.currentKey = other.currentKey;
    this.mutex = new Object();
    this.nextCallable = null;
    //    this.ensembleCacheUtilsSingle = new EnsembleCacheUtilsSingle();
  }

  public LeadsCollector(int maxCollectorSize, String cacheName, InfinispanManager manager) {
    //    this.maxCollectorSize = maxCollectorSize;
    //    emitCount = new AtomicInteger();
    this.imanager = manager;
    this.cacheName = cacheName;
    storeCache = (BasicCache) emanager
        .getCache(cacheName, new ArrayList<>(emanager.sites()), EnsembleCacheManager.Consistency.DIST);
    //    storeCache = (BasicCache) this.imanager.getPersisentCache(cacheName);
  }
  //  public Cache getCounterCache() {
  //    return counterCache;
  //  }

  //  public void setCounterCache(Cache counterCache) {
  //    this.counterCache = counterCache;
  //  }

  public BasicCache getIntermediateDataCache() {
    return intermediateDataCache;
  }

  public void setIntermediateDataCache(BasicCache intermediateDataCache) {
    this.intermediateDataCache = intermediateDataCache;
  }

  public BasicCache getInputCache() {
    return inputCache;
  }

  public void setInputCache(Cache inputCache) {
    this.inputCache = inputCache;
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

  public int getEmitCount() {
    return emitCount;
  }

  public void setEmitCount(int emitCount) {
    this.emitCount = emitCount;
  }

  public int getMaxCollectorSize() {
    return maxCollectorSize;
  }

  public void setMaxCollectorSize(int maxCollectorSize) {
    this.maxCollectorSize = maxCollectorSize;
  }

  public void initializeCache(String inputCacheName, InfinispanManager imanager) {
    ensembleCacheUtilsSingle = new EnsembleCacheUtilsSingle();
    ensembleCacheUtilsSingle.initialize(emanager);
    counter = 0;
    this.imanager = imanager;
    log = LoggerFactory.getLogger(LeadsCollector.class);
    node =imanager.getMemberName().toString();
    if (site == null) {
      LQPConfiguration.getInstance().getMicroClusterName();
    }
    if (onMap) {
      intermediateDataCache = (BasicCache) emanager
          .getCache(cacheName + ".data", new ArrayList<>(emanager.sites()), EnsembleCacheManager.Consistency.DIST);
      baseIntermKey = new ComplexIntermediateKey(site, manager.getAddress().toString() + UUID.randomUUID().toString(),
          inputCacheName);
      mutex = new Object();
    } else {
      storeCache =
          emanager.getCache(cacheName, new ArrayList<>(emanager.sites()), EnsembleCacheManager.Consistency.DIST);
    }
  }

  public void emit(KOut key, VOut value) {
    if (nextCallable != null) {
      nextCallable.executeOn(key, value);
      return;
    }
    if (onMap) {
      //      Integer currentCount = -1;
      synchronized (mutex) {
        //         currentCount = (Integer) counterCache.get(key);
        //        if (currentCount == null) {
        //          currentCount = new Integer(0);
        //        baseIndexedKey.setKey(key.toString());
        //        if(LQPConfiguration.getInstance().getConfiguration().getBoolean("processor.validate.intermediate")){
        //          IndexedComplexIntermediateKey ik = new IndexedComplexIntermediateKey(baseIndexedKey.getSite(),baseIndexedKey.getNode(),baseIndexedKey.getCache(),key.toString());
        //          Object o = indexSiteCache.get(ik.getUniqueKey());
        //          assert (o.equals(baseIndexedKey));
        //        }
        //        } else {
        //          currentCount = currentCount + 1;
        counter++;
        //          currentCount = counter;
      }
      //        counterCache.put(key, currentCount);
      //      }
      //      baseIntermKey.setKey(key.toString());
      //      baseIntermKey.setCounter(currentCount);
      //      baseIntermKey.setCounter(currentCount);
      ComplexIntermediateKey newKey =
          new ComplexIntermediateKey(baseIntermKey.getSite(), baseIntermKey.getNode(), key.toString(),
              baseIntermKey.getCache(), counter);
      //      System.err.println("WRITING " + baseIntermKey + " " + baseIntermKey.asString());
      ensembleCacheUtilsSingle.putToCache(intermediateDataCache, newKey, value);
      //      if(LQPConfiguration.getInstance().getConfiguration().getBoolean("processor.validate.intermediate")){
      //        ComplexIntermediateKey v = new ComplexIntermediateKey(baseIntermKey.getSite(),baseIntermKey.getNode(),key.toString(),baseIntermKey.getCache(),currentCount);
      //        Object o = intermediateDataCache.get(v);
      //        assert(o.equals(value));
      //      }
    } else {
      ensembleCacheUtilsSingle.putToCache(storeCache, key, value);
    }
  }

  public void reset() {
    storeCache.clear();
    //    emitCount.set(0);
  }

  public boolean isOverflown() {
    return emitCount > maxCollectorSize;
  }

  public void initializeNextCallable(JsonObject conf) {
    if (conf.getString("next.type").equals(LeadsNodeType.GROUP_BY.toString())) {

      nextCallable = new LeadsMapperCallable(null, new LeadsCollector(0, cacheName),
          new GroupByMapper(conf.getObject("next").getObject("configuration").toString()), site);
      nextCallable.setEnsembleHost(ensembleHost);
      nextCallable.setEnvironment(inputCache, null);
      //      nextCallable.initialize();
    } else if (conf.getString("next.type").equals(LeadsNodeType.JOIN.toString())) {
      nextCallable = new LeadsMapperCallable(null, new LeadsCollector(0, cacheName),
          new JoinMapper(conf.getObject("next").getObject("configuration").toString()), site);
      nextCallable.setEnsembleHost(ensembleHost);
      nextCallable.setEnvironment(inputCache, null);
      //      nextCallable.initialize();
    } else if (conf.getString("next.type").equals(LeadsNodeType.SORT.toString())) {
      nextCallable = null;
      System.err.println("SORT SCAN NOT IMPLEMENTED YET");
    } else {
      nextCallable = null;
      System.err.println(conf.getString("next.type") + " SCAN NOT IMPLEMENTED YET");
    }

  }

  public void finalizeCollector() {
    if (nextCallable != null) {
      nextCallable.finalizeCallable();
    }
    try {
      ensembleCacheUtilsSingle.waitForAllPuts();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setEnsembleHost(String ensembleHost) {
    this.ensembleHost = ensembleHost;
  }

  public String getEnsembleHost() {
    return ensembleHost;
  }

  public void setIsReduceLocal(boolean isReduceLocal) {
    this.isReduceLocal = isReduceLocal;
  }

  public boolean isReduceLocal() {
    return isReduceLocal;
  }

  public void setReduceLocal(boolean isReduceLocal) {
    this.isReduceLocal = isReduceLocal;
  }
}
