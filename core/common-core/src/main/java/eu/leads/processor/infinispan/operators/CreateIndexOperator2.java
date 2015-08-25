package eu.leads.processor.infinispan.operators;

/**
 * Created by vagvaz on 10/26/14.
 */

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.apache.tajo.algebra.CreateIndex;
import org.apache.tajo.algebra.JsonHelper;
import org.apache.tajo.algebra.Projection;
import org.apache.tajo.algebra.Relation;
import org.infinispan.Cache;
import org.infinispan.ensemble.EnsembleCacheManager;
import org.vertx.java.core.json.JsonObject;


public class CreateIndexOperator2 extends BasicOperator {

  transient protected EnsembleCacheManager emanager;
  String tableName;

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
