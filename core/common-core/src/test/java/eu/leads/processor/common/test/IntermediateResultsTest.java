package eu.leads.processor.common.test;

import eu.leads.processor.common.infinispan.CacheManagerFactory;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.infinispan.ComplexIntermediateKey;
import eu.leads.processor.infinispan.IndexedComplexIntermediateKey;
import eu.leads.processor.infinispan.LeadsIntermediateIterator;
import org.infinispan.commons.api.BasicCache;

/**
 * Created by vagvaz on 3/27/15.
 */
public class IntermediateResultsTest {

  static BasicCache indexedCache;
  static BasicCache dataCache;
  static BasicCache keysCache;
  static String[] nodes= {"node0","node1","node2","node3","node00","node11","node22","node33"};
  static String[] microClouds = {"mc0","mc1"};//,"mc2","mc01","mc11","mc21"};
  static String[] keys;
  static int numOfkeys = 2;
  static int valuesPerKey = 380;
  public static void main(String[] args) {
    LQPConfiguration.initialize();
    InfinispanManager manager = InfinispanClusterSingleton.getInstance().getManager();
    InfinispanManager manager11 = CacheManagerFactory.createCacheManager();
    InfinispanManager manager12 = CacheManagerFactory.createCacheManager();
    InfinispanManager manager13 = CacheManagerFactory.createCacheManager();
    InfinispanManager manager14 = CacheManagerFactory.createCacheManager();
     indexedCache = (BasicCache) manager.getIndexedPersistentCache("prefix.indexed");
     dataCache = (BasicCache)manager.getPersisentCache("prefix.data");
     keysCache = (BasicCache)manager.getPersisentCache("keysCache");


     keys = new String[numOfkeys];
    for (int index = 0; index < numOfkeys; index++) {
      keys[index] = "key"+index;
      keysCache.put(keys[index],keys[index]);
    }
    //generate intermediate keyValuePairs
    generateIntermKeyValue(keysCache,dataCache,indexedCache,valuesPerKey,keys,nodes, microClouds);

    int counter = 0;

    for(String k : keys){
      int keyCounter = 0;
      LeadsIntermediateIterator iterator = new LeadsIntermediateIterator(k,"prefix",InfinispanClusterSingleton.getInstance().getManager());
      while(iterator.hasNext()){
        System.out.println(keyCounter + ": "+ iterator.next().toString());
        keyCounter++;
        counter++;
      }
      System.err.println("key " + k + " " + keyCounter);
    }
    System.err.println("Total counted " + counter + " total " + keys.length* microClouds.length*nodes.length*valuesPerKey);
    System.exit(0);
  }

  private static void generateIntermKeyValue(BasicCache keysCache, BasicCache dataCache,
                                              BasicCache indexedCache, int valuesPerKey, String[] keys,
                                              String[] nodes, String[] microClouds) {
//    IndexedComplexIntermediateKey indexedKey = new IndexedComplexIntermediateKey();
//    ComplexIntermediateKey ikey = new ComplexIntermediateKey();
    for(String node : nodes){
      for(String site : microClouds) {
        for (String key : keys) {
          for (int counter = 0; counter < valuesPerKey; counter++) {
            IndexedComplexIntermediateKey indexedKey = new IndexedComplexIntermediateKey();
            ComplexIntermediateKey ikey = new ComplexIntermediateKey();
            indexedKey.setKey(key);
            indexedKey.setNode(node);
            indexedKey.setSite(site);
            ikey.setCounter(counter);
            ikey.setKey(key);
            ikey.setSite(site);
            ikey.setNode(node);
            indexedCache.put(indexedKey.getUniqueKey(), new IndexedComplexIntermediateKey(indexedKey));
            dataCache.put(new ComplexIntermediateKey(ikey), new ComplexIntermediateKey(ikey));
            keysCache.put(key, key);
          }
          System.out.println("Generated " + node + " " + site + " " + key);
        }
      }
    }

  }
}
