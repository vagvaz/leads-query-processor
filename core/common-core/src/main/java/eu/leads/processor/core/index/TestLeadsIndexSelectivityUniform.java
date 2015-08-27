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
public class TestLeadsIndexSelectivityUniform {

    public static void main(String[] args){
        LQPConfiguration.initialize();
//        InfinispanManager man2 = CacheManagerFactory.createCacheManager();
//        Cache cachefoo = (Cache) man2.getPersisentCache("queriesfoo");
        InfinispanManager man = InfinispanClusterSingleton.getInstance().getManager();
        Cache cache = (Cache) man.getIndexedPersistentCache("indexedCache");
        System.out.println("Creating Tuples");
        int numStrings = 10;// 10000
        int numTuples = 150;// 150000

        // find number of generated tuples: run until memory exception
        List<String> lstStr = new ArrayList<>();
        List<Integer> lstInt = new ArrayList<>();
        List<Double> lstDouble = new ArrayList<>();
        List<Float> lstFloat = new ArrayList<>();
        List<Long> lstLong = new ArrayList<>();
        Random rand = new Random();
        int randomInd;

        for(int i=0;i<numStrings; i++){
            lstStr.add(RandomStringUtils.randomAlphabetic(10));
            lstInt.add(rand.nextInt());
            lstDouble.add(rand.nextDouble());
            lstFloat.add(rand.nextFloat());
            lstLong.add(rand.nextLong());
        }
        System.out.println("putting Tuples");

        for(int i=0;i<numTuples;i++){
            randomInd = rand.nextInt(numStrings);// uniform
            LeadsIndex lInd = new LeadsIndexString();

            if(randomInd<0)
                randomInd = -randomInd;

            if(randomInd>=numStrings)
                randomInd = numStrings-1;

            lInd.setCacheName("indexedCache");
            lInd.setAttributeName("attributeName");
            lInd.setAttributeValue(lstStr.get(randomInd));
            lInd.setKeyName("infinispanKey" + i);

            cache.put("infinispanKey" + i, lInd);
        }
        System.out.println("putting Tuples finished");

        List<Long> listTimes = new ArrayList<>();

        // query
        for(int tm=0; tm<100; tm++) {
            long startTime = System.currentTimeMillis();
            SearchManager sm = org.infinispan.query.Search.getSearchManager(cache);
            QueryFactory qf = sm.getQueryFactory();
            randomInd = rand.nextInt(numStrings);// uniform
            org.infinispan.query.dsl.Query lucenequery = qf.from(LeadsIndexString.class)
                    .having("attributeName").eq("attributeName")
                    .and()
                    .having("attributeValue").eq(lstStr.get(randomInd))
                    .toBuilder().build();
            List<LeadsIndex> list = lucenequery.list();
            for (LeadsIndex lst : list) {
                System.out.println(lst.getAttributeName()+":"+lst.getAttributeValue()+":"+lst.getKeyName());
            }
            long stopTime = System.currentTimeMillis();
            listTimes.add(stopTime - startTime);
        }

        Long counter=0L;
        for(Long lst : listTimes){
            counter = counter + lst;
        }
        Long avgTime = counter/100;
        System.out.println(avgTime+ " ms\n");

        System.exit(0);
    }
}
