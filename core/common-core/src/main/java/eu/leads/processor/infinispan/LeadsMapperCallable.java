package eu.leads.processor.infinispan;

import eu.leads.processor.common.LeadsCollector;
import org.infinispan.Cache;

import java.io.Serializable;
import java.util.Set;

public class LeadsMapperCallable<K, V, kOut, vOut> extends LeadsBaseCallable<K,V> implements

		 Serializable {

	/**
	 * tr
	 */
	private static final long serialVersionUID = 1242145345234214L;
	 
	LeadsCollector<kOut, vOut> collector = null;

	Set<K> keys;
	LeadsMapper<K, V, kOut, vOut> mapper = null;
   String site;

	public LeadsMapperCallable(Cache<K, V> cache,
			LeadsCollector<kOut, vOut> collector,
			LeadsMapper<K, V, kOut, vOut> mapper,String site) {
    super("{}",collector.getCache().getName());
      this.site = site;
		this.collector = collector;
		this.mapper = mapper;
	}

  @Override
	public void setEnvironment(Cache<K, V> cache, Set<K> inputKeys) {
    super.setEnvironment(cache,inputKeys);
//		this.cache =  cache;
//		this.keys = inputKeys;
//		collector.initializeCache(cache.getCacheManager());
	}

  @Override
  public void initialize(){
//    collector.initializeCache(inputCache.getCacheManager());
    collector.initializeCache(imanager);
  }

//	public String call() throws Exception {
//
//		if (mapper == null) {
//			System.out.println(" Mapper not initialized ");
//		} else {
//         mapper.setCacheManager(cache.getCacheManager());
//			String result = imanager.getCacheManager().getAddress().toString();
//			final ClusteringDependentLogic cdl = cache.getAdvancedCache().getComponentRegistry().getComponent(ClusteringDependentLogic.class);
//			for(Object key : cache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).keySet()){
//				if(!cdl.localNodeIsPrimaryOwner(key))
//					continue;
//				V value = cache.get(key);
//				if (value != null) {
//					mapper.map((K)key, value, collector);
//				}
//			}
//
//			return result;
//		}
//		return null;
//	}

  @Override public void executeOn(K key, V value) {
    mapper.map(key,value,collector);
  }

  @Override public void finalize() {
    super.finalize();
    mapper.finalize();
  }
}