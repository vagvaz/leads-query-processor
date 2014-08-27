package eu.leads.processor.common;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.infinispan.Cache;
import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.manager.EmbeddedCacheManager;

public class LeadsCollector<KOut, VOut> implements Collector<KOut, VOut>,
		Serializable {

	public LeadsCollector(int maxCollectorSize,
			Cache<KOut, List<VOut>> collectorCache) {
		super();

		// store = new HashMap<KOut, List<VOut>>(1024, 0.75f);
		emitCount = new AtomicInteger();
		this.maxCollectorSize = maxCollectorSize;
		store_cache = collectorCache;
		cache_name=collectorCache.getName();
	}

	// private Map<KOut, List<VOut>> store;
	private transient Cache<KOut, List<VOut>> store_cache;
	private final AtomicInteger emitCount;
	private final int maxCollectorSize;
	private String cache_name;
	
	
	
	
	public Cache<KOut, List<VOut>> getCache() {
		return store_cache;
	}

	public void emit(KOut key, VOut value) {

		// List<VOut> list = store.get(key);
		List<VOut> list = store_cache.get(key);
		if (list == null) {
			list = new ArrayList<VOut>(128);
			// store.put(key, list);
			store_cache.put(key, list);
		}
		list.add(value);
		emitCount.incrementAndGet();
		// if (isOverflown() && mcc.hasCombiner()) {
		// combine(mcc, this);
		// }
	}

	// public Map<KOut, List<VOut>> collectedValues() {
	// return store;
	// }
	
	public void initialize_cache(EmbeddedCacheManager manager){
		store_cache=manager.getCache(cache_name);
	}

	public void reset() {
		// store.clear();
		store_cache.clear();
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
	private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
//        stream.writeObject(name);
//        stream.writeInt(id);
//        stream.writeObject(DOB);
    }
	private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
		
//        name = (String) stream.readObject();
//        id = stream.readInt();
//        DOB = (String) stream.readObject();
    }
}
