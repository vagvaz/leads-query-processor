package eu.leads.processor.infinispan;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.utils.PrintUtilities;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.QueryFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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
    this.imanager = imanager;
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
    List<IndexedComplexIntermediateKey> alist = lucenequery.list();
    this.list = new ArrayList<>();
    for(IndexedComplexIntermediateKey ikey : alist){
      list.add(ikey);
    }
    chunkIterator = list.iterator();

    if(chunkIterator.hasNext()) {
      currentChunk = chunkIterator.next();
      baseIntermKey = new ComplexIntermediateKey(currentChunk);
    }

  }

  @Override public boolean hasNext() {
    boolean result = false;
    if(currentChunk == null)
      result = false;
    Object o = intermediateDataCache.get(baseIntermKey);
    if(o != null){
      System.err.println("baseIntermKey " + baseIntermKey);
      System.err.println("with object " + o.toString());
      result = true;
    }
    if(chunkIterator.hasNext()) {
      System.err.println("chunk has next " + chunkIterator.toString());
      PrintUtilities.printList(list);
      result = true;
    }

    System.err.println("leadsIntermediateIterator hasNext returns " + result);
    return result;
  }

  @Override public V next() {
    if(!hasNext()){
      throw new NoSuchElementException("LeadsIntermediateIterator the iterator does not have next");
    }
    System.err.println("in next ");
    V returnValue = (V) intermediateDataCache.get(baseIntermKey);
    if(returnValue == null){
      System.err.println("\n\n\nERROR NULL GET FROM intermediate data cache " + intermediateDataCache.size());
      throw new NoSuchElementException("LeadsIntermediateIterator read from cache returned NULL");
    }
    baseIntermKey.next();
    Object o = intermediateDataCache.get(baseIntermKey);
    if(o == null){
//
      if(chunkIterator.hasNext()) {
        currentChunk = chunkIterator.next();
        baseIntermKey = new ComplexIntermediateKey(currentChunk);
      }
      else{
        baseIntermKey = null;
        currentChunk = null;
      }
//      else{
//        break;
//      }
      System.err.println("next base key = " + baseIntermKey);
      if(baseIntermKey!= null && intermediateDataCache.containsKey(baseIntermKey)) {
        System.err.println("base key exists" );
      }
      else {
        System.err.println("NOT base key exists or is NULL" );
      }

    }
    if(returnValue != null) {
      System.err.println("out next ");
      return returnValue;
    }
    else {
        if(chunkIterator.hasNext()) {
          System.err.println("TbaseInterm Key is not contained and chunkIterator has NeXt");
          System.err.println(this.toString());
        }
        currentChunk = null;

    }
    if(returnValue != null) {
      System.err.println("out of next !null next ");
      return returnValue;
    }
    else{
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
    System.err.println(result);
    String resultb =
             ", baseIntermKey=" + baseIntermKey.toString();
    result += resultb;
    System.err.println(resultb);

    String resultc =
             ", list=" + list.size() +
             ", currentCounter=" + currentCounter.toString() +

             '}';
    PrintUtilities.printList(list);
    result += resultc;
    System.err.println(resultc);
    result += ", currentChunk=" + currentChunk;
    System.err.println(result);
    return result;
  }
}
