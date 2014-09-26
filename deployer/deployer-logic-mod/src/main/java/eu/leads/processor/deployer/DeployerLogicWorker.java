package eu.leads.processor.deployer;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.*;
import eu.leads.processor.nqe.NQEConstants;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.*;

/**
 * Created by vagvaz on 8/27/14.
 */
public class DeployerLogicWorker extends Verticle implements LeadsMessageHandler {
    private final String componentType = "deployer";
    JsonObject config;
    String deployerGroup;
    String monitorAddress;
    String recoveryAddress;
    String nqeGroup;
    String imanagerQueue;
    Set<String> ignoredOperators;
    Map<String, ExecutionPlanMonitor> runningPlans;
    LogProxy log;
    Node com;
    String id;
    String workQueueAddress;
    InfinispanManager persistence;
    Cache<String,String> queriesCache;
    @Override
    public void start() {
        super.start();
        config = container.config();
        deployerGroup = config.getString("deployer");
        monitorAddress = config.getString("monitor");
        recoveryAddress = config.getString("recovery");
        nqeGroup = config.getString("nqe");
        imanagerQueue = config.getString("imanager");
        ignoredOperators = new HashSet<String>();
        workQueueAddress = config.getString("workqueue");
        runningPlans = new HashMap<String, ExecutionPlanMonitor>();
        LQPConfiguration.initialize();
        persistence = InfinispanClusterSingleton.getInstance().getManager();
        queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
        id = config.getString("id");
        com = new DefaultNode();
        com.initialize(id, deployerGroup, null, this, null, vertx);
        log = new LogProxy(config.getString("log"), com);

    }

    @Override
    public void stop() {
        super.stop();
        com.unsubscribeFromAll();
    }

    @Override
    public void handle(JsonObject msg) {
        String type = msg.getString("type");
        String from = msg.getString(MessageUtils.FROM);
        String to = msg.getString(MessageUtils.TO);
        if (type.equals("action")) {
            Action action = new Action(msg);
            String label = action.getLabel();
            Action newAction = null;
            action.setProcessedBy(id);
            //         action.setStatus(ActionStatus.INPROCESS.toString());

            switch (ActionStatus.valueOf(action.getStatus())) {
                case PENDING: //probably received an action from an external source
                    if (label.equals(DeployerConstants.DEPLOY_SQL_PLAN)) {
                        SQLPlan plan = new SQLPlan(action.getData().getObject("plan"));
                        ExecutionPlanMonitor executionPlan = new ExecutionPlanMonitor(plan);
                        executionPlan.setAction(action);
                        runningPlans.put(plan.getQueryId(), executionPlan);
                        startExecution(executionPlan);

                    } else if (label.equals(DeployerConstants.DEPLOY_CUSTOM_PLAN)) {
                        SQLPlan plan = new SQLPlan(action.getData().getObject("plan"));
                        ExecutionPlanMonitor executionPlan = new ExecutionPlanMonitor(plan);
                        runningPlans.put(plan.getQueryId(), executionPlan);
                        startExecution(executionPlan);
                    }
                    //               else if (label.equals(DeployerConstants.OPERATOR_COMPLETED)) {
                    //
                    //               }
                    //               else if (label.equals(DeployerConstants.OPERATOR_FAILED)) {
                    //
                    //               }
                    else {
                        log.error("Unknown PENDING Action received " + action.toString());
                        return;
                    }
                    action.setStatus(ActionStatus.INPROCESS.toString());
                    if (newAction != null) {
                        action.addChildAction(newAction.getId());
                        logAction(newAction);
                    }
                    logAction(action);
                    break;
                case INPROCESS: //  probably received an action from internal source (processors)
                    //               if (label.equals(DeployerConstants.DEPLOY_SQL_PLAN)) {
                    //
                    //               } else if (label.equals(DeployerConstants.DEPLOY_CUSTOM_PLAN)) {
                    //
                    //               }
                    //               else if (label.equals(DeployerConstants.OPERATOR_COMPLETED)) {
                    //
                    //               }
                    //               else if (label.equals(DeployerConstants.OPERATOR_FAILED)) {
                    //
                    //               }
                    //               else {
                    log.error("Unknown PENDING Action received " + action.toString());
                    //                  return;
                    //               }
                    //               action.setStatus(ActionStatus.INPROCESS.toString());
                    //               if (newAction != null) {
                    //                  action.addChildAction(newAction.getId());
                    //                  logAction(newAction);
                    //               }
                    //               logAction(action);
                    break;
                case COMPLETED: // the action either a part of a multistep workflow (INPROCESSING) or it could be processed.
                    //               if (label.equals(DeployerConstants.DEPLOY_SQL_PLAN)) {
                    //
                    //               } else if (label.equals(DeployerConstants.DEPLOY_CUSTOM_PLAN)) {
                    //
                    //               }
                    //               else
                    if (label.equals(DeployerConstants.OPERATOR_COMPLETED)) {
                        PlanNode node = new PlanNode(action.getData().getObject("operator"));
                        String queryId = action.getData().getString("queryId");
                        ExecutionPlanMonitor plan = runningPlans.get(queryId);
                        plan.complete(node);
                        PlanNode tobeDeployed = plan.getNextOperator(node);
                       if(tobeDeployed.getNodeType().equals(LeadsNodeType.OUTPUT_NODE)){
                          plan.complete(tobeDeployed);
                          tobeDeployed = plan.getNextOperator(tobeDeployed);
                       }

                        if ( tobeDeployed == null && plan.isFullyExecuted()){
                            finalizeQuery(plan.getQueryId());
                        }
                        else{
                           deployOperator(plan,tobeDeployed);
                        }
                    } else if (label.equals(DeployerConstants.OPERATOR_FAILED)) {
                        PlanNode node = new PlanNode(action.getData().getObject("operator"));
                        String queryId = action.getData().getString("queryId");
                        ExecutionPlanMonitor plan = runningPlans.get(queryId);
                        plan.fail(node);
                        newAction = createNewAction(action);
                        newAction.getData().putObject("operator", node.asJsonObject());
                        newAction.getData().putObject("plan", plan.getLogicalPlan().asJsonObject());
                        com.sendTo(recoveryAddress, newAction.asJsonObject());
                    } else {
                        log.error("Unknown PENDING Action received " + action.toString());
                        return;
                    }
                    action.setStatus(ActionStatus.INPROCESS.toString());
                    if (newAction != null) {
                        action.addChildAction(newAction.getId());
                        logAction(newAction);
                    }

                    finalizeAction(action);
            }
        }
    }

