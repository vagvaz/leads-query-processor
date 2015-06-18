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
public class TestLeadsIndex {

    public static void main(String[] args){
        LQPConfiguration.initialize();
//        InfinispanManager man2 = CacheManagerFactory.createCacheManager();
//        Cache cachefoo = (Cache) man2.getPersisentCache("queriesfoo");
        InfinispanManager man = InfinispanClusterSingleton.getInstance().getManager();
        Cache cache = (Cache) man.getPersisentCache("defaultCache");

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
        for(int i=0;i<numStrings; i++){
            lstStr.add(RandomStringUtils.randomAlphabetic(10));
            lstInt.add(rand.nextInt());
            lstDouble.add(rand.nextDouble());
            lstFloat.add(rand.nextFloat());
            lstLong.add(rand.nextLong());
        }
        long stopTime = System.currentTimeMillis();
        System.out.println("Runtime RandomStringUtils: " + (stopTime-startTime) + " ms\n");

        startTime = System.currentTimeMillis();
        for(int i=0;i<numTuples;i++){
            randomInd = rand.nextInt(numStrings-1);// uniform
            LeadsIndex lInd = new LeadsIndexString();

            lInd.setCacheName("indexedCache");

            lInd.setAttributeName("attributeName");

            lInd.setAttributeValue(lstStr.get(randomInd));

            lInd.setKeyName("infinispanKey" + i);

            cache.put("infinispanKey" + i, lInd);
        }

        stopTime = System.currentTimeMillis();
        System.out.println("Runtime Tuples: " + (stopTime-startTime) + " ms\n");

        // get the results for predicate example (bring all LeadIndex me attributeValue = "mystring" AND attributeName = "thestring")
        startTime = System.currentTimeMillis();

        // create query
        SearchManager sm = org.infinispan.query.Search.getSearchManager(cache);
        QueryFactory qf = sm.getQueryFactory();
        org.infinispan.query.dsl.Query lucenequery = qf.from(LeadsIndexString.class)
                .having("attributeName").eq("attributeName")
                .toBuilder().build();
        List<LeadsIndex> list = lucenequery.list();

        stopTime = System.currentTimeMillis();
        System.out.println("Runtime Query: " + (stopTime-startTime) + " ms\n");

        System.exit(0);
    }
}
