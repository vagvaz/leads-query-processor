package eu.leads.processor.core;

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
public class DataType_test_avro_put {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
/*Create cache to store the tuples*/
        LQPConfiguration.initialize();
        InfinispanManager man = InfinispanClusterSingleton.getInstance().getManager();
        Cache map = (Cache) man.getPersisentCache("queriesfoo");
//         InfinispanManager man2 = CacheManagerFactory.createCacheManager();
//         Cache map2 = (Cache) man2.getPersisentCache("queriesfoo");
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
        ArrayList<Tuple> arrlstavro = new ArrayList<>();

        for(int i=0; i<N_tuples; i++){
            Tuple tavro = new Tuple();
            tavro.setAttribute(attributeName1, i+value1);
            tavro.setAttribute(attributeName2, i+value2);
            tavro.setAttribute(attributeName3, i+value3);
            tavro.setAttribute(attributeName4, i+value4);
            tavro.setAttribute(attributeName5, i+value5);
            tavro.setAttribute(attributeName6, i + value6);
            tavro.setNumberAttribute(attributeName7, i+value7);
            tavro.setNumberAttribute(attributeName8, i+value8);
            tavro.setNumberAttribute(attributeName9, i+value9);
            tavro.setNumberAttribute(attributeName10, i + value10);
            arrlstavro.add(tavro);
        }
/*Iterate Arraylist, get tuple and insert tuple into the cache
* also mesaure time and memory*/
        long startTime = System.currentTimeMillis();
        for(int i=0; i<N_tuples; i++) {
            map.put("infinispanKey" + i, arrlstavro.get(i));
        }
        long stopTime = System.currentTimeMillis();
        System.out.println("Runtime avro: " + (stopTime-startTime) + " ms\n");
// long size = FileUtils.sizeOfDirectory(new File("/tmp/leveldb"));
// System.out.println("Folder Size: " + (size/(1024*1024)) + " MB");
// /*delete directory /tmp/leveldb to run again*/
// FileUtils.deleteDirectory(new File("/tmp/leveldb"));
        System.exit(0);
    }
}