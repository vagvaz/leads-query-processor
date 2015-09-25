package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.index.LeadsIndex;
import eu.leads.processor.core.index.LeadsIndexHelper;
import eu.leads.processor.math.FilterOperatorTree;
import org.apache.tajo.algebra.*;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by vagvaz on 2/20/15.
 */
public class CreateIndexCallable<K, V> extends LeadsSQLCallable<K, V> implements Serializable {
  static final long serialVersionUID = 42L;
  transient protected FilterOperatorTree tree;

  protected Logger log = LoggerFactory.getLogger(CreateIndexCallable.class.toString());

  transient private String tableName;
  transient String IndexName;
  transient ArrayList<String> columnNames;
  transient ArrayList<String> fullColumnNames;
  transient Logger profilerLog;
  private ProfileEvent fullProcessing;
  transient ArrayList<Cache> indexCaches;
  transient int printStackTraceCnt=0;
  transient ArrayList<DistCMSketch> sketches;
  transient LeadsIndexHelper lindHelp ;
  int counter = 0;
  public CreateIndexCallable(String configString, String output) {
    super(configString, output);
  }

  @Override
  public void initialize() {
    super.initialize();
    profilerLog = LoggerFactory.getLogger("###PROF###" + this.getClass().toString());
    System.out.println("Emanager has " + emanager.sites().size() + " sites");
    String CreateIndexJ = conf.getString("rawquery");

    CreateIndex newExpr = JsonHelper.fromJson(CreateIndexJ, CreateIndex.class);

    IndexName = newExpr.getIndexName();
    if (IndexName.isEmpty())
      IndexName = "noname"+UUID.randomUUID();

    tableName = (((Relation) ((Projection) newExpr.getChild()).getChild())).getName();
    Sort.SortSpec[] collumns = newExpr.getSortSpecs();

    columnNames = new ArrayList<>();
    for (Sort.SortSpec sc : collumns)
      columnNames.add(((ColumnReferenceExpr) sc.getKey()).getName());

    fullColumnNames = new ArrayList<>();

    System.out.println(" TableName: " + tableName);
    tableName = StringConstants.DEFAULT_DATABASE_NAME + "." + tableName;
    for(String s : columnNames){
      fullColumnNames.add(tableName+"."+s);
    }
    System.out.println(" TableName: " + tableName);

    System.out.println(" IndexName: " + IndexName);
    System.out.println(" columns found: " + columnNames.toString());

    //fix IndexName
    Cache<String, String> allIndexes = (Cache) imanager.getPersisentCache("allIndexes");
    for (String column : columnNames) {
      System.out.println("Saving Index name: " + IndexName +" for cache " + tableName + "." + column);
      allIndexes.put(IndexName, tableName + "." + column);
    }

    indexCaches = new ArrayList<>();
    sketches = new ArrayList<>();
    for (int c = 0; c < columnNames.size(); c++) {
      System.out.println("Creating Index Caches, column " + tableName + "." + columnNames.get(c));
      indexCaches.add((Cache) imanager.getIndexedPersistentCache(tableName + "." + columnNames.get(c)));
      System.out.println("Creating DistCMSketch " + tableName + "." + columnNames.get(c) + ".sketch");
      sketches.add(new DistCMSketch(null,false));
    }
    inputCache = (Cache) imanager.getPersisentCache(tableName);

    fullProcessing = new ProfileEvent("Full Processing", profilerLog);
    lindHelp = new LeadsIndexHelper();
    System.out.println("Init finished ");
  }


  /**
   * This method shoul read the Versions if any , from the configuration of the Scan operator and return true
   * if there are specific versions required, false otherwise
   *
   * @param conf the configuration of the operator
   * @return returns true if there is a query on specific versions false otherwise
   */
  private boolean getVersionPredicate(JsonObject conf) {
    return false;
  }

  @Override
  public void executeOn(K key, V ivalue) {
    counter++;
    if(counter % 10000 == 0){
      Thread.yield();
      try {
        log.error("sleeping after " + counter);
        Thread.sleep(200);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    //ProfileEvent createIndexExecute = new ProfileEvent("CreateIndexExecute", profilerLog);
    String ikey = (String) key;
    Tuple value = (Tuple) ivalue;
    try {
      if (value == null) {
        log.error("key: " + key + " value null");
        return;
      }
      for (int c = 0; c < columnNames.size(); c++) {
        String column = fullColumnNames.get(c);  // tableName + '.' + columnNames.get(c);
        LeadsIndex lInd = lindHelp.CreateLeadsIndex(value.getGenericAttribute(column), ikey, column, tableName);
        //putToCacheDirect(indexCaches.get(c), ikey, lInd);
        indexCaches.get(c).getAdvancedCache().withFlags(Flag.CACHE_MODE_LOCAL,Flag.IGNORE_RETURN_VALUES).put(ikey, lInd);
        sketches.get(c).add(value.getGenericAttribute(column));
      }
    }catch (Exception e){
      System.err.println(" Ex " + key + " " + e.toString());
      if((printStackTraceCnt++%1000)==0) {
        System.err.println("StackTraces "+printStackTraceCnt);
        e.printStackTrace();
      }
    }

    //createIndexExecute.end();
  }

  @Override
  public void finalizeCallable() {
    try {
      Cache<String,Object> sketchesM=  (Cache)imanager.getPersisentCache("sketchMerge");
      for (int c = 0; c < columnNames.size(); c++) {
        sketches.get(c).storeAsObject(sketchesM,  embeddedCacheManager.getAddress().toString()+":"+columnNames.get(c)+":");
      }
      fullProcessing.end();
      super.finalizeCallable();
    }catch (Exception e){
      System.err.println(" Ex "+ e.toString());
      if((printStackTraceCnt++%1000)==0) {
        System.err.println("StackTraces "+printStackTraceCnt);
        e.printStackTrace();
      }
    }
  }

}
