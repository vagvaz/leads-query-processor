package eu.leads.processor;

import eu.leads.processor.common.infinispan.TupleBuffer;
import eu.leads.processor.core.Tuple;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.Site;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.ensemble.cache.distributed.HashBasedPartitioner;

import java.util.*;

/**
 * Created by vagvaz on 8/31/15.
 */
public class BatchPutMain {
    static String dresden2 = "80.156.73.113:11222;80.156.73.116:11222";
    static String dd1a = "80.156.222.4:11222;80.156.222.18"; //qe8,qe9
    static String dd2c = "87.190.239.18:11222;87.190.239.130:11222"; //qe28,qe29
    static String softnet = "clu25.softnet.tuc.gr:11222;clu24.softnet.tuc.gr:11223";
    static String local = "147.27.14.80:11222;147.27.14.80:11223";
    static Map<String,String> clouds = new HashMap<>();
    static List<String> activeClouds = new ArrayList<>();
    static int numberOfkeys = 0;
    static boolean batchPut = false;
    static Map<String,EnsembleCacheManager> emanagers = new HashMap<>();
    static EnsembleCacheManager emanager;
    static Map<String,TupleBuffer> buffers = new HashMap<>();
    static HashBasedPartitioner partitioner;
    private static int threshold = 500;
    private static String cacheName = "batchPutTest";
    private static EnsembleCache ecache;
    private static EnsembleCache ecache2;
    private static int numberOfvalues = 5;

    public static void main(String[] args) {
        clouds.put("dresden2",dresden2);
        clouds.put("dd1a",dd1a);
        clouds.put("dd2c",dd2c);
        clouds.put("softnet",softnet);
        clouds.put("localcluster",local);
        if(args.length > 0){
            readArguments(args);
        }

        String ensembleString  = "";
        for(String cloudString : activeClouds){
            ensembleString += clouds.get(cloudString)+"|";
            //emanagers.put(cloudString,new EnsembleCacheManager(cloudString));
        }

        ensembleString = ensembleString.substring(0,ensembleString.length()-1);
        emanager = new EnsembleCacheManager(ensembleString);
        ArrayList elist = new ArrayList<>(emanager.sites());
        ecache = emanager.getCache(cacheName, new ArrayList<>(emanager.sites()),
            EnsembleCacheManager.Consistency.DIST);


        for(Object s : ecache.sites()){
            Site site = (Site)s;
            buffers.put(site.getName(),new TupleBuffer(threshold,ecache, new EnsembleCacheManager(ecache.getName().substring(1,ecache.getName().length()))));
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
        for (int key = 0; key < numberOfkeys; key++) {
            String keyString = Integer.toString(key);
            Tuple tuple = getTuple(key);
            tuples.put(keyString,tuple);
        }

        long start = System.nanoTime();
        for (Map.Entry<String,Tuple> entry : tuples.entrySet()) {
            String keyString = entry.getKey();
            Tuple tuple = entry.getValue();
            if(batchPut){
                String mc = decideMC(keyString);
                if(buffers.get(mc).add(keyString,tuple)){
                    buffers.get(mc).flushToMC();
                }
            }
            else{
                try {
                    ecache.put(keyString, tuple);
                }catch (Exception e){

                }
            }
        }
        long end = System.nanoTime();
        double dur = (end - start)/1000000f;
        System.out.println("Duration: " + dur + " ms rate: " + (numberOfkeys/dur)+ " per ms");

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
            t.setAttribute("val"+i,Integer.toString(i)+"-value-"+Integer.toString(key));
        }
        return t;
    }

    private static void readArguments(String[] args) {
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
