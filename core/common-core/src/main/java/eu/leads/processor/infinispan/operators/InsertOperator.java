package eu.leads.processor.infinispan.operators;

/**
 * Created by vagvaz on 10/26/14.
 */

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.index.LeadsIndex;
import eu.leads.processor.core.index.LeadsIndexHelper;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.math.MathUtils;
import org.infinispan.Cache;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.versioning.utils.version.Version;
import org.infinispan.versioning.utils.version.VersionScalar;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.*;

//import org.infinispan.versioning.VersionedCache;
//import org.infinispan.versioning.impl.VersionedCacheTreeMapImpl;
//import org.infinispan.versioning.utils.version.Version;
//import org.infinispan.versioning.utils.version.VersionScalar;
//import org.infinispan.versioning.utils.version.VersionScalarGenerator;


public class InsertOperator extends BasicOperator {


  private ArrayList<Cache> indexCaches=null;


  public InsertOperator(Node com, InfinispanManager persistence, LogProxy log, Action action) {
    super(com, persistence, log, action);
  }

  Tuple data;
  String key = "";
  String tableName;
  Version version = null;
  transient protected EnsembleCacheManager emanager;
  private String ensembleHost;
  transient protected EnsembleCache ecache;

  @Override
  public void init(JsonObject config) {
//                            super.init(config);
    EnsembleCacheUtils.initialize();
    ensembleHost = computeEnsembleHost();
    if (ensembleHost != null && !ensembleHost.equals("")) {
      emanager = new EnsembleCacheManager(ensembleHost);
      emanager.start();
//      emanager = createRemoteCacheManager();
    } else {
      LQPConfiguration.initialize();
      emanager = new EnsembleCacheManager(LQPConfiguration.getConf().getString("node.ip") + ":11222");
//            emanager = createRemoteCacheManager();
      emanager.start();
    }
    data = new Tuple();
    JsonArray columnNames = conf.getObject("body").getArray("columnNames");
    JsonArray values = conf.getObject("body").getArray("exprs");
    JsonArray primaryArray = conf.getObject("body").getArray("primaryColumns");
    Set<String> primaryColumns = new HashSet<String>(primaryArray.toList());
    Iterator<Object> columnIterator = columnNames.iterator();
    Iterator<Object> valuesIterator = values.iterator();
    if (values.size() != columnNames.size()) {
      log.error("INSERT problem different size between values and columnNames");
    }
    tableName = conf.getObject("body").getString("tableName");
    key = tableName + ":";
    while (columnIterator.hasNext() && valuesIterator.hasNext()) {
      String column = (String) columnIterator.next();
      JsonObject jsonValue = (JsonObject) valuesIterator.next();
      Object value = MathUtils.getValueFrom(jsonValue);
      if (column.equalsIgnoreCase("version")) {
        Object ob = MathUtils.getValueFrom(jsonValue);
        if (ob instanceof String) {
          SimpleDateFormat df = new SimpleDateFormat();
//                                    try {
//                                            version = new VersionScalar(df.parse((String) ob).getTime());
//                                        } catch (ParseException e) {
//                                            e.printStackTrace();
//                                        }
//                                }else if(ob instanceof Long){
//                                 version  = new VersionScalar ((Long)ob);
//                                }
//                        }
          if (primaryColumns.contains(column)) {
            key = key + "," + value.toString();
          }
//                                data.putValue(column, value);

            }

         }
         if (primaryColumns.contains(column)) {
            if(value != null)
            {
               key = key + "," + value.toString();
            }
            else{
               key = key +",null";
            }
         }
         data.setAttribute(tableName+"."+column,value);
      }

  }

  @Override
  public void run() {
//      targetCache = (Cache) manager.getPersisentCache(tableName);
    ecache = emanager.getCache(tableName, new ArrayList<>(emanager.sites()),
            EnsembleCacheManager.Consistency.DIST);
//                VersionedCache versionedCache = new VersionedCacheTreeMapImpl(targetCache, new VersionScalarGenerator(),targetCache.getName());

    if (version == null) {
      version = new VersionScalar(System.currentTimeMillis());
    }

//      long size = targetCache.size();
    log.info("inserting into " + ecache.getName() + " "

            + key + "     \n" + data.toString());
    EnsembleCacheUtils.putToCache(ecache, key, data);
//    if(checkIndex_usage())
//    for (String column : data.getFieldNames()) {
//      LeadsIndexHelper lindHelp = new LeadsIndexHelper();
//      Cache indexCache = (Cache) manager.getPersisentCache(tableName + "." + column);
//      int i = indexCache.size() + 1;
//      if (indexCache != null) {
//        LeadsIndex lInd = lindHelp.CreateLeadsIndex(data.getGenericAttribute(column), key, column, tableName);
//        indexCache.put(key, lInd);
//      }
//    }

//                targetCache.put(key,data.toString());
//                        versionedCache.put(key,data.toString(),version);
    if (ecache.get(key) == null) {
      log.error("Insert Failed " + ecache.size());
    }
    EnsembleCacheUtils.waitForAllPuts();
    cleanup();
  }

  private boolean checkIndex_usage() {
 
    String columnName = null;
    indexCaches = new ArrayList<>();
    JsonArray columnNames = conf.getObject("body").getArray("columnNames");
    Iterator<Object> iterator = columnNames.iterator();
    while (iterator.hasNext()) {
      JsonObject tmp = (JsonObject) iterator.next();
      columnName = tmp.getString("name");
      indexCaches.add((Cache) manager.getIndexedPersistentCache(tableName + "." +columnName));
    }

    return indexCaches.size()>0;
  }

  @Override
  public void cleanup() {
    super.cleanup();
  }

  @Override
  public void createCaches(boolean isRemote, boolean executeOnlyMap, boolean executeOnlyReduce) {

  }

  @Override
  public void setupMapCallable() {

  }

  @Override
  public void setupReduceCallable() {

  }

  @Override
  public boolean isSingleStage() {
    return true;
  }


}
