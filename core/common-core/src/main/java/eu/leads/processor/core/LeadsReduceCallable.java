package eu.leads.processor.core;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.interceptors.locking.ClusteringDependentLogic;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class LeadsReduceCallable<kOut, vOut> implements
        DistributedCallable<kOut, List<vOut>, vOut>, Serializable {

    /**
     * tr
     */
    private static final long serialVersionUID = 3724554288677416505L;
    transient private Cache<kOut, vOut> outCache;
    transient private Cache<kOut, List<vOut>> inputCache;

    private Set<kOut> keys;
    private LeadsReducer<kOut, vOut> reducer = null;

    private String outputCacheName;

    public LeadsReduceCallable(Cache<kOut, vOut> outCache,
                               LeadsReducer<kOut, vOut> reducer) {
        super();
        this.outCache = outCache;
        this.outputCacheName = this.outCache.getName();
        this.reducer = reducer;
    }

    public vOut call() throws Exception {
        if (reducer == null) {
            System.out.println(" Reducer not initialized ");
        } else {
           reducer.setCacheManager(inputCache.getCacheManager());
            // System.out.println(" Run Reduce ");
            vOut result = null;
//            System.out.println("inputCache Cache Size:"
//                    + this.inputCache.size());
//            for (Entry<kOut, List<vOut>> entry : inputCache.entrySet()) {
          final ClusteringDependentLogic cdl = inputCache.getAdvancedCache().getComponentRegistry().getComponent(ClusteringDependentLogic.class);
          for(Object ikey : inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).keySet()){
            if(!cdl.localNodeIsPrimaryOwner(ikey))
              continue;
                kOut key = (kOut)ikey;
                List<vOut> list = inputCache.get(key);
                if(list == null || list.size() == 0){
                  continue;
                }

                vOut res = reducer.reduce(key, list.iterator());
                if(res == null || res.toString().equals("")){
                  ;
                }
                else
                {
//                  outCache.put(key, res);
                }
            }

            return result;
        }
        return null;
    }

    public void setEnvironment(Cache<kOut, List<vOut>> inputCache,
                               Set<kOut> inputKeys) {
        this.keys = inputKeys;
        this.inputCache = inputCache;
        outCache = inputCache.getCacheManager().getCache(outputCacheName);
    }
}
