package eu.leads.processor.core.index;

import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import org.apache.commons.lang.RandomStringUtils;
import org.infinispan.Cache;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.FilterConditionContext;
import org.infinispan.query.dsl.QueryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by angelos on 11/02/15.
 */
public class TestLeadsIndex {

  public static void main(String[] args) {
    LQPConfiguration.initialize();
    //InfinispanManager man = CacheManagerFactory.createCacheManager();
    //        Cache cachefoo = (Cache) man2.getPersisentCache("queriesfoo");
    InfinispanManager man = InfinispanClusterSingleton.getInstance().getManager();
    Cache cache = (Cache) man.getIndexedPersistentCache("IIdefaultCache");

    int numStrings = 1000;// 10000
    int numTuples = 500;// 15000000

    // find number of generated tuples: run until memory exception
    List<String> lstStr = new ArrayList<>();
    List<Integer> lstInt = new ArrayList<>();
    List<Double> lstDouble = new ArrayList<>();
    List<Float> lstFloat = new ArrayList<>();
    List<Long> lstLong = new ArrayList<>();
    Random rand = new Random();
    int randomInd;

    long startTime = System.currentTimeMillis();
    for (int i = 0; i < numStrings; i++) {
      lstStr.add(RandomStringUtils.randomAlphabetic(10));
      lstInt.add(rand.nextInt());
      lstDouble.add(rand.nextDouble());
      lstFloat.add(rand.nextFloat());
      lstLong.add(rand.nextLong());
    }
    long stopTime = System.currentTimeMillis();
    System.out.println("Runtime RandomStringUtils: " + (stopTime - startTime) + " ms\n");

    startTime = System.currentTimeMillis();
    for (int i = 0; i < numTuples; i++) {
      randomInd = rand.nextInt(numStrings - 1);// uniform
      LeadsIndex lInd = new LeadsIndexString();

      lInd.setCacheName("indexedCache");

      lInd.setAttributeName("attributeName");

      lInd.setAttributeValue(lstStr.get(randomInd));

      lInd.setKeyName("infinispanKey" + i);

      cache.put("infinispanKey" + i, lInd);
    }
    LeadsIndex lInd = new LeadsIndexString();
    lInd.setCacheName("indexedCache");
    lInd.setAttributeName("attributeName");
    lInd.setAttributeValue("teeest1");
    lInd.setKeyName("infinispanKey" + 788);

    cache.put("infinispanKey" + 8, lInd);
    stopTime = System.currentTimeMillis();
    System.out.println("Runtime Tuples: " + (stopTime - startTime) + " ms\n");

    // get the results for predicate example (bring all LeadIndex me attributeValue = "mystring" AND attributeName = "thestring")
    startTime = System.currentTimeMillis();

    // create query
    SearchManager sm = org.infinispan.query.Search.getSearchManager(cache);
    QueryFactory qf = sm.getQueryFactory();
    FilterConditionContext lucenequery =
        qf.from(LeadsIndexString.class).having("attributeValue").eq("teeest1").and().having("attributeValue")
            .eq("teeest1");
    System.out.print(lucenequery.toString());
    lucenequery.toBuilder().build();
    List<LeadsIndex> list = lucenequery.toBuilder().build().list();

    stopTime = System.currentTimeMillis();
    System.out.println("Runtime Query: " + (stopTime - startTime) + " ms \n" + list.size());

    System.exit(0);
  }
}
