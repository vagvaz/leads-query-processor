package eu.leads.processor.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.distexec.mapreduce.Collector;
import org.infinispan.distexec.mapreduce.Reducer;


public  class LeadsReduceCallable<kOut, vOut> implements
 DistributedCallable<kOut, vOut, List<vOut>>, Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 3724554288677416505L;

	public LeadsReduceCallable(Cache<kOut, vOut> cache,
			LeadsCollector<kOut, vOut> collector,LeadsReducer<kOut, vOut> reducer ) {
		super( );
		this.OutCache = cache;
		this.collector = collector;
		this.reducer = reducer;
	}
	private LeadsCollector<kOut, vOut> collector = null;
	
	private Cache<kOut, vOut> OutCache;
	private Set<kOut> keys;
	private LeadsReducer<kOut, vOut> reducer = null;

	//private Cache<kOut, vOut> cache;

	public List<vOut> call() throws Exception {
		// TODO Auto-generated method stub
		if (reducer == null) {
			System.out.println(" Reducer not initialized ");
		} else {
			//System.out.println(" Run Reduce ");
			List<vOut> result = new ArrayList<vOut>();
			//System.out.println("Collector Cache Size:" + this.collector.getCache().size());
			for (Entry<kOut, List<vOut>> entry : this.collector.getCache().entrySet()) {
			    kOut key = entry.getKey();
				 
				vOut res = reducer.reduce(key  , entry.getValue().iterator());
				result.add(res);
				OutCache.put(key, res);
			}
			//System.out.println(" OutCache size : " + OutCache.size() +  " result size: " + result.size());
			return result;
		}
		return null;
	}

	public void setEnvironment(Cache<kOut, vOut > cache, Set<kOut> inputKeys) {
		//this.OutCache = cache; // fix it
		this.keys = inputKeys;
	}
	 

}
