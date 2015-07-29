package eu.leads.processor.infinispan.operators;

/**
 * Created by vagvaz on 10/26/14.
 */

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.index.*;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.infinispan.LocalDataFilter;
import eu.leads.processor.math.MathUtils;
import org.apache.tajo.algebra.*;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
import org.infinispan.context.Flag;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.infinispan.versioning.utils.version.Version;
import org.infinispan.versioning.utils.version.VersionScalar;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.*;


public class CreateIndexOperator extends BasicOperator {


  transient protected EnsembleCacheManager emanager;
  transient protected EnsembleCache ecache;
  String IndexName;
  String tableName;
  ArrayList<String> columnNames;
  //JsonArray columnNames;
  private String ensembleHost;
  ArrayList<Cache> indexCaches;
  ArrayList<Cache> sketchCaches;
  ArrayList<DistCMSketch> sketches;


  public CreateIndexOperator(Node com, InfinispanManager persistence, LogProxy log, Action action) {
    super(com, persistence, log, action);
    if(this.action.getData().getObject("operator").getString("id")==null){
      System.out.println("Create Index Action: " + this.action.toString());
      this.action.getData().getObject("operator").putString("id","Cindex");
    }
  }

  @Override
  public void init(JsonObject config) {
    ensembleHost =  computeEnsembleHost();
    if (ensembleHost != null && !ensembleHost.equals("")) {
      emanager = new EnsembleCacheManager(ensembleHost);
      emanager.start();
    } else {
      LQPConfiguration.initialize();
      emanager = new EnsembleCacheManager(LQPConfiguration.getConf().getString("node.ip") + ":11222");
      emanager.start();
    }

    String CreateIndexJ = conf.getString("rawquery");

    CreateIndex newExpr = JsonHelper.fromJson(CreateIndexJ, CreateIndex.class);

    IndexName = newExpr.getIndexName();
    if (IndexName.isEmpty())
      IndexName = "noname"+UUID.randomUUID();

    tableName = (((Relation) ((Projection) newExpr.getChild()).getChild())).getName();
    Sort.SortSpec[] collumns = newExpr.getSortSpecs();

    columnNames = new ArrayList<>();//= conf.getObject("CreateIndex").getArray("SortSpecs");
    for (Sort.SortSpec sc : collumns)
      columnNames.add(((ColumnReferenceExpr) sc.getKey()).getName());

    System.out.println(" TableName: " + tableName);
    tableName = StringConstants.DEFAULT_DATABASE_NAME + "." + tableName;

    System.out.println(" TableName: " + tableName);

    System.out.println(" IndexName: " + IndexName);
    System.out.println(" columns found: " + columnNames.toString());

    //fix IndexName

    Cache<String, String> allIndexes = (Cache) manager.getPersisentCache("allIndexes");
    for (String column : columnNames)
      allIndexes.put(IndexName, tableName + "." + column);

    indexCaches = new ArrayList<>();
    sketchCaches = new ArrayList<>();

    for (int c = 0; c < columnNames.size(); c++) {
      if(!manager.getCacheManager().cacheExists(tableName + "." + columnNames.get(c))) {
        indexCaches.add((Cache) manager.getIndexedPersistentCache(tableName + "." + columnNames.get(c)));
        Cache tmp =(Cache) manager.getPersisentCache(tableName + "." + columnNames.get(c) + ".sketch");
        sketchCaches.add(tmp);
        sketches.add(new DistCMSketch(tmp,false));
        log.info("Creating Index Caches, column " + tableName + "." + columnNames.get(c));
      }else {

        log.info("Index Already exists on column ... but anyway reindexing" +tableName + "." + columnNames.get(c));
      }
    }

    //indexCaches
    //fix tablename
    inputCache = (Cache) manager.getPersisentCache(tableName);

    System.out.println(" output cache: " + getOutput() +  " Action " + action.asJsonObject().toString());
  }

  @Override
  public void run() {
    LeadsIndexHelper lindHelp = new LeadsIndexHelper();
    int i = 0;
    System.out.println("inputCache Size : " + inputCache.getAdvancedCache());
    for (Object key : inputCache.getAdvancedCache().keySet()) {

      String ikey = (String) key;
      Tuple value = (Tuple) inputCache.get(ikey);

      for (int c = 0; c < columnNames.size(); c++) {
        String column = tableName +'.' +columnNames.get(c);
        LeadsIndex lInd = lindHelp.CreateLeadsIndex(value.getGenericAttribute(column), ikey, column, tableName);
        indexCaches.get(c).put(ikey, lInd);
        sketches.get(c).add(value.getGenericAttribute(column));
      }
      i++;
    }
    log.info("Succesfully completed indexing records, columns:" + columnNames.size() + ", data per column:" + i);
   // EnsembleCacheUtils.waitForAllPuts();

    cleanup();
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
