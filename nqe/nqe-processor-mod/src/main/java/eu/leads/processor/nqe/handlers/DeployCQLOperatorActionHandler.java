package eu.leads.processor.nqe.handlers;


import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.WebUtils;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.LeadsNodeType;
import eu.leads.processor.core.plan.PlanNode;
import eu.leads.processor.core.plan.SQLPlan;
import eu.leads.processor.deployer.ExecutionPlanMonitor;
import eu.leads.processor.infinispan.continuous.BasicContinuousOperatorListener;
import eu.leads.processor.infinispan.operators.*;
import eu.leads.processor.nqe.NQEConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vagvaz on 9/23/15.
 */
public class DeployCQLOperatorActionHandler implements ActionHandler {
  private Map<String,ExecutionPlanMonitor> cqlQueries;
  private Map<String,List<JsonObject>> continuousConfigs;
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
    continuousConfigs = new HashMap<>();
    this.logg = logg;
    log = LoggerFactory.getLogger(DeployCQLOperatorActionHandler.class);
  }

  @Override public Action process(Action action) {
    Action result = new Action(action);
    result.setStatus(ActionStatus.COMPLETED.toString());
    if(action.getLabel().equals(NQEConstants.DEPLOY_CQL_OPERATOR)) {
      SQLPlan sqlPlan = new SQLPlan(action.getData().getObject("plan"));
      System.err.println("CQL Query arrived " + sqlPlan.getQueryId()+"\n");

      log.error("CQL Query arrived " + sqlPlan.getQueryId());
      ExecutionPlanMonitor plan  =new ExecutionPlanMonitor(sqlPlan);
      cqlQueries.put(plan.getQueryId(),plan);
      List<JsonObject> jsonArray = new ArrayList<>();
      continuousConfigs.put(plan.getQueryId(),jsonArray);
      PlanNode limitNode = plan.getNodeByType(LeadsNodeType.LIMIT);
      PlanNode sortNode = plan.getNodeByType(LeadsNodeType.SORT);
      JsonObject limitConf = null;
      JsonObject sortConf = null;
      if(limitNode != null){
        limitConf = limitNode.getConfiguration();
      }
      if(sortConf != null){
        sortConf = sortNode.getConfiguration();
      }
      //Find sources
      List<PlanNode> sources = plan.getSources();
//      if(sources.get)
      List<PlanNode> nodes = new ArrayList<>(plan.getPlan().getNodes().size());

      if(sources.size() > 1){
        PrintUtilities.printAndLog(log, "Join in Continuous Queries is not supported");
        cqlQueries.remove(sqlPlan.getQueryId());
      }else{
        PlanNode scanOp = sources.get(0);
        int timeWindow = scanOp.getConfiguration().getObject("body").getNumber("range").intValue();

        PlanNode current = scanOp;
        while(current.getNodeType().equals(LeadsNodeType.ROOT) || current.getNodeType().equals(LeadsNodeType.OUTPUT_NODE)){
          nodes.add(current);
          current = plan.getNextOperator(scanOp);
        }

        for (int index = 0; index < nodes.size(); index++) {
          PlanNode node = nodes.get(index);
          JsonObject continuousConf =  new JsonObject();
          continuousConf.putString("listener", BasicContinuousOperatorListener.class.getCanonicalName().toString());
          continuousConf.putString("operatorClass", getOperatorClass(node));
          JsonObject listenerConf = new JsonObject();
          listenerConf.putObject("operator", new JsonObject());
          listenerConf.getObject("operator").putObject("configuration", node.getConfiguration().copy());
          if(node.getNodeType().equals(LeadsNodeType.SORT)){
            listenerConf.getObject("operator").getObject("configuration").putObject("limit",limitConf);
            listenerConf.getObject("operator").putString("isReduce", "false");
          }
          else if(node.getNodeType().equals(LeadsNodeType.LIMIT)){
            listenerConf.getObject("operator").getObject("configuration").putObject("sort",sortConf);
            listenerConf.getObject("operator").putString("isReduce", "true");
          } else{
            listenerConf.getObject("operator").putString("isReduce", "false");
          }

          listenerConf.getObject("operator").putString("isLocal", "false");
          listenerConf.putString("operatorClass", getContinuousListenerClass(node));
          continuousConf.putObject("conf", listenerConf);
          String ensembleString = computeEnsembleHost(node,globalConfig);
          String inputListener = getInput(node,nodes,plan);
          String outputListener = getOuput(node,nodes,plan);
          String window = "timeBased";
          int windowSize = timeWindow;//LQPConfiguration.getInstance().getConfiguration().getInt("node.continuous.windowSize", 1000);
          int parallelism = 1;//LQPConfiguration.getInstance().getConfiguration().getInt("node.engine.parallelism", 4);
          continuousConf.putString("cache", inputListener);
          continuousConf.getObject("conf").putString("window", window);
          continuousConf.getObject("conf").putNumber("windowSize", windowSize);
          continuousConf.getObject("conf").putNumber("parallelism", parallelism);
          continuousConf.getObject("conf").putString("input", inputListener);
          continuousConf.getObject("conf").putString("ensembleHost", ensembleString);
          continuousConf.getObject("conf").getObject("operator").putString("ensembleString", ensembleString);
          continuousConf.getObject("conf").getObject("operator").putString("output", outputListener);
          continuousConf.getObject("conf").getObject("operator").putNumber("parallelism", parallelism);
          jsonArray.add(continuousConf);
          WebUtils.addListener(inputListener, getContinuousListenerClass(node), continuousConf, globalConfig);
        }

      }
    }
    else if(action.getLabel().equals(NQEConstants.STOP_CQL)){
      System.err.println("Stop CQL--- " + action.getData().getString("queryId"));
      List<JsonObject> configs = continuousConfigs.get(action.getData().getString("queryId"));
      for(JsonObject config : configs){
        WebUtils.stopCache(config.getObject("conf").getObject("operator").getString("output"),globalConfig);
      }
      cqlQueries.remove(action.getData().getString("queryId"));
    }
    else{
      System.err.println("UNKNOWN action label arrived in DeployCQLOperator " + action.getLabel());
    }
    return result;
  }

  private String getOuput(PlanNode node, List<PlanNode> nodes, ExecutionPlanMonitor plan) {
    String result = plan.getQueryId();
    PlanNode next =  plan.getNextOperator(node);
    if(node.getNodeType().equals(LeadsNodeType.OUTPUT_NODE) || node.getNodeType().equals(LeadsNodeType.ROOT)){
      plan.getQueryId();
    }
    return result;
  }

  private String getInput(PlanNode node, List<PlanNode> nodes, ExecutionPlanMonitor plan) {
    if(node.getNodeType().equals(LeadsNodeType.SCAN)){
      return node.getConfiguration().getObject("body").getObject("tableDesc").getString("tableName");
    }
    else{
      return node.getInputs().get(0);
    }
  }

  private String computeEnsembleHost(PlanNode node, JsonObject globalConfig) {
    return null;
  }

  private String getContinuousListenerClass(PlanNode node) {
    return getOperatorClass(node);
  }

  private String getOperatorClass(PlanNode planNode) {
    switch (planNode.getNodeType()) {
      case PROJECTION:
        return ProjectOperator.class.getCanonicalName().toString();
      case LIMIT:
        return LimitOperator.class.getCanonicalName().toString();
      case SORT:
        return SortOperator.class.getCanonicalName().toString();
      case HAVING:
        return FilterOperator.class.getCanonicalName().toString();
      case GROUP_BY:
        return GroupByOperator.class.getCanonicalName().toString();
      case SELECTION:
        return FilterOperator.class.getCanonicalName().toString();
      case JOIN:
        return ScanOperator.class.getCanonicalName().toString();
      case SCAN:
        return ScanOperator.class.getCanonicalName().toString();

    }
    PrintUtilities.printAndLog(log,"UNKNOWN continuous type " + planNode.getNodeType().toString());
    return "";
  }
}
