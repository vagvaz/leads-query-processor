package eu.leads.processor.plugins.product;

import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.plugins.PluginInterface;
import org.apache.commons.configuration.Configuration;
import org.apache.lucene.analysis.util.CharArrayMap;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by vagvaz on 6/8/14.
 */
public class ProductPlugin  implements PluginInterface {
    private String id;
    private BasicCache targetCache;
    private BasicCache sumCache;
    private List<String> attributes;
    private Logger log = LoggerFactory.getLogger(ProductPlugin.class);
    private String globalEnsembleString;
    private String localEnsembleString;
    private EnsembleCacheManager globalManager;
    private EnsembleCacheManager countManager;
    private Logger logger = LoggerFactory.getLogger(this.getClassName());
    private Map<String,Integer> countMap;
    private Map<String,String> urlMap;
    private Thread timer;

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
        return ProductPlugin.class.getCanonicalName();
    }

    @Override
    public void initialize(Configuration configuration, InfinispanManager infinispanManager) {
        countMap = new HashMap<>();
        urlMap = new HashMap<>();
        String targetCacheName = configuration.getString("cache");
        attributes = configuration.getList("words");
        globalEnsembleString = configuration.getString("globalEnsembleString");
        localEnsembleString = configuration.getString("localEnsembleString");
        //*Create Caches*//
        targetCache = (BasicCache) infinispanManager.getPersisentCache(targetCacheName);
        sumCache = (BasicCache)infinispanManager.getPersisentCache(targetCacheName+".count");
        if(globalEnsembleString==null || localEnsembleString == null){
            PrintUtilities.printAndLog(log,"global: " + globalEnsembleString + " local " + localEnsembleString);
        } else{
            countManager = new EnsembleCacheManager(localEnsembleString);
            countManager.start();
            globalManager = new EnsembleCacheManager(globalEnsembleString);
            globalManager.start();
            targetCache = globalManager.getCache(targetCacheName, new ArrayList<>(globalManager.sites()),
                EnsembleCacheManager.Consistency.DIST);
            sumCache  = countManager.getCache(targetCacheName+".count", new ArrayList<>(countManager.sites()),
                EnsembleCacheManager.Consistency.DIST);
            Tuple t = new Tuple();
            //            t.setAttribute("total",0);
            //            sumCache.put("total",t);
        }
        timer = new Thread(new Runnable() {
            @Override public void run() {
                while(true){
                    //                    System.err.println("Updateing " + countMap.size());
                    Map<String,Integer> tmp = countMap;
                    countMap = new HashMap<String, Integer>();
                    for (Map.Entry<String, Integer> entry : tmp.entrySet()) {
                        updateValue(sumCache, entry.getKey(), entry.getValue());
                    }
                    tmp.clear();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timer.start();
        //        timer = new Timer(true);
        //        timer.scheduleAtFixedRate(new TimerTask() {
        //            @Override public void run() {
        //                System.err.println("Updateing " + countMap.size());
        //                Map tmp = countMap;
        //                countMap = new HashMap<String, Integer>();
        //                for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
        //                    updateValue(sumCache, entry.getKey(), entry.getValue());
        //                }
        //                tmp.clear();
        //            }
        //        }, 0, 2000);
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
        //        tuple.keepOnly(attributes);
        //        targetCache.put(key, tuple);
        log.error("grep plugin: " + key);
        int counter = 0;
        String body = null;
        if(tuple.getFieldNames().contains("default.webpages.body")){
            body = tuple.getAttribute("default.webpages.body");
        }else if(tuple.getFieldNames().contains("default.webpages.content")) {
            body = tuple.getAttribute("default.webpages.content");
        }
        if(body != null) {
            for (String w : attributes) {
                if (body.toLowerCase().contains(w.toLowerCase())) {
                    Integer count = countMap.get(w);
                    if (count != null) {
                        count++;
                    } else {
                        count = 1;
                    }
                    if(!urlMap.containsKey(tuple.getAttribute("default.webpages.url"))){
                        urlMap.put(w,tuple.getAttribute("default.webpages.url"));
                    }
                    countMap.put(w, count);
                    counter++;
                    if (counter == 1) {
                        targetCache.put(key, tuple);
                    }
                }
            }
            if (counter != 0) {
                Integer count = countMap.get("totalFiltered");
                if (count != null) {
                    count++;
                } else {
                    count = counter;
                }
            }
        }
    }

    public int updateValue(BasicCache cache,String valueName, Integer count){
        Tuple t  = new Tuple();
        Tuple old = (Tuple) cache.get(valueName);
        boolean isok = false;
        while(!isok) {
            t.setAttribute("word",valueName);
            t.setAttribute("sample_url",urlMap.get(valueName));
            if (old != null) {

                if(old.hasField("found")) {
                    t.setAttribute("found", count + ((Number) old.getGenericAttribute("found")).intValue());
                }
                else{
                    t.setAttribute("found",count);
                }
            } else{
                t.setAttribute("found",count);
            }
            cache.put(valueName,t);
            old = (Tuple) cache.get(valueName);
            if(old.getNumberAttribute("found").intValue() > count){
                isok = true;
            }
        }
        return 0;
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
