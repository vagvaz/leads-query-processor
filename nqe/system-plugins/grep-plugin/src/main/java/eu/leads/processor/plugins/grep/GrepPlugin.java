package eu.leads.processor.plugins.grep;

import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.plugins.PluginInterface;
import org.apache.commons.configuration.Configuration;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GrepPlugin implements PluginInterface {
  private String id;
  private BasicCache grepCache;
  private EnsembleCache summaryMap;
  private List<String> products;
  private Logger log;
  private String globalEnsembleString;
  private String localEnsembleString;
  private EnsembleCacheManager globalManager;
  private EnsembleCacheManager countManager;
  private Map<String,Integer> countMap;
  private Map<String,String> urlMap;
  private Thread thread;
  private String targetCacheName;
  private Configuration configuration;
  private String installedCacheName = null;


  @Override
  public void initialize(Configuration configuration, InfinispanManager infinispanManager) {
    //read plugin configuration
    readConfiguration(configuration);
    //initialize necessary structures
    initStructures(infinispanManager);

  }

  @Override
  public void created(Object key, Object value, Cache<Object, Object> objectObjectCache) {
    Tuple t = new Tuple(value.toString());
    if(installedCacheName == null){
      installedCacheName = objectObjectCache.getName();
    }
    processTuple(key.toString(), t);
  }

  @Override
  public void modified(Object key, Object value, Cache<Object, Object> objectObjectCache) {
    Tuple t = new Tuple(value.toString());
    if(installedCacheName == null){
      installedCacheName = objectObjectCache.getName();
    }
    processTuple(key.toString(), t);
  }

  @Override
  public void removed(Object o, Object o2, Cache<Object, Object> objectObjectCache) {
    if(installedCacheName == null){
      installedCacheName = objectObjectCache.getName();
    }
  }

  protected void processTuple(String key, Tuple webpage) {
    log.error("grep plugin: " + key);
    //Plugin Logic
    //Check if webpage contains any of the product words and update counters
    for(String product : products) {

      //Filtering Logic
      //Check if product is present in the body of a webpage
      if (checkTupleAgainstProducts(webpage, product)) {

        //Processing Logic
        //update count for product and add webpage to output
        updateStructures(webpage,product);
      }
    }
  }

  /**
   * Filtering Logic
   * @param webpage containing the attributes of a webpage
   * @param product product/word name
   * @return true if the product name exist inside the body of the webpage false otherwise
   */
  private boolean checkTupleAgainstProducts(Tuple webpage, String product) {
    String body = null;
    if(webpage.getFieldNames().contains(installedCacheName+".body")){
      body = webpage.getAttribute(installedCacheName+".body");
    }else if(webpage.getFieldNames().contains(installedCacheName+".content")) {
      body = webpage.getAttribute(installedCacheName+".content");
    }
    if(body != null) {
      if (body.toLowerCase().contains(product.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Processing Logic
   * @param webpage webpage to output
   * @param product product name contained in the webpage
   */
  private void updateStructures(Tuple webpage, String product) {
    Integer count = countMap.get(product);
    if (count != null) {
      count++;
    } else {
      count = 1;
    }
    if(!urlMap.containsKey(webpage.getAttribute(installedCacheName+".url"))){
      urlMap.put(product,webpage.getAttribute(installedCacheName+".url"));
    }
    countMap.put(product, count);

    if (!grepCache.containsKey(targetCacheName+":"+webpage.getAttribute(installedCacheName+".url"))) {

      Set<String> set = new HashSet<>(webpage.getFieldSet());
      for(String field : set){
        if(field.contains(installedCacheName)){
          webpage.renameAttribute(field,field.replace(installedCacheName,targetCacheName));
        }
      }
      grepCache.put(targetCacheName+":"+webpage.getAttribute(targetCacheName+".url"), webpage);
    }
  }

  private void readConfiguration(Configuration configuration) {
    targetCacheName = configuration.getString("cache");
    products = configuration.getList("words");
    globalEnsembleString = configuration.getString("globalEnsembleString");
    localEnsembleString = configuration.getString("localEnsembleString");
  }


  private void initStructures(InfinispanManager infinispanManager) {
    log = LoggerFactory.getLogger(GrepPlugin.class);
    //initialize maps
    countMap = new HashMap<>();
    urlMap = new HashMap<>();

    //create local caches for remote puts
    infinispanManager.getPersisentCache(targetCacheName);
   infinispanManager.getPersisentCache(targetCacheName+".count");
    if(globalEnsembleString==null || localEnsembleString == null){
      PrintUtilities.printAndLog(log,"global: " + globalEnsembleString + " local " + localEnsembleString);
      return;
    } else{
      countManager = new EnsembleCacheManager(localEnsembleString);
      countManager.start();
      globalManager = new EnsembleCacheManager(globalEnsembleString);
      globalManager.start();
      grepCache = globalManager.getCache(targetCacheName, new ArrayList<>(globalManager.sites()),
          EnsembleCacheManager.Consistency.DIST);
      summaryMap = countManager.getCache(targetCacheName+".count", new ArrayList<>(countManager.sites()),
          EnsembleCacheManager.Consistency.DIST);
    }
    PrintUtilities.printAndLog(log,"2global: " + globalEnsembleString + " 2local " + localEnsembleString);
    //Create thread that periodically updates the caches from the local data
    thread = new Thread(new Runnable() {
      @Override public void run() {
        while(true){
          Map<String,Integer> tmp = countMap;
          countMap = new HashMap<String, Integer>();
          for (Map.Entry<String, Integer> entry : tmp.entrySet()) {
            updateValue(summaryMap, entry.getKey(), entry.getValue());
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
    thread.start();
  }


  public int updateValue(EnsembleCache cache,String valueName, Integer count){
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
      old = (Tuple) EnsembleCacheUtils.getFromCache(cache,valueName);
      if(old.getNumberAttribute("found").intValue() > count){
        isok = true;
      }
    }
    return 0;
  }
  @Override
  public void cleanup() {

  }

  @Override
  public Configuration getConfiguration() {
    return null;
  }

  @Override
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }
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
    return GrepPlugin.class.getCanonicalName();
  }
}
