package eu.leads.processor.common.test;

import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.infinispan.IndexedComplexIntermediateKey;
import org.infinispan.commons.api.BasicCache;

/**
 * Created by vagvaz on 3/27/15.
 */
public class IntermediateResultsTest {

  static BasicCache indexedCache;
  static BasicCache dataCache;
  static BasicCache keysCache;
  static String[] nodes= {"node0","node1","node2","node3"};
  static String[] microClouds = {"mc0","mc1","mc2"};
  static String[] keys;
  static int numOfkeys = 100;
  static int valuesPerKey = 10;
  public static void main(String[] args) {
    InfinispanManager manager = InfinispanClusterSingleton.getInstance().getManager();
     indexedCache = (BasicCache) manager.getIndexedPersistentCache("indexedCache");
     dataCache = (BasicCache)manager.getPersisentCache("dataCache");
     keysCache = (BasicCache)manager.getPersisentCache("keysCache");


     keys = new String[numOfkeys];
    for (int index = 0; index < numOfkeys; index++) {
      keys[index] = "key"+index;
      keysCache.put(keys[index],keys[index]);
    }
    //generate intermediate keyValuePairs
    generateIntermKeyValue(keysCache,dataCache,indexedCache,valuesPerKey,keys,nodes, microClouds);

  }

  private static void generateIntermKeyValue(BasicCache keysCache, BasicCache dataCache,
                                              BasicCache indexedCache, int valuesPerKey, String[] keys,
                                              String[] nodes, String[] microClouds) {
    IndexedComplexIntermediateKey indexedKey;
    
    for(String node : nodes){
      for(String mc : microClouds){
        for(String key : keys)
          for (int counter = 0; counter < valuesPerKey; counter++) {

          }
      }
    }
  }
}
