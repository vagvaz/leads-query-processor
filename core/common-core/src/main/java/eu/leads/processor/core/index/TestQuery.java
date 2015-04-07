package eu.leads.processor.core.index;

import eu.leads.processor.common.infinispan.CacheManagerFactory;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.infinispan.operators.AttributeFilter;
import org.apache.commons.lang.RandomStringUtils;
import org.infinispan.Cache;
import org.infinispan.container.entries.CacheEntry;
import org.infinispan.iteration.EntryIterable;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.QueryFactory;

import java.util.*;

/**
 * Created by vagvaz on 2/15/15.
 */
public class TestQuery {
//  private static Queue<NotifyingFuture> buffer = new LinkedList<>();

  public static void main(String[] args) {
    LQPConfiguration.initialize();
    InfinispanManager man = InfinispanClusterSingleton.getInstance().getManager();
    // comment out for uniform distributed data
    InfinispanManager man2 = CacheManagerFactory.createCacheManager();
    InfinispanManager man3 = CacheManagerFactory.createCacheManager();
//    InfinispanManager man4 = CacheManagerFactory.createCacheManager();
    Cache cache = (Cache) man.getPersisentCache("indexedCache");
    Cache tuplesCacheString = (Cache) man.getPersisentCache("tuplesString");
    Cache resultIndexCache = (Cache)man.getPersisentCache("resultIndex");
    Cache resultTrivialCache = (Cache)man.getPersisentCache("resultTuples");
    int numStrings = 500;// 10000
    int numTuples = 2000;// 150000
    int numAttributes = 5;// 100
    long startTime,stopTime;
    Map<Integer,String> mapStr = new HashMap<>();
    Random rand = new Random();

    String attr = "attributeName";//"attributeName"+rand.nextInt(numAttributes);

    for(int i=0;i<numStrings; i++){
        mapStr.put(i, RandomStringUtils.randomAlphabetic(50));
    }

    for(int i=0;i<numTuples;i++){
        int randomInd = (int) Math.ceil(rand.nextGaussian() * (0.2*numStrings) + ((0.5*numStrings))); // gaussian
//        int randomInd = (int) Math.ceil(rand.nextInt(numStrings));// uniform

        ////////////////////////////////////////////
        // tuple generation
        ////////////////////////////////////////////

        Tuple tuple = new Tuple();
        tuple.setAttribute(attr,mapStr.get(randomInd));
        for(int j=0;j<numAttributes-1;j++){
            tuple.setAttribute(attr+j,mapStr.get(randomInd));
        }
        tuplesCacheString.put("infinispanKey" + i, tuple.toString());


        ////////////////////////////////////////////
        // leads index generation
        ////////////////////////////////////////////

        LeadsIndex lInd = new LeadsIndexString();
        lInd.setCacheName("tuples");
        lInd.setAttributeName(attr);
        lInd.setAttributeValue(mapStr.get(randomInd));
        lInd.setKeyName("infinispanKey" + i);
        cache.put("infinispanKey" + i, lInd);

    }

    List<Long> listTimes = new ArrayList<>();
    int randomInd = (int) Math.ceil(numStrings * 0.5);

    ////////////////////////////////////////////
    ///////////////// indexing /////////////////
    ////////////////////////////////////////////

    for(int tm=0; tm<100; tm++) {
        startTime = System.currentTimeMillis();
        SearchManager sm = org.infinispan.query.Search.getSearchManager(cache);
        QueryFactory qf = sm.getQueryFactory();
        org.infinispan.query.dsl.Query lucenequery = qf.from(LeadsIndexString.class)
                                                        .having("attributeName").eq(attr)
                                                        .and()
                                                        .having("attributeValue").eq(mapStr.get(randomInd))
                                                        .toBuilder().build();
        List<LeadsIndex> list = lucenequery.list();

        for(LeadsIndex indexTuple : list){
            resultIndexCache.put(indexTuple.getKeyName(), indexTuple.getAttributeValue());
        }

        stopTime = System.currentTimeMillis();
        listTimes.add(stopTime - startTime);
    }

    Long counter=0L;
    for(Long lst : listTimes){
        counter = counter + lst;
    }
    Long avgTime = counter/listTimes.size();
    System.out.println(avgTime+ " ms\n");
    listTimes.clear();

    ////////////////////////////////////////////
    //////////////// filtering /////////////////
    ////////////////////////////////////////////

    for (int iteration = 0; iteration < 100; iteration++) {

        startTime = System.currentTimeMillis();
        try {
            AttributeFilter filter = new AttributeFilter(attr, mapStr.get(randomInd));
            try (EntryIterable<String, String> iterable = tuplesCacheString.getAdvancedCache().filterEntries(filter)){
                for (CacheEntry<String, String> entry : iterable) {
                    resultTrivialCache.put(entry.getKey(), entry.getValue());
                }
                iterable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopTime = System.currentTimeMillis();
        listTimes.add(stopTime - startTime);
    }
    counter=0L;
    for(Long lst : listTimes){
        counter = counter + lst;
    }
    avgTime = counter/listTimes.size();
    System.out.println(avgTime+ " ms\n");
      listTimes.clear();

    System.exit(0);
  }
}