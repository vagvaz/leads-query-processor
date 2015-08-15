package eu.leads.processor.infinispan;

import org.infinispan.Cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by vagvaz on 16/07/15.
 */
public class IntermediateKeyIndex {
    private Cache<String,Integer> keysCache;
    private Cache<String,Object> dataCache;
    private int putCounter;
    public IntermediateKeyIndex(Map keysCache, Map dataCache) {
        this.keysCache = (Cache<String, Integer>) keysCache;
        this.dataCache = (Cache<String, Object>) dataCache;
    }

    public void put(String key, Object value){
        synchronized (this) {
            Integer count = keysCache.get(key);
            if (count == null) {
                count = 0;
            } else {
                count++;
            }
            dataCache.putAsync(key + count.toString(), value);
            keysCache.putAsync(key, count);
//            dataCache.get(key+count.toString());
        }
    }

    public Set<Map.Entry<String,Integer>> getKeysIterator(){
        return keysCache.entrySet();
    }

    public Iterator<Object> getKeyIterator(String key, Integer counter){
        return new LocalIndexKeyIterator(key,counter,dataCache);
    }

    public Map<String, Integer> getKeysCache() {
        return keysCache;
    }

    public void setKeysCache(Map<String, Integer> keysCache) {
        this.keysCache = (Cache<String, Integer>) keysCache;
    }

    public Map<String, Object> getDataCache() {
        return dataCache;
    }

    public void setDataCache(Map<String, Object> dataCache) {
        this.dataCache = (Cache<String, Object>) dataCache;
    }
}
