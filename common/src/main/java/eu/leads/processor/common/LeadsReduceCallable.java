package eu.leads.processor.common;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;

public class LeadsReduceCallable<kOut, vOut> implements
        DistributedCallable<kOut, List<vOut>, vOut>, Serializable {

    /**
     * tr
     */
    private static final long serialVersionUID = 3724554288677416505L;
    transient private Cache<kOut, vOut> OutCache;
    transient private Cache<kOut, List<vOut>> inputCache;

    private Set<kOut> keys;
    private LeadsReducer<kOut, vOut> reducer = null;

    private String outputCacheName;

    public LeadsReduceCallable(Cache<kOut, vOut> OutCache,
                               LeadsReducer<kOut, vOut> reducer) {
        super();
        this.OutCache = OutCache;
        this.outputCacheName = this.OutCache.getName();
        this.reducer = reducer;
    }

    public vOut call() throws Exception {
        if (reducer == null) {
            System.out.println(" Reducer not initialized ");
        } else {
            // System.out.println(" Run Reduce ");
            vOut result = null;
//            System.out.println("inputCache Cache Size:"
//                    + this.inputCache.size());
            for (Entry<kOut, List<vOut>> entry : inputCache.entrySet()) {
                kOut key = entry.getKey();
                vOut res = reducer.reduce(key, entry.getValue().iterator());
                OutCache.put(key, res);
            }
            return result;
        }
        return null;
    }

    public void setEnvironment(Cache<kOut, List<vOut>> inputCache,
                               Set<kOut> inputKeys) {
        this.keys = inputKeys;
        this.inputCache = inputCache;
        OutCache = inputCache.getCacheManager().getCache(outputCacheName);
    }

}
