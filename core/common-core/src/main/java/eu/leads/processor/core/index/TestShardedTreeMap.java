package eu.leads.processor.core.index;

import eu.leads.processor.common.infinispan.CacheManagerFactory;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import org.apache.commons.lang.RandomStringUtils;
import org.infinispan.Cache;
import org.infinispan.versioning.impl.VersionedCacheShardedTreeMapImpl;
import org.infinispan.versioning.utils.version.Version;
import org.infinispan.versioning.utils.version.VersionScalar;
import org.infinispan.versioning.utils.version.VersionScalarGenerator;

import java.util.*;

/**
 * Created by angelos on 26/02/15.
 */
public class TestShardedTreeMap {
  //  private static Queue<NotifyingFuture> buffer = new LinkedList<>();

  public static void main(String[] args) {
    LQPConfiguration.initialize();
    InfinispanManager man = InfinispanClusterSingleton.getInstance().getManager();
    InfinispanManager man2 = CacheManagerFactory.createCacheManager();
    InfinispanManager man3 = CacheManagerFactory.createCacheManager();
    InfinispanManager man4 = CacheManagerFactory.createCacheManager();
    InfinispanManager man5 = InfinispanClusterSingleton.getInstance().getManager();
    InfinispanManager man6 = CacheManagerFactory.createCacheManager();
    InfinispanManager man7 = CacheManagerFactory.createCacheManager();
    InfinispanManager man8 = CacheManagerFactory.createCacheManager();
    Cache cache = (Cache) man.getPersisentCache("indexedCache");
    Cache tuplesCacheString = (Cache) man.getPersisentCache("tuplesString");
    Cache resultIndexCache = (Cache) man.getPersisentCache("resultIndex");

    int numStrings = 100;// 10000
    int numTuples = 500;// 150000
    int numAttributes = 100;// 100
    long startTime, stopTime;
    Map<Integer, String> mapStr = new HashMap<>();
    Random rand = new Random();
    String attr = "attributeName";//"attributeName"+rand.nextInt(numAttributes);

    VersionScalarGenerator vsg = new VersionScalarGenerator();
    VersionedCacheShardedTreeMapImpl vcstmap = new VersionedCacheShardedTreeMapImpl(cache, vsg, "indexedCache");


    for (int i = 0; i < numStrings; i++) {
      mapStr.put(i, RandomStringUtils.randomAlphabetic(50));
    }

    List<Version> lver = new ArrayList<>();
    for (int i = 0; i < numTuples; i++) {

      int randomInd = (int) Math.ceil(rand.nextInt(numStrings)); // uniform
      //        int randomInd = (int) Math.ceil(rand.nextGaussian() * (0.2*numStrings) + ((0.5*numStrings))); // gaussian
      //        if(randomInd<0)
      //            randomInd = -randomInd;
      //        if(randomInd>=numStrings)
      //            randomInd = numStrings-1;

      //        System.out.println(randomInd);
      ////////////////////////////////////////////
      // tuple generation
      ////////////////////////////////////////////

      Tuple tuple = new Tuple();
      tuple.setAttribute(attr, mapStr.get(randomInd));
      for (int j = 0; j < numAttributes - 1; j++) {
        tuple.setAttribute(attr + j, mapStr.get(randomInd));
      }
      tuplesCacheString.put("infinispanKey" + i, tuple.toString());


      ////////////////////////////////////////////
      // leads index generation
      ////////////////////////////////////////////
      Version v = new VersionScalar(i);
      if (i == 0 || i == numTuples - 1)
        lver.add(v);
      LeadsIndex lInd = new LeadsIndexString();
      lInd.setCacheName("tuples");
      lInd.setAttributeName(attr);
      lInd.setAttributeValue(mapStr.get(randomInd));
      lInd.setKeyName("infinispanKey" + i);
      vcstmap.put(mapStr.get(randomInd), lInd);

    }

    List<Long> listTimes = new ArrayList<>();
    //      int randomInd = (int) Math.ceil(rand.nextGaussian() * (0.2*numStrings) + ((0.5*numStrings))); // gaussian
    //      if(randomInd<0)
    //          randomInd = -randomInd;
    //      if(randomInd>=numStrings)
    //          randomInd = numStrings-1;
    int randomInd = (int) Math.ceil(numStrings * 0.5);

    System.out.println("VersionedCacheShardedTreeMap Test");

    ////////////////////////////////////////////////////////////////////
    ///////////////// VersionedCacheShardedTreeMapImpl /////////////////
    ////////////////////////////////////////////////////////////////////

    for (int tm = 0; tm < 1; tm++) {
      startTime = System.currentTimeMillis();
      Collection<Version> vLeadsIndex;
      vLeadsIndex = vcstmap.get(mapStr.get(randomInd), lver.get(0), lver.get(1));
      System.out.println("cLeadsIndex: " + vLeadsIndex);
      for (Version ver : vLeadsIndex) {
        System.out.println("vcstmap: " + vcstmap);
        LeadsIndex ldi = (LeadsIndex) vcstmap.get(mapStr.get(randomInd), ver);
        System.out.println(ldi.getKeyName());
        System.out.println(ldi.getAttributeValue());
        resultIndexCache.put(ldi.getKeyName(), ldi.getAttributeValue());
      }
      stopTime = System.currentTimeMillis();
      listTimes.add(stopTime - startTime);
    }

    /////////////////////////////////////////////////////////////
    ///////////////// Average time for indexing /////////////////
    /////////////////////////////////////////////////////////////
    Long counter = 0L;
    for (Long lst : listTimes) {
      counter = counter + lst;
    }
    Long avgTime = counter / listTimes.size();
    System.out.println(avgTime + " ms\n");
    listTimes.clear();

    System.exit(0);
  }
}
