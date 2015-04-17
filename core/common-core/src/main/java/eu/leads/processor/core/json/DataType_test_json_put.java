package eu.leads.processor.core.json;

import eu.leads.processor.common.infinispan.CacheManagerFactory;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import org.infinispan.Cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by angelos on 22/01/15.
 */
public class DataType_test_json_put {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        /*Create cache to store the tuples*/
        LQPConfiguration.initialize();
        InfinispanManager man = InfinispanClusterSingleton.getInstance().getManager();
        Cache map = (Cache) man.getPersisentCache("queriesfoo");
        InfinispanManager man2 = CacheManagerFactory.createCacheManager();
        Cache map2 = (Cache) man2.getPersisentCache("queriesfoo");

        /*Create attribute names*/
        String attributeName1 = "name1";
        String attributeName2 = "name2";
        String attributeName3 = "name3";
        String attributeName4 = "name4";
        String attributeName5 = "name5";
        String attributeName6 = "name6";
        String attributeName7 = "longnum1";
        String attributeName8 = "longnum2";
        String attributeName9 = "doublenum1";
        String attributeName10 = "doublenum2";

        /*Create attributes' values*/
        String value1 = "b9re9dmqls44ced";
        String value2 = "q2vklxkkexqxh1m";
        String value3 = "69eoihvawk6gco5";
        String value4 = "gd5taacfygbwwc2";
        String value5 = "yubuqp2zqv6hmwi";
        String value6 = "0fdyakype0bxu38";
        Long value7 = 3965960296543005006L;
        Long value8 = -5807565109641799382L;
        Double value9 = 0.7535329047380597;
        Double value10 = 0.47334616297362775;

        /*Create #N_tuples and isnert to an Arraylist*/
        int N_tuples=1000;
        ArrayList<Tuple> arrlstJson = new ArrayList<>();

        for(int i=0; i<N_tuples; i++){
            Tuple tJson = new Tuple();
            tJson.setAttribute(attributeName1, i+value1);
            tJson.setAttribute(attributeName2, i+value2);
            tJson.setAttribute(attributeName3, i+value3);
            tJson.setAttribute(attributeName4, i+value4);
            tJson.setAttribute(attributeName5, i+value5);
            tJson.setAttribute(attributeName6, i+value6);
            tJson.setNumberAttribute(attributeName7, i+value7);
            tJson.setNumberAttribute(attributeName8, i+value8);
            tJson.setNumberAttribute(attributeName9, i+value9);
            tJson.setNumberAttribute(attributeName10, i+value10);
            arrlstJson.add(tJson);
//            System.out.println(tJson.asString());
        }

        /*Iterate Arraylist, get tuple and insert tuple into the cache
        * also mesaure time and memory*/
        long startTime = System.currentTimeMillis();
        for(int i=0; i<N_tuples; i++)
            map.put("infinispanKey" + i, arrlstJson.get(i));

        long stopTime = System.currentTimeMillis();
        System.out.println("Runtime json: " + (stopTime-startTime) + " ms\n");
        System.exit(0);
    }
}