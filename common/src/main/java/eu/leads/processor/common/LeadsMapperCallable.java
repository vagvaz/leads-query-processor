package eu.leads.processor.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.distexec.mapreduce.Collector;

public class LeadsMapperCallable<K, V, kOut, vOut> implements
		DistributedCallable<K, V, List<K>>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1242145345234214L;
	 
	LeadsCollector<kOut, vOut> collector = null;
	Cache<K, V> cache;
	Set<K> keys;
	LeadsMapper<K, V, kOut, vOut> mapper = null;

	public void setEnvironment(Cache<K, V> cache, Set<K> inputKeys) {
		// TODO Auto-generated method stub
		this.cache = cache;
		this.keys = inputKeys;
	}

	public LeadsMapperCallable(Cache<K, V> cache,
			LeadsCollector<kOut, vOut> collector,
			LeadsMapper<K, V, kOut, vOut> mapper) {
		super();
		this.cache = cache;
		this.collector = collector;
		this.mapper = mapper;
	}
	
	public List<K> call() throws Exception {
		
		if (mapper == null) {
			System.out.println(" Mapper not initialized ");
		} else {
			List<K> result = new ArrayList<K>();
			//System.out.println(" Run Mapper Callable ");
			//Use Cache Keys only
			for (Entry< K, V  > entry : cache.entrySet()){//K key : keys) {
				V value = entry.getValue();//cache.get(key);
				if (value != null) {
					mapper.map(entry.getKey(), value, collector);
				}
			}
			//System.out.println(" Run end ");
			return result;
		}
		return null;
	}
}
