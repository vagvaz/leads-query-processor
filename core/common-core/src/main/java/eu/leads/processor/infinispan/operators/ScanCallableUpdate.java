package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.infinispan.AcceptAllFilter;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.math.FilterOperatorTree;
import eu.leads.processor.plugins.pagerank.node.DSPMNode;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
import org.infinispan.versioning.VersionedCache;
import org.infinispan.versioning.impl.VersionedCacheTreeMapImpl;
import org.infinispan.versioning.utils.version.Version;
import org.infinispan.versioning.utils.version.VersionScalar;
import org.infinispan.versioning.utils.version.VersionScalarGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by vagvaz on 2/20/15.
 */
public class ScanCallableUpdate<K,V> extends LeadsSQLCallable<K,V> {

  transient protected VersionedCache versionedCache;

  transient  protected Cache pageRankCache;
  transient protected FilterOperatorTree tree;
  transient protected double totalSum;
  transient protected Cache approxSumCache;
  protected String qualString;
  transient  boolean versioning;
  boolean onVersionedCache;

//  transient protected InfinispanManager manager;
  protected Logger log = LoggerFactory.getLogger(ScanCallableUpdate.class.toString());

  public ScanCallableUpdate(String configString, String output) {
    super(configString, output);
  }
  public ScanCallableUpdate(String configString, String output,boolean onVersionedCache) {
    super(configString, output);
    this.onVersionedCache = onVersionedCache;
  }

  @Override public void initialize() {
    super.initialize();
    versionedCache = new VersionedCacheTreeMapImpl(inputCache,new VersionScalarGenerator(),inputCache.getName());

    pageRankCache = (Cache) imanager.getPersisentCache("pagerankCache");
    log.info("--------------------    get approxSum cache ------------------------");
    approxSumCache = (Cache) imanager.getPersisentCache("approx_sum_cache");
    totalSum = -1f;

    if(conf.getObject("body").containsField("qual"))
    {
      tree = new FilterOperatorTree(conf.getObject("body").getObject("qual"));
    }
    else{
      tree =null;
    }

    versioning = getVersionPredicate(conf);
  }

  /**
   * This method shoul read the Versions if any , from the configuration of the Scan operator and return true
   * if there are specific versions required, false otherwise
   * @param conf the configuration of the operator
   * @return returns true if there is a query on specific versions false otherwise
   */
  private boolean getVersionPredicate(JsonObject conf) {
    return false;
  }

  @Override public void executeOn(K key, V ivalue) {
    //         System.err.println(manager.getCacheManager().getAddress().toString() + " "+ entry.getKey() + "       " + entry.getValue());
    Tuple toRunValue = null;
    if(onVersionedCache){
      String versionedKey = (String) key;
      String ikey = pruneVersion(versionedKey);
      Version currentVersion = getVersion(versionedKey);
      if(versioning){
        if(isInVersionRange(currentVersion)){
          toRunValue = (Tuple) ivalue;
        }
      }
      else{
        Version latestVersion = versionedCache.getLatestVersion(ikey);
        if (latestVersion == null) {
          return;
        }
        Object objectValue = versionedCache.get(ikey);
        toRunValue = (Tuple) objectValue;
//        toRunValue = (String) objectValue;
      }
    }
    else{
      toRunValue = (Tuple)ivalue;
    }
//    if(versioning) {
//      String versionedKey = (String) key;
//      String ikey = pruneVersion(versionedKey);
//      Version latestVersion = versionedCache.getLatestVersion(ikey);
//      if (latestVersion == null) {
//        continue;
//      }
//      Version currentVersion = getVersion(versionedKey);
//      Object objectValue = versionedCache.get(ikey);
//      String value = (String) objectValue;
//    }
//    else {

//      String ikey = (String) key;
//      String value = (String) inputCache.get(ikey);
      //ENDNONVERSIONDING
      //         String value = (String) entry.getValue();

      //          String value = (String) entry.getValue();
      //          String value = (String)inputCache.get(key);
//      Tuple tuple = new Tuple(toRunValue);
      Tuple tuple = new Tuple(toRunValue);
      namesToLowerCase(tuple);
      renameAllTupleAttributes(tuple);
      if (tree != null) {
        if (tree.accept(tuple)) {
          tuple = prepareOutput(tuple);
          //               log.info("--------------------    put into output with filter ------------------------");
          if (key != null && tuple != null)
            outputCache.put(key.toString(), tuple);
        }
      } else {
        tuple = prepareOutput(tuple);
        //            log.info("--------------------    put into output without tree ------------------------");
        if (key != null && tuple != null){
          outputToCache(key,tuple);
      }
    }
  }



