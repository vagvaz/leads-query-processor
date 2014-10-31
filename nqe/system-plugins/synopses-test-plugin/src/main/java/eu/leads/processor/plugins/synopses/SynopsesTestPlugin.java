package eu.leads.processor.plugins.synopses;

import cern.jet.random.engine.RandomEngine;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.plugins.PluginInterface;
import eu.leads.processor.plugins.synopses.custom_objs_utils.StringFunnel;
import eu.leads.processor.plugins.synopses.whole_stream_structs.BloomFilter;
import eu.leads.processor.plugins.synopses.whole_stream_structs.CM_Sketch;
import org.apache.commons.configuration.Configuration;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Class implementing a test exploiting sketch structures.
 *
 * We consider a scenario when a user wants to approximately
 * monitor the frequency of some specific words of interest
 * (e.g. Adidas wants to monitor the frequency of crawling
 * the words {Reebok, Nike, Puma, Admiral} in order to
 * discover possible increase/decrease of popularity
 * of those competitive companies).
 *
 * Each node of the distributed crawler maintains a bloom filter
 * structure, which contains the words of interest, i.e.
 * we have turned on the bits corresponding to the hash
 * values of those words.
 * This Bloom Filter structure is materialized at each one of the
 * participating nodes.
 *
 *
 * During the crawling phase, the parsed content of each web-page
 * is tokenized, and each token is queried against the bloom filter.
 * In the case that a token may exist in the bloom filter
 * (i.e. it may be one of the words of interest),
 * we place it in a local CM sketch structure.
 *
 * The CM sketch structures are periodically synchronized
 * with the KVS.
 * The answer returned to the user querying about the frequency
 * of the words of his/her interest will be derived from the sum
 * of the frequencies derived from the local CM sketches.
 *
 *
 */
public class SynopsesTestPlugin implements PluginInterface {

    private String id;
    private Cache targetCache;
    private List<String> attributes;
    private Logger log = LoggerFactory.getLogger(SynopsesTestPlugin.class);

    private ArrayList<String> wordsOfInterest;
    private StringTokenizer st;
    private BloomFilter<String> mybf;
    private CM_Sketch<String> mycm;

    private int ctr;

    private HashMap<String, Integer> myFrequencies;
    private String nodeName;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String s) {
        this.id = s;
    }

    @Override
    public String getClassName() {
        return SynopsesTestPlugin.class.getCanonicalName();
    }

    @Override
    public void initialize(Configuration configuration, InfinispanManager infinispanManager) {
        String targetCacheName = configuration.getString("cache");
        if ( targetCacheName != null || !targetCacheName.equals("") ) {
            targetCache = (Cache) infinispanManager.getPersisentCache(targetCacheName);
        } else {
            log.error("TargetCache is not defined using default for not breaking");
            targetCache = (Cache) infinispanManager.getPersisentCache("default");
        }
        attributes = configuration.getList("attributes");

        wordsOfInterest = new ArrayList<>(2);
        wordsOfInterest.add("News");
        wordsOfInterest.add("Yahoo");

        RandomEngine generator = new cern.jet.random.engine.MersenneTwister64(new Date());
        mybf = new BloomFilter<String>(StringFunnel.INSTANCE, wordsOfInterest.size(), 0.01, generator.nextInt());

        //hashmap used for testing purposes
        myFrequencies = new HashMap<String, Integer>();
        for (String s: wordsOfInterest) {
            mybf.put(s);
            myFrequencies.put(s, 0);
        }

        mycm = new CM_Sketch<String>(StringFunnel.INSTANCE, 0.01, 0.01);
        ctr = 0;

        nodeName = infinispanManager.getCacheManager().getAddress().toString();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void modified(Object key, Object value, Cache<Object, Object> objectObjectCache) {
        Tuple t = new Tuple(value.toString());
        processTuple(key.toString(), t);
    }

    protected void processTuple(String key, Tuple tuple) {

        if (ctr > 100){
            ctr = 0;

            for (String s: wordsOfInterest) {

                targetCache.put(nodeName+s, mycm.estimateCount(s) );

                System.out.println(s+": true frequency: "+myFrequencies.get(s)+
                        ", estimated through CM sketch: "+
                            mycm.estimateCount(s));


            }
        }

        tuple.keepOnly(attributes);

        String content = tuple.getAttribute("body");
        st = new StringTokenizer(content);
        String tmp;
        while (st.hasMoreTokens()){

            tmp = st.nextToken();

            if ( mybf.mightContain(tmp) ){

                //if the tokenized word might be contained
                //in the bloom filter structure,
                //i.e., it is possible that it
                //is one of the initially selected words of interest,
                //then we place it into our local CM sketch structure
                //in order to track its respective frequency
                mycm.put(tmp);
                ctr++;
            }

            //used only for testing purposes
            if ( myFrequencies.containsKey(tmp) )
                myFrequencies.put(tmp, myFrequencies.get(tmp)+1);
        }
    }

    @Override
    public void created(Object key, Object value, Cache<Object, Object> objectObjectCache) {
        Tuple t = new Tuple(value.toString());
        processTuple(key.toString(), t);
    }

    @Override
    public void removed(Object o, Object o2, Cache<Object, Object> objectObjectCache) {

    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public void setConfiguration(Configuration configuration) {

    }

}
