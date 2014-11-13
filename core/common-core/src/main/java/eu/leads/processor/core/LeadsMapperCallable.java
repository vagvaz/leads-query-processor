package eu.leads.processor.core;

import eu.leads.processor.common.LeadsCollector;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.interceptors.locking.ClusteringDependentLogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LeadsMapperCallable<K, V, kOut, vOut> implements

		DistributedCallable<K, V, List<K>>, Serializable {

	/**
	 * tr
	 */
	private static final long serialVersionUID = 1242145345234214L;
	 
	LeadsCollector<kOut, vOut> collector = null;
	transient Cache<K, V> cache;
	Set<K> keys;
	LeadsMapper<K, V, kOut, vOut> mapper = null;

	public LeadsMapperCallable(Cache<K, V> cache,
			LeadsCollector<kOut, vOut> collector,
			LeadsMapper<K, V, kOut, vOut> mapper) {
		super();
		this.cache = cache;
		this.collector = collector;
		this.mapper = mapper;
	}

	public void setEnvironment(Cache<K, V> cache, Set<K> inputKeys) {

		this.cache =  cache;
		this.keys = inputKeys;
		collector.initialize_cache(cache.getCacheManager());
	}
	
	public List<K> call() throws Exception {
		
		if (mapper == null) {
			System.out.println(" Mapper not initialized ");
		} else {
         mapper.setCacheManager(cache.getCacheManager());
			List<K> result = new ArrayList<K>();
			final ClusteringDependentLogic cdl = cache.getAdvancedCache().getComponentRegistry().getComponent(ClusteringDependentLogic.class);
			for(Object key : cache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).keySet()){
				if(!cdl.localNodeIsPrimaryOwner(key))
					continue;
				V value = cache.get(key);
				if (value != null) {
					mapper.map((K)key, value, collector);
				}
			}
			
			return result;
		}
		return null;
	}

}
