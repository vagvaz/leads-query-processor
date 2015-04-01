package eu.leads.processor.infinispan;

import eu.leads.processor.common.infinispan.InfinispanManager;
import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
  private static Logger log = null;

  public LeadsIntermediateIterator(String key, String prefix, InfinispanManager imanager){
    log = LoggerFactory.getLogger(LeadsIntermediateIterator.class);
    this.imanager = imanager;
    intermediateDataCache = (Cache) imanager.getPersisentCache(prefix + ".data");
    //createIndexCache for getting all the nodes that contain values with the same key! in a mc
    indexSiteCache = (Cache) imanager.getIndexedPersistentCache(prefix + ".indexed");
    baseIntermKey = new ComplexIntermediateKey();
    baseIntermKey.setCounter(currentCounter);
    baseIntermKey.setKey(key);
    log.error("INDEXED SITE = " + indexSiteCache.size());
    for(Object keys : indexSiteCache.keySet()){
      log.error("key: " + keys.toString() + indexSiteCache.get(keys).toString());
    }
    // create query
//    SearchManager sm = org.infinispan.query.Search.getSearchManager((Cache<?, ?>) indexSiteCache);
    QueryFactory qf = org.infinispan.query.Search.getQueryFactory((Cache<?, ?>) indexSiteCache); //Search.getQueryFactory((RemoteCache) indexSiteCache);
    org.infinispan.query.dsl.Query lucenequery = qf.from(IndexedComplexIntermediateKey.class)
                                                   .having("key").eq(key)
                                                   .toBuilder().build();
    ListIterator<Object> anIterator = lucenequery.list().listIterator();

    this.list = new ArrayList<>();
    while(anIterator.hasNext()){
      Object o = anIterator.next();
      log.error("Adding to list " + o.toString());
      if(o instanceof IndexedComplexIntermediateKey) {
        IndexedComplexIntermediateKey ikey = (IndexedComplexIntermediateKey) o;
        list.add(ikey);
      }
      else{
        log.error("Error in indexSiteCache found class of type " + o.getClass().toString());
      }

    }
    chunkIterator = list.iterator();

    if(chunkIterator.hasNext()) {
      currentChunk = chunkIterator.next();
      baseIntermKey = new ComplexIntermediateKey(currentChunk);
    }

  }

  @Override public boolean hasNext() {
    boolean result = false;
    if(currentChunk == null || baseIntermKey == null)
      result = false;

//    Object o = null;
//    if(baseIntermKey != null)
//     o = intermediateDataCache.get(new ComplexIntermediateKey(baseIntermKey));
//    if(o != null){

    if (baseIntermKey != null && intermediateDataCache.containsKey(new ComplexIntermediateKey(baseIntermKey))) {
//      System.err.println("baseIntermKey " + baseIntermKey);
//      System.err.println("with object " + o.toString());
        result = true;
      }

    if(chunkIterator != null && chunkIterator.hasNext()) {
//      System.err.println("chunk has next " + chunkIterator.toString());
//      PrintUtilities.printList(list);
      result = true;
    }

//    System.err.println("leadsIntermediateIterator hasNext returns " + result);
    return result;
  }

  @Override public V next() {
    if(!hasNext()){
      throw new NoSuchElementException("LeadsIntermediateIterator the iterator does not have next");
    }
//    System.err.println("in next ");
    log.error(baseIntermKey.toString());
    V returnValue = (V) intermediateDataCache.get(new ComplexIntermediateKey(baseIntermKey));
    if(returnValue == null){
      System.err.println("\n\n\nERROR NULL GET FROM intermediate data cache " + intermediateDataCache.size());
      throw new NoSuchElementException("LeadsIntermediateIterator read from cache returned NULL");
    }
    baseIntermKey.next();
//    baseIntermKey = baseIntermKey.next();
//    Object o = intermediateDataCache.get(baseIntermKey);
//    if(o == null){
//
    if(!intermediateDataCache.containsKey(new ComplexIntermediateKey(baseIntermKey))){
      if(chunkIterator.hasNext()) {
        currentChunk = chunkIterator.next();
        baseIntermKey = new ComplexIntermediateKey(currentChunk);
      }
      else{
        baseIntermKey = null;
        currentChunk = null;
      }


    }
    if(returnValue != null) {
//      System.err.println("out next ");
      return returnValue;
    }
    else {
        if(chunkIterator.hasNext()) {
          System.err.println("TbaseInterm Key is not contained and chunkIterator has NeXt");
          System.err.println(this.toString());
        }
        currentChunk = null;
      throw new NoSuchElementException("LeadsIntermediateIterator return Value NULL at the end");
    }
  }

  @Override public void remove() {

  }

  @Override public String toString() {
    String result =  "LeadsIntermediateIterator{" +
             "intermediateDataCache=" + intermediateDataCache.getName() +
             ", indexSiteCache=" + indexSiteCache.getName() +
             ", imanager=" + imanager.getCacheManager().getAddress().toString();
//    System.err.println(result);
    String resultb =
             ", baseIntermKey=" + baseIntermKey.toString();
    result += resultb;
//    System.err.println(resultb);

    String resultc =
             ", list=" + list.size() +
             ", currentCounter=" + currentCounter.toString() +

             '}';
//    PrintUtilities.printList(list);
    result += resultc;
//    System.err.println(resultc);
    result += ", currentChunk=" + currentChunk;
//    System.err.println(result);
    return result;
  }
}
