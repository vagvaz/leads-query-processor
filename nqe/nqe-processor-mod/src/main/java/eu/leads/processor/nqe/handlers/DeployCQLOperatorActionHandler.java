package eu.leads.processor.nqe.handlers;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.SQLPlan;
import eu.leads.processor.nqe.NQEConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vagvaz on 9/23/15.
 */
public class DeployCQLOperatorActionHandler implements ActionHandler {
  private Map<String,SQLPlan> cqlQueries;
  private Node com;
  private Logger log;
  private InfinispanManager imanager;
  private JsonObject globalConfig;
  private String id;
  private LogProxy logg;

  public DeployCQLOperatorActionHandler(Node com, LogProxy logg, InfinispanManager persistence, String id,
      JsonObject globalConfig) {
    this.com = com;
    this.imanager = persistence;
    this.id = id;
    this.globalConfig = globalConfig;
    cqlQueries = new HashMap<>();
    this.logg = logg;
    log = LoggerFactory.getLogger(DeployCQLOperatorActionHandler.class);
  }

  @Override public Action process(Action action) {
    Action result = new Action(action);
    result.setStatus(ActionStatus.COMPLETED.toString());
    if(action.getLabel().equals(NQEConstants.DEPLOY_CQL_OPERATOR)) {
      SQLPlan plan = new SQLPlan(action.getData().getObject("plan"));
      System.err.println("CQL Query arrived " + plan.getQueryId()+"\n");
      System.out.println("CQL Query arrived " + plan.getQueryId()+"\n");
      log.error("CQL Query arrived " + plan.getQueryId());
      cqlQueries.put(plan.getQueryId(),plan);
    }
    else if(action.getLabel().equals(NQEConstants.STOP_CQL)){
      System.err.println("Stop CQL--- " + action.getData().getString("queryId"));
      cqlQueries.remove(action.getData().getString("queryId"));
    }
    else{
      System.err.println("UNKNOWN action label arrived in DeployCQLOperator " + action.getLabel());
    }
    return result;
  }
}
