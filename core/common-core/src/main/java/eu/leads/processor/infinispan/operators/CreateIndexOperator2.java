package eu.leads.processor.infinispan.operators;

/**
 * Created by vagvaz on 10/26/14.
 */

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.index.LeadsIndex;
import eu.leads.processor.core.index.LeadsIndexHelper;
import eu.leads.processor.core.net.Node;
import org.apache.tajo.algebra.*;
import org.infinispan.Cache;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.UUID;

import static eu.leads.processor.common.infinispan.EnsembleCacheUtils.putToCache;


public class CreateIndexOperator2 extends BasicOperator {


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


  public CreateIndexOperator2(Node com, InfinispanManager persistence, LogProxy log, Action action) {
    super(com, persistence, log, action);
    if(this.action.getData().getObject("operator").getString("id")==null){
      System.out.println("Create Index Action: " + this.action.toString());
      this.action.getData().getObject("operator").putString("id","Cindex");
    }
  }

  @Override
  public void init(JsonObject config) {


    String CreateIndexJ = conf.getString("rawquery");

    CreateIndex newExpr = JsonHelper.fromJson(CreateIndexJ, CreateIndex.class);

    tableName = (((Relation) ((Projection) newExpr.getChild()).getChild())).getName();

    inputCache = (Cache) manager.getPersisentCache(tableName);
  }

  @Override
  public void run() {
    LeadsIndexHelper lindHelp = new LeadsIndexHelper();
    int i = 0;
    if(tableName==null)
    {
      log.error("EROOORRRR tableName: " + tableName +" null");
      return;
    }
    System.out.println("inputCache Size : " + inputCache.getAdvancedCache());
    long timeStart = System.currentTimeMillis();
    int reportRate= 100000;
    for (Object key : inputCache.getAdvancedCache().keySet()) {
      try {


        String ikey = (String) key;
        Tuple value = (Tuple) inputCache.get(ikey);
        if (value == null) {
          log.error("key: " + key + " value null");
          continue;
        }
        for (int c = 0; c < columnNames.size(); c++) {
          String column = tableName + '.' + columnNames.get(c);
          LeadsIndex lInd = lindHelp.CreateLeadsIndex(value.getGenericAttribute(column), ikey, column, tableName);
          putToCache(indexCaches.get(c), ikey, lInd);
          //indexCaches.get(c).put(ikey, lInd);
          //sketches.get(c).add(value.getGenericAttribute(column));
          //if(i%10==0)
          //
           if(i%reportRate==0) {
             long time2 = System.currentTimeMillis();
             System.out.println(" Put " + i + " Time: " + (time2-timeStart)/1000.0 + " Rate t/s" + reportRate/((time2-timeStart)/1000.0) );
             timeStart=time2;
           }
        }
        i++;


      }catch (Exception e){
        System.err.println(" Exception " + i + " " + e.toString());
      }

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
     mapperCallable = new CreateIndexCallable<>(conf.toString(),getOutput());

  }

  @Override
  public void setupReduceCallable() {

  }

  @Override
  public boolean isSingleStage() {
    return true;
  }


}
