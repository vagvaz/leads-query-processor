package eu.leads.processor.core.index;

import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import org.apache.commons.lang.RandomStringUtils;
import org.infinispan.Cache;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.QueryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by angelos on 11/02/15.
 */
public class TestLeadsIndexSelectivityGaussian {

  public static void main(String[] args) {
    LQPConfiguration.initialize();
    InfinispanManager man = InfinispanClusterSingleton.getInstance().getManager();
    Cache cache = (Cache) man.getPersisentCache("indexedCache");

    int numStrings = 10000;// 10000
    int numTuples = 150000;// 150000

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
      lstInt.add(rand.nextInt());
      lstDouble.add(rand.nextDouble());
      lstFloat.add(rand.nextFloat());
      lstLong.add(rand.nextLong());
    }

    for (int i = 0; i < numTuples; i++) {
      randomInd = (int) Math.ceil(rand.nextGaussian() * (numStrings / 6) + ((numStrings / 2))); // gaussian
      LeadsIndex lInd = new LeadsIndexString();

      if (randomInd < 0)
        randomInd = -randomInd;

      if (randomInd >= numStrings)
        randomInd = numStrings - 1;

      lInd.setCacheName("indexedCache");
      lInd.setAttributeName("attributeName");
      lInd.setAttributeValue(lstStr.get(randomInd));
      lInd.setKeyName("infinispanKey" + i);

      cache.put("infinispanKey" + i, lInd);
    }

    List<Long> listTimes = new ArrayList<>();

    // 1st query N/2
    for (int tm = 0; tm < 100; tm++) {
      long startTime = System.currentTimeMillis();
      SearchManager sm = org.infinispan.query.Search.getSearchManager(cache);
      QueryFactory qf = sm.getQueryFactory();
      randomInd = (int) Math.ceil(numStrings * 0.5);
      if (randomInd < 0)
        randomInd = -randomInd;
      if (randomInd >= numStrings)
        randomInd = numStrings - 1;
      org.infinispan.query.dsl.Query lucenequery =
          qf.from(LeadsIndexString.class).having("attributeName").eq("attributeName").and().having("attributeValue")
              .eq(lstStr.get(randomInd)).toBuilder().build();
      List<LeadsIndex> list = lucenequery.list();
      long stopTime = System.currentTimeMillis();
      listTimes.add(stopTime - startTime);
    }

    Long counter = 0L;
    for (Long lst : listTimes) {
      counter = counter + lst;
    }
    Long avgTime = counter / 100;
    System.out.println(avgTime + " ms\n");
    listTimes.clear();

    // 2nd query 0.05N
    for (int tm = 0; tm < 100; tm++) {
      long startTime = System.currentTimeMillis();
      SearchManager sm = org.infinispan.query.Search.getSearchManager(cache);
      QueryFactory qf = sm.getQueryFactory();
      randomInd = (int) Math.ceil(numStrings * 0.05);
      if (randomInd < 0)
        randomInd = -randomInd;
      if (randomInd >= numStrings)
        randomInd = numStrings - 1;
      org.infinispan.query.dsl.Query lucenequery =
          qf.from(LeadsIndexString.class).having("attributeName").eq("attributeName").and().having("attributeValue")
              .eq(lstStr.get(randomInd)).toBuilder().build();
      List<LeadsIndex> list = lucenequery.list();
      long stopTime = System.currentTimeMillis();
      listTimes.add(stopTime - startTime);
    }

    counter = 0L;
    for (Long lst : listTimes) {
      counter = counter + lst;
    }
    avgTime = counter / 100;
    System.out.println(avgTime + " ms\n");
    listTimes.clear();

    // 3rd query 0.95N
    for (int tm = 0; tm < 100; tm++) {
      long startTime = System.currentTimeMillis();
      SearchManager sm = org.infinispan.query.Search.getSearchManager(cache);
      QueryFactory qf = sm.getQueryFactory();
      randomInd = (int) Math.ceil(numStrings * 0.95);
      if (randomInd < 0)
        randomInd = -randomInd;
      if (randomInd >= numStrings)
        randomInd = numStrings - 1;
      org.infinispan.query.dsl.Query lucenequery =
          qf.from(LeadsIndexString.class).having("attributeName").eq("attributeName").and().having("attributeValue")
              .eq(lstStr.get(randomInd)).toBuilder().build();
      List<LeadsIndex> list = lucenequery.list();
      long stopTime = System.currentTimeMillis();
      listTimes.add(stopTime - startTime);
    }

    counter = 0L;
    for (Long lst : listTimes) {
      counter = counter + lst;
    }
    avgTime = counter / 100;
    System.out.println(avgTime + " ms\n");
    listTimes.clear();

    // 4th query random Guassian
    for (int tm = 0; tm < 100; tm++) {
      long startTime = System.currentTimeMillis();
      SearchManager sm = org.infinispan.query.Search.getSearchManager(cache);
      QueryFactory qf = sm.getQueryFactory();
      randomInd = (int) Math.ceil(rand.nextGaussian() * (numStrings / 5) + (numStrings / 2)); // gaussian
      if (randomInd < 0)
        randomInd = -randomInd;

      if (randomInd >= numStrings)
        randomInd = numStrings - 1;
      org.infinispan.query.dsl.Query lucenequery =
          qf.from(LeadsIndexString.class).having("attributeName").eq("attributeName").and().having("attributeValue")
              .eq(lstStr.get(randomInd)).toBuilder().build();
      List<LeadsIndex> list = lucenequery.list();
      long stopTime = System.currentTimeMillis();
      listTimes.add(stopTime - startTime);
    }

    counter = 0L;
    for (Long lst : listTimes) {
      counter = counter + lst;
    }
    avgTime = counter / 100;
    System.out.println(avgTime + " ms\n");
    listTimes.clear();


    System.exit(0);
  }
}
