package eu.leads.processor.infinispan;

import eu.leads.processor.common.LeadsListener;
import eu.leads.processor.common.infinispan.AcceptAllFilter;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.core.LevelDBIndex;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
import org.infinispan.context.Flag;
import org.infinispan.interceptors.locking.ClusteringDependentLogic;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Apostolos Nydriotis on 2015/06/24.
 */
public class LeadsLocalReducerCallable<kOut, vOut> extends LeadsBaseCallable<kOut, Object>
    implements
    Serializable {

    private static final long serialVersionUID = 8028728191155715526L;
    private LeadsReducer<kOut, vOut> reducer = null;
    private LeadsCollector collector;
    private String prefix;
    private transient LevelDBIndex index;
    private transient LeadsListener leadsListener;

    public LeadsLocalReducerCallable(String cacheName, LeadsReducer<kOut, vOut> reducer,
        String prefix, String site) {
        super("{}", cacheName);
        this.reducer = reducer;
        collector = new LeadsCollector(1000, cacheName);
        collector.setOnMap(true);
        this.prefix = prefix;
    }

//    public void setLocalSite(String localSite){
//        collector.setLocalSite(localSite);
//    }

    @Override
    public void executeOn(kOut key, Object value) {
        //    LeadsIntermediateIterator<vOut> values = new LeadsIntermediateIterator<>((String) key, prefix,
        //                                                                             imanager);
        //    Iterator<vOut> values = ((List)value).iterator();
        Iterator<vOut> values = (Iterator<vOut>) value;
        reducer.reduce(key, values, collector);
    }


    @Override public String call() throws Exception {
        profCallable.end("call");
        if(!isInitialized){
            initialize();
        }
        Cache dataCache = inputCache.getCacheManager().getCache(prefix+".data");

        index = null;
        //        EnsembleCacheUtils.waitForAllPuts();
        for(Object listener : dataCache.getListeners()){
            if(listener instanceof LocalIndexListener){
                System.err.println("listener class is " + listener.getClass().toString());
                LocalIndexListener localIndexListener = (LocalIndexListener) listener;
                leadsListener = localIndexListener;
                System.err.println("WaitForAllData");
                localIndexListener.waitForAllData();

                System.err.println("getIndex");
                index = localIndexListener.getIndex();
                //                index.flush();
                break;
            }
        }
        if(index == null){
            System.err.println("\n\n\n\n\n\nIndex was not installed serious...\n\n\n\n\n\n");
            profilerLog.error("\n\n\n\n\n\nIndex was not installed serious...\n\n\n\n\n\n");
            return embeddedCacheManager.getAddress().toString();
        }
        //        System.err.println(
        //            "LeadsIndex size " + index.getKeysCache().size() + " data " + index.getDataCache()
        //                .size() + " input: " + inputCache.getAdvancedCache()
        //                .withFlags(Flag.CACHE_MODE_LOCAL).size());
        //        profilerLog.error("MRLOG: LeadsIndex size " + index.getKeysCache().size() + " data " + index.getDataCache()
        //                .size() );
        //        if(index.getKeysCache().size() != index.getDataCache().size()/2) {
        //            for (Map.Entry<String, Integer> entry : index.getKeysIterator()) {
        //                if (entry.getValue() != 1) {
        //                    System.err.println("THE KEY IS " + entry.getKey() + " " + entry.getValue());
        //                    profilerLog
        //                        .error("MRLOG: " + "THE KEY IS " + entry.getKey() + " " + entry.getValue());
        //
        //                }
        //            }
        //        }
        System.err.println("Start processing");
        profCallable.end();
        for(Map.Entry<String,Integer> entry : index.getKeysIterator()){
            Iterator iterator =
                index.getKeyIterator(entry.getKey(),entry.getValue());
            executeOn((kOut)entry.getKey(),iterator);
        }
        //            CloseableIterable iterable = inputCache.getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL).filterEntries(new LocalDataFilter<K,V>(cdl));
        //            profExecute.end();
        //            try {
        //                for (Object object : iterable) {
        //                    Map.Entry<K, V> entry = (Map.Entry<K, V>) object;
        //
        //                    //      V value = inputCache.get(key);
        //                    K key = (K) entry.getKey();
        //                    V value = (V) entry.getValue();
        //
        //                    if (value != null) {
        //                        profExecute.start("ExOn" + (++count));
        //                        executeOn((K) key, value);
        //                        profExecute.end();
        //                    }
        //                }
        //            }
        //            catch(Exception e){
        //                iterable.close();
        //                profilerLog.error("Exception in LEADSBASEBACALLABE " + e.getClass().toString());
        //                PrintUtilities.logStackTrace(profilerLog, e.getStackTrace());
        //            }
        profCallable.end();
        finalizeCallable();
        return embeddedCacheManager.getAddress().toString();
    }


    @Override
    public void initialize() {
        super.initialize();
        collector.setOnMap(true);
        collector.setEmanager(emanager);
        collector.setManager(embeddedCacheManager);
        collector.initializeCache(inputCache.getName(), imanager);
        collector.setIsReduceLocal(true);
        this.reducer.initialize();
    }

    @Override public void finalizeCallable() {
        System.err.println("finalize collector in reduce callable");
        collector.finalizeCollector();
        System.err.println("finalize reducelocalabe task");
        reducer.finalizeTask();
        System.err.println("finalize base");
        super.finalizeCallable();
    }
}