  /**
   *
   * @param currentVersion the version of the tuple currently processed by the operator
   * @return true if it satisfies the version range defined in the operator false otherwise
   */
  private boolean isInVersionRange(Version currentVersion) {
    //SAMPLE CODE NOT NECESSARILY exactly like that
    //if(currentVersion >= minVersion && <= maxVersion)
//      return true;
//    else
//      return false;
    return false;
  }

  private Version getVersion(String versionedKey) {
    Version result = null;
    String stringVersion = versionedKey.substring(versionedKey.lastIndexOf(":") + 1);
    result = new VersionScalar(Long.parseLong(stringVersion));
    return result;
  }

  private String pruneVersion(String versionedKey) {
    String result = versionedKey.substring(0,versionedKey.lastIndexOf(":"));
    return result;
  }


  private void namesToLowerCase(Tuple tuple) {
    Set<String> fieldNames  =  new HashSet<>(tuple.getFieldNames());
    for(String field : fieldNames){
      tuple.renameAttribute(field,field.toLowerCase());
    }
  }

  private void renameAllTupleAttributes(Tuple tuple) {
    JsonArray fields = inputSchema.getArray("fields");
    Iterator<Object> iterator = fields.iterator();
    String columnName = null;
    while(iterator.hasNext()){
      JsonObject tmp = (JsonObject) iterator.next();
      columnName = tmp.getString("name");
      int lastPeriod = columnName.lastIndexOf(".");
      String attributeName = columnName.substring(lastPeriod+1);
      tuple.renameAttribute(attributeName,columnName);
    }

    handlePagerank(columnName.substring(0,columnName.lastIndexOf(".")),tuple);
  }

  protected void handlePagerank(String substring, Tuple t) {
    if(conf.getObject("body").getObject("tableDesc").getString("tableName").equals("default.webpages")){
      if(totalSum < 0){
        computeTotalSum();
      }
      String url = t.getAttribute("default.webpages.url");
      DSPMNode currentPagerank = (DSPMNode) pageRankCache.get(url);
      if(currentPagerank == null || totalSum <= 0)
      {
        t.setAttribute("default.webpages.pagerank",0f);
        return;
      }
      //            t.setNumberAttribute("default.webpages.pagerank",0.032342);
      t.setNumberAttribute("default.webpages.pagerank",currentPagerank.getVisitCount()/totalSum);

      //READ PAGERANK FROM PAGERANK CACHE;
      //READ TOTAL ONCE
      //compute value update it to tuple



      //      if (t.hasField("default.webpages.pagerank")) {
      //         if (!t.hasField("url"))
      //            return;
      //         String pagerankStr = t.getAttribute("pagerank");
      //            Double d = Double.parseDouble(pagerankStr);
      //            if (d < 0.0) {
      //
      //                try {
      ////                    d = LeadsPrGraph.getPageDistr(t.getAttribute("url"));
      //                    d = (double) LeadsPrGraph.getPageVisitCount(t.getAttribute("url"));
      //
      //                } catch (IOException e) {
      //                    e.printStackTrace();
      //                }
      //                t.setAttribute("pagerank", d.toString());
      //        }
    }
  }

  private void computeTotalSum() {
    log.info("--------------------   Creating iterable over approx sum entries ------------------------");
    CloseableIterable<Map.Entry<String, Integer>> iterable =
      approxSumCache.getAdvancedCache().filterEntries(new AcceptAllFilter());
    log.info("--------------------    Iterating over approx sum entries cache ------------------------");
    for (Map.Entry<String, Integer> outerEntry : iterable) {
      totalSum += outerEntry.getValue() ;
    }
    if(totalSum > 0){
      totalSum+=1;
    }
  }
}