package eu.leads.processor.infinispan.operators;

/**
 * Created by vagvaz on 10/26/14.
 */

import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.utils.PrintUtilities;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;

//import org.infinispan.versioning.VersionedCache;
//import org.infinispan.versioning.impl.VersionedCacheTreeMapImpl;
//import org.infinispan.versioning.utils.version.Version;
//import org.infinispan.versioning.utils.version.VersionScalar;
//import org.infinispan.versioning.utils.version.VersionScalarGenerator;


public class InsertOperator extends BasicOperator {


  private static int indexedSize = 0;
  transient protected EnsembleCacheManager emanager;
  transient protected EnsembleCache ecache;
  Tuple data;
  String key = "";
  String tableName;
  Version version = null;
  private ArrayList<Cache> indexCaches = null;
  private String ensembleHost;

  public InsertOperator(Node com, InfinispanManager persistence, LogProxy log, Action action) {
    super(com, persistence, log, action);
  }

  @Override public void init(JsonObject config) {
    //                            super.init(config);
    EnsembleCacheUtils.initialize();
    ensembleHost = computeEnsembleHost();
    if (ensembleHost != null && !ensembleHost.equals("")) {
      emanager = new EnsembleCacheManager(ensembleHost);
      emanager.start();
    } else {
      LQPConfiguration.initialize();
      emanager = new EnsembleCacheManager(LQPConfiguration.getConf().getString("node.ip") + ":11222");
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
        if (value != null) {
          key = key + "," + value.toString();
        } else {
          key = key + ",null";
        }
      }
      data.setAttribute(tableName + "." + column, value);
    }
    System.out.println(" output cache: " + getOutput() + " Action " + action.asJsonObject().toString());
  }

  @Override public void run() {
    //      targetCache = (Cache) manager.getPersisentCache(tableName);
    ecache = emanager.getCache(tableName, new ArrayList<>(emanager.sites()), EnsembleCacheManager.Consistency.DIST);

    if (version == null) {
      version = new VersionScalar(System.currentTimeMillis());
    }
    boolean inserted = false;

    //      long size = targetCache.size();
    log.info("inserting into " + ecache.getName() + " " + key + "     \n" + data.toString());
    EnsembleCacheUtils.putToCacheDirect(ecache, key, data);
    if (checkIndex_usage())
      for (String column : data.getFieldNames()) {
        LeadsIndexHelper lindHelp = new LeadsIndexHelper();

        if (manager.getCacheManager().cacheExists(tableName + "." + column)) {
          Cache indexCache = (Cache) manager.getPersisentCache(tableName + "." + column);
          LeadsIndex lInd = lindHelp.CreateLeadsIndex(data.getGenericAttribute(column), key, column, tableName);
          indexCache.put(key, lInd);

          if (manager.getCacheManager().cacheExists(tableName + "." + column + ".sketch")) {
            Cache sketchCache = (Cache) manager.getPersisentCache(tableName + "." + column + ".sketch");
            DistCMSketch sk = new DistCMSketch(sketchCache, true);
            sk.add(data.getGenericAttribute(column));
            inserted = true;
          }
        }
      }
    if (inserted) {
      if ((indexedSize++ % 20) == 0) {
        //Update size
        Cache<String, Long> TableSizeCache = (Cache) manager.getPersisentCache("TablesSize");
        TableSizeCache.put(tableName, TableSizeCache.get(tableName));
      }
    }

    try {
      EnsembleCacheUtils.waitForAllPuts();
    } catch (InterruptedException e) {
      e.printStackTrace();
      PrintUtilities.logStackTrace(log, e.getStackTrace());
    } catch (ExecutionException e) {
      e.printStackTrace();
      PrintUtilities.logStackTrace(log, e.getStackTrace());
    }
    try {
      if (ecache.get(key) == null) {
        log.error("Insert Failed " + ecache.size());
      }
    } catch (java.lang.IllegalAccessError e) {
      log.error("Insert Failed ??? " + ecache.size() + "\n exception: " + e.getMessage() + "\n ex:" + e.toString());
    }

    cleanup();
  }

  private boolean checkIndex_usage() {

    String columnName = null;
    indexCaches = new ArrayList<>();
    JsonArray columnNames = conf.getObject("body").getArray("columnNames");
    Iterator<Object> iterator = columnNames.iterator();
    while (iterator.hasNext()) {
      columnName = (String) iterator.next();

      if (manager.getCacheManager().cacheExists(tableName + "." + columnName))
        indexCaches.add((Cache) manager.getIndexedPersistentCache(tableName + "." + columnName));
    }

    return indexCaches.size() > 0;
  }

  @Override public void cleanup() {
    super.cleanup();
  }

  @Override public void createCaches(boolean isRemote, boolean executeOnlyMap, boolean executeOnlyReduce) {

  }

  @Override public String getContinuousListenerClass() {
    return null;
  }

  @Override public void setupMapCallable() {

  }

  @Override public void setupReduceCallable() {

  }

  @Override public boolean isSingleStage() {
    return true;
  }


}
