package eu.leads.processor.infinispan;

import eu.leads.processor.common.infinispan.InfinispanManager;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.QueryFactory;

import java.util.Iterator;
import java.util.List;

/**
 * Created by vagvaz on 3/7/15.
 */
public class LeadsIntermediateIterator<V> implements Iterator<V> {
  protected transient BasicCache intermediateDataCache;
  protected transient BasicCache indexSiteCache;
  private transient InfinispanManager imanager;
  private String key;
  private ComplexIntermediateKey baseIntermKey;
  private IndexedComplexIntermediateKey currentChunk;
  private List<IndexedComplexIntermediateKey> list;
  private Integer currentCounter = 0;
  private Iterator<IndexedComplexIntermediateKey> chunkIterator;


  public LeadsIntermediateIterator(String key, String prefix, InfinispanManager imanager){
    intermediateDataCache = (Cache) imanager.getPersisentCache(prefix + ".data");
    //createIndexCache for getting all the nodes that contain values with the same key! in a mc
    indexSiteCache = (Cache) imanager.getIndexedPersistentCache(prefix + ".indexed");
    baseIntermKey = new ComplexIntermediateKey();
    baseIntermKey.setCounter(currentCounter);
    baseIntermKey.setKey(key);
    // create query
    SearchManager sm = org.infinispan.query.Search.getSearchManager((Cache<?, ?>) indexSiteCache);
    QueryFactory qf = sm.getQueryFactory();
    org.infinispan.query.dsl.Query lucenequery = qf.from(IndexedComplexIntermediateKey.class)
                                                   .having("key").eq(key)
                                                   .toBuilder().build();
    List<IndexedComplexIntermediateKey> list = lucenequery.list();
    chunkIterator = list.iterator();
    if(chunkIterator.hasNext()) {
      currentChunk = chunkIterator.next();
      baseIntermKey = new ComplexIntermediateKey(currentChunk);
    }
    currentChunk = null;
  }

  @Override public boolean hasNext() {
    if(currentChunk == null)
      return false;
    if(chunkIterator.hasNext()) {
      return true;
    }
    if(intermediateDataCache.containsKey(baseIntermKey)){
      return true;
    }
    return false;
  }

  @Override public V next() {
    V returnValue = (V) intermediateDataCache.get(baseIntermKey);
    baseIntermKey.next();
    if(!intermediateDataCache.containsKey(baseIntermKey)) {
      if(chunkIterator.hasNext()){
        currentChunk = chunkIterator.next();
        baseIntermKey = new ComplexIntermediateKey(currentChunk);
      }
      else {
        currentChunk = null;
      }
    }
    return returnValue;
  }

  @Override public void remove() {

  }
}
