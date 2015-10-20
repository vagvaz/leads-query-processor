package eu.leads.processor.core.index;

import eu.leads.processor.common.infinispan.CacheManagerFactory;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import org.apache.commons.lang.RandomStringUtils;
import org.infinispan.Cache;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by angelos on 11/02/15.
 */
public class TestLeadsIndexOrderBy {

  public static void main(String[] args) {
    LQPConfiguration.initialize();
    //        InfinispanManager man2 = InfinispanClusterSingleton.getInstance().getManager();
    //        Cache cachefoo = (Cache) man2.getPersisentCache("queriesfoo");
    InfinispanManager man = CacheManagerFactory.createCacheManager();
    Cache cache = (Cache) man.getIndexedPersistentCache("indexedCache");

    int numStrings = 5;// 10000
    int numTuples = 8;// 1500000

    // find number of generated tuples: run until memory exception
    List<String> lstStr = new ArrayList<>();
    List<Integer> lstInt = new ArrayList<>();
    List<Double> lstDouble = new ArrayList<>();
    List<Float> lstFloat = new ArrayList<>();
    List<Long> lstLong = new ArrayList<>();
    Random rand = new Random();
    int randomInd;

    for (int i = 0; i < numStrings; i++) {
      lstStr.add(RandomStringUtils.randomAlphabetic(10));
      lstInt.add(rand.nextInt(10));
      lstDouble.add(rand.nextDouble());
      lstFloat.add(rand.nextFloat());
      lstLong.add(rand.nextLong());
    }
    for (int i = 0; i < numTuples; i++) {
      randomInd = rand.nextInt(numStrings);
      LeadsIndex lInd = new LeadsIndexString();

      lInd.setCacheName("indexedCache");

      lInd.setAttributeName("attributeName");

      lInd.setAttributeValue(lstStr.get(randomInd));
      //            lInd.setAttributeValueDouble(lstDouble.get(randomInd));
      //            lInd.setAttributeValueFloat(lstFloat.get(randomInd));
      //            lInd.setAttributeValue(lstInt.get(randomInd));
      //            lInd.setAttributeValueLong(lstLong.get(randomInd));

      lInd.setKeyName("infinispanKey" + i);

      cache.put("infinispanKey" + i, lInd);
    }


    // get the results for predicate example (bring all LeadIndex me attributeValue = "mystring" AND attributeName = "thestring")
    long startTime = System.currentTimeMillis();

    // create query
    SearchManager sm = org.infinispan.query.Search.getSearchManager(cache);
    QueryFactory qf = sm.getQueryFactory();
    org.infinispan.query.dsl.Query lucenequery = qf.from(LeadsIndexString.class).orderBy("attributeValue",
        SortOrder.DESC) // for negatives inverse operation of ASC DESC
        .having("attributeName").like("attributeName").toBuilder().build();
    List<LeadsIndex> list = lucenequery.list();

    long stopTime = System.currentTimeMillis();
    System.out.println("Runtime Query: " + (stopTime - startTime) + " ms\n");

    System.out.println("Query returns: ");
    for (LeadsIndex lst : list) {
      System.out.println(lst.getAttributeName() + ":" + lst.getAttributeValue());
    }

    System.exit(0);
  }
}
