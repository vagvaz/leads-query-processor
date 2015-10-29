package eu.leads.processor.infinispan;

import org.infinispan.Cache;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class LeadsMapperCallable<K, V, kOut, vOut> extends LeadsBaseCallable<K, V> implements

    Serializable {

  /**
   * tr
   */
  private static final long serialVersionUID = 1242145345234214L;

  //	private LeadsCollector<kOut, vOut> collector = null;

  private Set<K> keys;
  private LeadsMapper<K, V, kOut, vOut> mapper = null;
  String site;

  public LeadsMapperCallable() {
    super();
  }

  public LeadsMapperCallable(Cache<K, V> cache, LeadsCollector<kOut, vOut> collector,
      LeadsMapper<K, V, kOut, vOut> mapper, String site) {
    super("{}", collector.getCacheName());
    this.site = site;
    this.collector = collector;
    this.mapper = mapper;
  }

  public LeadsMapper<K, V, kOut, vOut> getMapper() {
    Class<?> mapperClass = mapper.getClass();
    Constructor<?> constructor = null;
    try {
      constructor = mapperClass.getConstructor();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    LeadsMapper result = null;
    try {
      result = (LeadsMapper) constructor.newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    result.setConfigString(mapper.configString);

    return result;
  }

  public void setMapper(LeadsMapper<K, V, kOut, vOut> mapper) {
    this.mapper = mapper;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  @Override public void setEnvironment(Cache<K, V> cache, Set<K> inputKeys) {
    super.setEnvironment(cache, inputKeys);
    //		this.cache =  cache;
    //		this.keys = inputKeys;
    //		collector.initializeCache(cache.getCacheManager());
  }

  @Override public void initialize() {
    //    collector.initializeCache(inputCache.getCacheManager());
    super.initialize();
    collector.setOnMap(true);
    collector.setManager(this.embeddedCacheManager);
    collector.setEmanager(emanager);
    collector.setSite(site);
    collector.initializeCache(inputCache.getName(), imanager);
    mapper.initialize();
  }


  @Override public void executeOn(K key, V value) {
    mapper.map(key, value, collector);
  }

  @Override public void finalizeCallable() {
    mapper.finalizeTask();
    collector.finalizeCollector();
    //    EnsembleCacheUtils.waitForAllPuts();
    super.finalizeCallable();
  }
}
