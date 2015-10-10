package eu.leads.processor;

import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.TupleBuffer;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.Site;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.ensemble.cache.distributed.HashBasedPartitioner;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by vagvaz on 8/31/15.
 */
public class BatchPutMain {
    static String dresden2 = "80.156.73.113:11222;80.156.73.116:11222;80.156.73.123:11222;80.156.73.128:11222";
    static String dd1a = "80.156.222.4:11222;80.156.222.18"; //qe8,qe9
    static String dd2c = "87.190.239.18:11222;87.190.239.130:11222"; //qe28,qe29
    static String softnet = "147.27.14.38:11222;147.27.14.37:11222";
    static String local = "192.168.178.43:11222;192.168.178.43:11223";
    static Map<String,String> clouds = new HashMap<>();
    static List<String> activeClouds = new ArrayList<>();
    static int numberOfkeys = 10;
    static boolean batchPut = false;
    static Map<String,EnsembleCacheManager> emanagers = new HashMap<>();
    static EnsembleCacheManager emanager;
    static Map<String,TupleBuffer> buffers = new HashMap<>();
    static HashBasedPartitioner partitioner;
    private static int threshold = 1000;
    private static String cacheName = "batchputTest";
    private static EnsembleCache ecache;
    private static EnsembleCache ecache2;
    private static int numberOfvalues = 1;
    private static HashMap<String,Integer> histogram;

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        clouds.put("dresden2",dresden2);
        clouds.put("dd1a",dd1a);
        clouds.put("dd2c",dd2c);
        clouds.put("softnet",softnet);
        clouds.put("localcluster", local);
        if(args.length > 0){
            readArguments(args);
        }

        LQPConfiguration.initialize();
        EnsembleCacheUtils.initialize();
        String ensembleString  = "";
        for(String cloudString : activeClouds){
            ensembleString += clouds.get(cloudString)+"|";
            //emanagers.put(cloudString,new EnsembleCacheManager(cloudString));
        }

        ensembleString = ensembleString.substring(0,ensembleString.length()-1);
        emanager = new EnsembleCacheManager(ensembleString);
        EnsembleCacheUtils.initialize(emanager,false);
        ArrayList elist = new ArrayList<>(emanager.sites());
        ecache = emanager.getCache(cacheName, new ArrayList<>(emanager.sites()),
            EnsembleCacheManager.Consistency.DIST);


        for(Object s : ecache.sites()){
            Site site = (Site)s;
            String eString = site.getName();
            buffers.put(site.getName(),new TupleBuffer(threshold,ecache, new EnsembleCacheManager(eString)));
            System.out.println("cache site: " + site.getName());
        }

//        for(Object s : elist){
//            System.out.println("manager site: " + ((Site)s).getName());
//        }
        ArrayList<EnsembleCache> cachesList = new ArrayList<>();
        for(Object s : new ArrayList<>(emanager.sites())){
            Site site = (Site)s;
            cachesList.add(site.getCache(ecache.getName()));
        }
        partitioner = new HashBasedPartitioner(cachesList);
        Map<String,Tuple> tuples = new HashMap<>();
        System.out.println("generating");
        for (int key = 0; key < numberOfkeys; key++) {
            String keyString = Integer.toString(key);
            Tuple tuple = getTuple(key);
            tuples.put(keyString,tuple);
        }
       histogram = new HashMap<>();
        System.out.println("Inserting");
        double step = 0.1;
        int counter = 0;
        long start = System.nanoTime();
        for (Map.Entry<String,Tuple> entry : tuples.entrySet()) {
            String keyString = entry.getKey();
            if(counter++ / (double)numberOfkeys > step)
            {
                System.out.println("inserted " + ((int)(100*counter / (double)numberOfkeys)) + "%");
                long end = System.nanoTime();
                double dur = (end - start)/1000000f;
                System.out.println("Duration: " + dur + " ms rate: " + (counter/dur)+ " per ms");
                step += 0.1;
            }
            Tuple tuple = entry.getValue();
            if(batchPut){
//                String mc = decideMC(keyString);
//                if(buffers.get(mc).add(keyString,tuple)){
//                    buffers.get(mc).flushToMC();
//                    updateHist(mc);
//                }
                EnsembleCacheUtils.putToCache(ecache,keyString,tuple);
            }
            else{
                try {
                    EnsembleCacheUtils.putToCache(ecache,keyString, tuple);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        if(batchPut){
            for (Map.Entry<String,TupleBuffer> buf : buffers.entrySet()){
                buf.getValue().flushToMC();
                buf.getValue().flushToMC();
            }
        }
        else{
            EnsembleCacheUtils.waitForAllPuts();
        }
        EnsembleCacheUtils.waitForAllPuts();
        long end = System.nanoTime();
        double dur = (end - start)/1000000f;
        System.out.println("Duration: " + dur + " ms rate: " + (numberOfkeys/dur)+ " per ms");
        PrintUtilities.printMap(histogram);
        int counter3 = 0;
        int found = 0;
        Random r = new Random();
        for(int i =0; i < 100; i++){
            int index = r.nextInt(numberOfkeys);
            Tuple t = (Tuple) ecache.get(Integer.toString(index));
            counter3++;
            if(t != null){
                if(!t.toString().equals(tuples.get(index))){
                    found++;
                }
            }
        }
        System.out.println("Found " + found + " read " + counter3);
        System.exit(0);

    }

    private static void updateHist(String mc) {
        Integer c = histogram.get(mc);
        if(c== null)
            c = 1;
        else
            c+= 1;
        histogram.put(mc,c);
    }

    private static String decideMC(String keyString) {
        EnsembleCache cache = partitioner.locate(keyString);
        String result = "";
        for(Object s : cache.sites()){
            Site site = (Site)s;
//            System.out.println("site: " + site.getName());
            result = site.getName();
        }

//        return result.substring(1,result.length());
        return result;
    }

    private static Tuple getTuple(int key) {
        Tuple t = new Tuple();
        t.setAttribute("key",key);
        for(int i = 0; i < numberOfvalues; i++){
            t.setAttribute("val"+i,"-val-"+i);
        }
        return t;
    }

    private static void readArguments(String[] args) {
        LQPConfiguration.initialize();
//        InfinispanClusterSingleton.getInstance().getManager();
        switch (args.length){
            case 1:
                String[] clouds = args[0].split(",");
                activeClouds.addAll(Arrays.asList(clouds));
                numberOfkeys = 10000;
                batchPut = false;
                break;
            case 2:
                clouds = args[0].split(",");
                activeClouds.addAll(Arrays.asList(clouds));
                numberOfkeys = Integer.parseInt(args[1]);
                batchPut = false;
                break;
            case 3:
                clouds = args[0].split(",");
                activeClouds.addAll(Arrays.asList(clouds));
                numberOfkeys = Integer.parseInt(args[1]);
                batchPut = Boolean.parseBoolean(args[2]);
                break;
        }

    }


}