    private void finalizeQuery(String queryId) {
      String queryDoc = queriesCache.get(queryId);
      if(queryDoc == null || queryDoc.equals("")){
         //error
      }
      JsonObject queryJson = new JsonObject(queryDoc);
       SQLQuery query = new SQLQuery(queryJson);
       ExecutionPlanMonitor plan = runningPlans.get(queryId);
       query.setPlan((Plan) plan.getLogicalPlan());
       query.getQueryStatus().setStatus(QueryState.COMPLETED);
       queriesCache.put(queryId,query.asJsonObject().toString());
       //LATER TODO we could inform Interface Manager about the query completion to inform UIs
    }

    private void startExecution(ExecutionPlanMonitor executionPlan) {
       List<PlanNode> sources = executionPlan.getSources();
       for(PlanNode source : sources){
          if(source != null){
             deployOperator(executionPlan,source);
          }
       }
    }

   private void deployOperator(ExecutionPlanMonitor executionPlan, PlanNode next) {
     Action deployAction = createNewAction(executionPlan.getAction());
      deployAction.getData().putString("monitor",monitorAddress);
      deployAction.getData().putObject("operator",next.asJsonObject());
      deployAction.getData().putString("operatorType",next.getNodeType().toString());
     deployAction.setLabel(NQEConstants.DEPLOY_OPERATOR);
     com.sendTo(nqeGroup, deployAction.asJsonObject());
     deployAction.setLabel(DeployerConstants.OPERATOR_STARTED);
     com.sendTo(monitorAddress,deployAction.asJsonObject());
   }

   private void finalizeAction(Action action) {
        //TODO
        //1 inform monitor about completion (if it is completed in this logic each action requires 1 step processing
        //2 remove from processing if necessary
        //3 update action to persistence service
    }


    private void logAction(Action action) {
        //TODO
        //1 inform monitor about action.
        //2 add action to processing set
        //3 update action to persistence service
    }

    private Action createNewAction(Action action) {
        Action result = new Action();
        result.setId(UUID.randomUUID().toString());
        result.setTriggered(action.getId());
        result.setComponentType(componentType);
        result.setStatus(ActionStatus.PENDING.toString());
        result.setTriggers(new JsonArray());
        result.setOwnerId(this.id);
        result.setProcessedBy("");
        result.setDestination("");
        result.setData(null);
        result.setResult(null);
        result.setLabel("");
        result.setCategory("");
        return result;
    }
}
