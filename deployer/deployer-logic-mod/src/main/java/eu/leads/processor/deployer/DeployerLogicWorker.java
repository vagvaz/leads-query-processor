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
       if(com != null)
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
                        String queryJson = queriesCache.get(plan.getQueryId());
                       if(queryJson == null || queryJson.equals("")){
                          failQuery(plan.getQueryId(),"Could not read query from queries");
                       }
                        SQLQuery query = new SQLQuery(new JsonObject(queryJson));
                        query.getQueryStatus().setStatus(QueryState.RUNNING);
                        queriesCache.put(query.getId(),query.asJsonObject().toString());
                        startExecution(executionPlan);

                    } else if (label.equals(DeployerConstants.DEPLOY_CUSTOM_PLAN)) {

                        String queryType = action.getData().getString("specialQueryType");
                        SQLPlan plan = new SQLPlan(action.getData().getObject("plan"));
                       String queryJson = queriesCache.get(plan.getQueryId());
                       if(queryJson == null || queryJson.equals("")){
                          failQuery(plan.getQueryId(),"Could not read query from queries");
                       }
                        SQLQuery query = new SQLQuery(new JsonObject(queryJson));
                        query.getQueryStatus().setStatus(QueryState.RUNNING);
                        queriesCache.put(query.getId(),query.asJsonObject().toString());
                        ExecutionPlanMonitor executionPlan = new ExecutionPlanMonitor(plan);
                        executionPlan.setAction(action);
                        executionPlan.setSpecial(true);
                        runningPlans.put(plan.getQueryId(), executionPlan);
                        List<PlanNode> source = executionPlan.getSources();
                      if(source.size() > 1){
                        log.error("SPECIAL PLAN " + action.toString() + "\n has more than on sources " + source.size());
                      }
                      PlanNode scan = source.get(0);
                      String input = scan.getInputs().get(0);
                      executionPlan.complete(scan);
                      PlanNode specialNode = executionPlan.getNextExecutableOperator(scan);
                      if(queryType.equals("rec_call"))
                      {
                        specialNode.setNodeType(LeadsNodeType.WGS_URL);
                        specialNode.getConfiguration().putString("type", LeadsNodeType.WGS_URL.toString());

                      }
                      else if(queryType.equals("ppq_call")){
                        specialNode.setNodeType(LeadsNodeType.EPQ);
                        specialNode.getConfiguration().putString("type",LeadsNodeType.EPQ.toString());
                      }
                      specialNode.getConfiguration().putString("realOutput",executionPlan.getQueryId());
                      specialNode.getInputs().set(0,input);
                      deployOperator(executionPlan,specialNode);
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
                    log.error("Unknown INPROCESS Action received " + action.toString());
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
                   try{
                    if (label.equals(NQEConstants.OPERATOR_COMPLETE) || label.equals(NQEConstants.DEPLOY_OPERATOR)) {

                        PlanNode node = new PlanNode(action.getData().getObject("operator"));
                        String queryId = action.getData().getString("queryId");
                        ExecutionPlanMonitor plan = runningPlans.get(queryId);
                        plan.complete(node);
                        PlanNode next = plan.getNextOperator(node);
                       log.info("Deployer " +  node.getNodeType() + " in action that  " + label + " has completed" );
                        boolean useNode = true;
                        if (next.getNodeType().equals(LeadsNodeType.ROOT)) {

                            if(!next.getConfiguration().containsField("mapreduce")) {
                               plan.complete(next);
                               next = plan.getNextExecutableOperator(next);
                               useNode = false;
                               ;
                               log.info("next is root  without MR");
                            }
                            else{
                               useNode = true;
                               log.info("next is root  MR");
                            }
                        }
                        if (next.getNodeType().equals(LeadsNodeType.OUTPUT_NODE)) {
                           log.info("next is output");
                            plan.complete(next);
                            next = plan.getNextExecutableOperator(next);
                            useNode = false;
                        }
                        log.info("Continue to deployement");
                        PlanNode tobeDeployed = null;
                        if (useNode) {
                           log.info("using node for the deployment of next operator");
                           tobeDeployed = plan.getNextExecutableOperator(node);
                        }
                        else {
                           log.info("next is used");
                            if (next != null) {
                               tobeDeployed = plan.getNextExecutableOperator(next);
                               log.info("To be deployed");
                            }
                            else{
                               log.info("Todeployed is null");
                                tobeDeployed = null;
                            }
                        }


//                      if(tobeDeployed.getNodeType().equals(LeadsNodeType.ROOT))
//                       {
//                          plan.complete(tobeDeployed);
//                          tobeDeployed = plan.getNextExecutableOperator(tobeDeployed);
//                       }
//                       if(tobeDeployed.getNodeType().equals(LeadsNodeType.OUTPUT_NODE)){
//                          plan.complete(tobeDeployed);
//                          tobeDeployed = plan.getNextExecutableOperator(tobeDeployed);
//                       }

                        if ( tobeDeployed == null && plan.isFullyExecuted()){
                            finalizeQuery(plan.getQueryId());
                        }
                        else{
                           deployOperator(plan, tobeDeployed);
                        }
                    } else if (label.equals(NQEConstants.OPERATOR_FAILED)) {
                        PlanNode node = new PlanNode(action.getData().getObject("operator"));
                        String queryId = action.getData().getString("queryId");
                        ExecutionPlanMonitor plan = runningPlans.get(queryId);
                        plan.fail(node);
                        newAction = createNewAction(action);
                        newAction.getData().putObject("operator", node.asJsonObject());
                        newAction.getData().putObject("plan", plan.getLogicalPlan().asJsonObject());
                        com.sendTo(recoveryAddress, newAction.asJsonObject());
                    } else {
                        log.error("Unknown COMPLETED Action received " + action.toString());
                        return;
                    }
                    action.setStatus(ActionStatus.INPROCESS.toString());
                    if (newAction != null) {
                        action.addChildAction(newAction.getId());
                        logAction(newAction);
                    }

                    finalizeAction(action);
                   }catch(Exception e){
                      log.error("Unexpected error encounted in DeployLogicWorker " + e.getClass().toString() + " " + e.getMessage());
                   }
            }
        }
    }

   private void failQuery(String queryId, String s) {

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
       String outputCacheName = plan.getCacheName();
      if(!plan.isSpecial()) {
        query.asJsonObject().putString("output", outputCacheName);
      }
      else{
        query.asJsonObject().putString("output",query.getId());
      }
       query.asJsonObject().putBoolean("isSorted",plan.isSorted());
      log.info("Query " + query.getId() + " completed");
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
       log.info("Deploying operator " + next.getNodeType().toString() + " to micro - cloud" + next.getSite());
                       deployAction.getData().putString("monitor", monitorAddress);
      deployAction.getData().putObject("operator",next.asJsonObject());
      deployAction.getData().putString("operatorType",next.getNodeType().toString());
      deployAction.getData().putString("queryId",executionPlan.getQueryId());
     deployAction.setLabel(NQEConstants.DEPLOY_OPERATOR);
     com.sendTo(nqeGroup, deployAction.asJsonObject());
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
        result.setData(new JsonObject());
        result.setResult(new JsonObject());
        result.setLabel("");
        result.setCategory("");
        return result;
    }
}
