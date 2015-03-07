package eu.leads.processor.infinispan;

import org.infinispan.Cache;

import java.io.Serializable;
import java.util.List;

public class LeadsReducerCallable<kOut, vOut> extends LeadsBaseCallable<kOut,Object> implements
                                                                              Serializable {

    /**
     * tr
     */
    private static final long serialVersionUID = 3724554288677416505L;
    private LeadsReducer<kOut, vOut> reducer = null;
    private LeadsCollector collector;


    public LeadsReducerCallable(Cache<kOut, vOut> outCache,
                                 LeadsReducer<kOut, vOut> reducer) {
        super("{}",outCache.getName());
        this.reducer = reducer;
        collector = new LeadsCollector(1000,outCache);
    }

  @Override public void executeOn(kOut key, Object value) {
    List<vOut> values = (List<vOut>) value;
    reducer.reduce(key,values.iterator(),collector);
  }

  @Override public void initialize() {
    super.initialize();
    collector.initializeCache(imanager);
    collector.setOnMap(false);
  }
  //    public vOut call() throws Exception {
//        if (reducer == null) {
//            System.out.println(" Reducer not initialized ");
//        } else {
//           reducer.setCacheManager(inputCache.getCacheManager());
//            // System.out.println(" Run Reduce ");
//            vOut result = null;
////            System.out.println("inputCache Cache Size:"
////                    + this.inputCache.size());
////            for (Entry<kOut, List<vOut>> entry : inputCache.entrySet()) {
//          final ClusteringDependentLogic cdl = inputCache.getAdvancedCache().getComponentRegistry().getComponent(ClusteringDependentLogic.class);
//          for(Object ikey : inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).keySet()){
//            if(!cdl.localNodeIsPrimaryOwner(ikey))
//              continue;
//                kOut key = (kOut)ikey;
//                List<vOut> list = inputCache.get(key);
//                if(list == null || list.size() == 0){
//                  continue;
//                }
//
//                vOut res = reducer.reduce(key, list.iterator());
//                if(res == null || res.toString().equals("")){
//                  ;
//                }
//                else
//                {
////                  outCache.put(key, res);
//                }
//            }
//
//            return result;
//        }
//        return null;
//    }

  @Override public void finalize() {
    super.finalize();
    reducer.finalize();
  }
}
