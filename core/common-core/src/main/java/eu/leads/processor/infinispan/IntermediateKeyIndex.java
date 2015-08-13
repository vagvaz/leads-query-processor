package eu.leads.processor.infinispan;

import org.infinispan.Cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by vagvaz on 16/07/15.
 */
public class IntermediateKeyIndex {
    private Map<String,Integer> keysCache;
    private Map<String,Object> dataCache;
    private int putCounter;
    public IntermediateKeyIndex(Map keysCache, Map dataCache) {
        this.keysCache = keysCache;
        this.dataCache = dataCache;
    }

    public void put(String key, Object value){
        synchronized (this) {
            Integer count = keysCache.get(key);
            if (count == null) {
                count = 0;
            } else {
                count++;
            }
            dataCache.put(key + count.toString(), value);
            keysCache.put(key, count);
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
        this.keysCache = keysCache;
    }

    public Map<String, Object> getDataCache() {
        return dataCache;
    }

    public void setDataCache(Map<String, Object> dataCache) {
        this.dataCache = dataCache;
    }
}
