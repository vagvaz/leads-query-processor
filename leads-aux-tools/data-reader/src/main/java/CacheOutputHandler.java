import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.ensemble.EnsembleCacheManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * Created by vagvaz on 01/08/15.
 */
public class CacheOutputHandler<K, V> implements OutputHandler<K, V> {

    BasicCache cache;
    boolean storeNutchData;
    EnsembleCacheManager manager;
    DummyOutputHandler dummyOutputHandler = null;
    long valueSize = 0;

    @Override public void initialize(Properties conf) {
        if (conf.containsKey("nutchData")) {
            storeNutchData = true;
        } else {
            storeNutchData = false;
        }
        String cacheName = conf.getProperty("cacheName");
        if (cacheName == null) {
            System.err.println("Initialization failed using dummy ouputhandler");
            dummyOutputHandler = new DummyOutputHandler();
            return;
        }
        if (conf.containsKey("remote")) {
            String remoteString = conf.getProperty("remote");
            manager = new EnsembleCacheManager(remoteString);
            cache =
                manager.getCache(cacheName, new ArrayList<>(manager.sites()), EnsembleCacheManager.Consistency.DIST);
        } else {
            cache = (BasicCache) conf.get(cacheName);
        }
        EnsembleCacheUtils.initialize();
    }

    @Override public long putAll(Map<K, V> data) {
        for (Map.Entry<K, V> entry : data.entrySet()) {
            append(entry.getKey(), entry.getValue());
        }
        valueSize += data.size();
        return valueSize;
    }

    @Override public long append(Object key, Object value) {

        if (dummyOutputHandler == null) {
            EnsembleCacheUtils.putToCacheDirect(cache, key, value);
        } else {
            dummyOutputHandler.append(key, value);
        }
        return valueSize;
    }

    @Override public void close() {
        if (dummyOutputHandler != null) {
            EnsembleCacheUtils.waitForAllPuts();
        }
    }
}
