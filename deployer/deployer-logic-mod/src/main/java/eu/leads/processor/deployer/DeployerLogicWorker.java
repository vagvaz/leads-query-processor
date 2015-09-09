package eu.leads.processor.deployer;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanClusterSingleton;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.ConfigurationUtilities;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.*;
import eu.leads.processor.nqe.NQEConstants;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.*;

/**
 * Created by vagvaz on 8/27/14.
 */
public class DeployerLogicWorker extends Verticle implements LeadsMessageHandler {
   private final String componentType = "deployer";
   private JsonObject config;
   private String deployerGroup;
   private String monitorAddress;
   private String recoveryAddress;
   private String nqeGroup;
   private String imanagerQueue;
   private Set<String> ignoredOperators;
   private Map<String, ExecutionPlanMonitor> runningPlans;
   private Logger log;
   private Node com;
   private String id;
   private String workQueueAddress;
   private InfinispanManager persistence;
   private Cache<String,String> queriesCache;
//   private Map<String,JsonObject> remoteOperators;
//   private Map<String,Set<String>> pendingOperators;
//   private Map<String,String> microclouds;
   private String localMicroCloud;
   private JsonObject globalConfig;


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
      LQPConfiguration.getInstance().getConfiguration().setProperty("node.current.component", "deployer");
      globalConfig = config.getObject("global");
      String publicIP = ConfigurationUtilities
          .getPublicIPFromGlobal(LQPConfiguration.getInstance().getMicroClusterName(), globalConfig);
      LQPConfiguration.getInstance().getConfiguration().setProperty(StringConstants.PUBLIC_IP,publicIP);
      localMicroCloud = LQPConfiguration.getInstance().getMicroClusterName();
      persistence = InfinispanClusterSingleton.getInstance().getManager();
      queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
      id = config.getString("id");
      com = new DefaultNode();
      com.initialize(id, deployerGroup, null, this, null, vertx);
//      log = new LogProxy(config.getString("log"), com);
      log = LoggerFactory.getLogger(id);
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
                  if (queryJson == null || queryJson.equals("")) {
                     failQuery(plan.getQueryId(), "Could not read query from queries");
                  }
                  SQLQuery query = new SQLQuery(new JsonObject(queryJson));
                  query.getQueryStatus().setStatus(QueryState.RUNNING);
                  queriesCache.put(query.getId(), query.asJsonObject().toString());
                  startExecution(executionPlan);
               }
//               } else if (label.equals(DeployerConstants.DEPLOY_SINGLE_MR)){
//                  JsonObject operator = action.getData();
//                  remoteOperators.put(operator.getObject("MROperator").getString("id"), operator);
//                  deployRemoteOperator(action,operator,new PlanNode(operator.getObject("MROperator")));
//               }
               else if (label.equals(DeployerConstants.DEPLOY_CUSTOM_PLAN)) {

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
                     log.error("Deployer " + node.getNodeType() + " in action that  " + label
                         + " has completed");
                     boolean useNode = true;
                     if (next.getNodeType().equals(LeadsNodeType.ROOT)) {

                        if(!next.getConfiguration().containsField("mapreduce")) {
                           plan.complete(next);
                           next = plan.getNextExecutableOperator(next);
                           useNode = false;
                           ;
                           log.error("next is root  without MR");
                        }
                        else{

                           if(!next.getConfiguration().getObject("mapreduce").containsField("after")) {
                              useNode = true;
                              log.error("next is root  MR");
                           }
                           else{
                              plan.complete(next);
                              next = plan.getNextExecutableOperator(next);
                              useNode = false;
                              log.error("next is root without MR");
                           }
                        }
                     }
                     if(next.getNodeType().equals(LeadsNodeType.SCAN)){

                        log.error("First MR executed continuing to the rest of the plan ... ");
                        log.error("First MR executed continuing to the rest of the plan ... ");
                        List<PlanNode> sources = plan.getSources();
                        for (PlanNode source : sources) {
                           if (source != null) {
                              deployOperator(plan, source);
                           }
                        }
                        return;
                     }
                     if (next.getNodeType().equals(LeadsNodeType.OUTPUT_NODE)) {
                        log.error("next is output");
                        plan.complete(next);
                        next = plan.getNextExecutableOperator(next);
                        useNode = false;
                     }

                     log.error("Continue to deployement");
                     PlanNode tobeDeployed = null;
                     if (useNode) {
                        log.error("using node for the deployment of next operator");
                        tobeDeployed = plan.getNextExecutableOperator(node);
                     }
                     else {
                        log.error("next is used");
                        if (next != null) {
                           tobeDeployed = plan.getNextExecutableOperator(next);
                           log.error("To be depl oyed");
                        }
                        else{
                           log.error("Todeployed is null");
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

   private void deployRemoteOperator(Action action,JsonObject operator, PlanNode mrOperator) {
      Action deployAction = createNewAction(action);
      log.error("Deploying operator " + mrOperator.getNodeType().toString() + " to micro - cloud"
          + mrOperator.getSite());
      deployAction.getData().putString("monitor", monitorAddress);
      deployAction.getData().putObject("operator",mrOperator.asJsonObject());
      deployAction.getData().putString("operatorType",mrOperator.getNodeType().toString());
      deployAction.getData().putString("queryId",operator.getString("queryId"));
      deployAction.setLabel(NQEConstants.DEPLOY_OPERATOR);
      com.sendTo(nqeGroup, deployAction.asJsonObject());
      com.sendTo(monitorAddress,deployAction.asJsonObject());
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
      log.error("Query " + query.getId() + " completed");
      queriesCache.put(queryId,query.asJsonObject().toString());
      Collection<PlanNode> nodes = ((Plan)plan.getLogicalPlan()).getNodes();
      for(PlanNode n : nodes){
         if(!n.getNodeId().equals(outputCacheName)){
            System.err.println("Clearing... " + n.getNodeId());
            persistence.removePersistentCache(n.getNodeId());
         }
      }
      //LATER TODO we could inform Interface Manager about the query completion to inform UIs
   }

   private void startExecution(ExecutionPlanMonitor executionPlan) {
      if(!executionPlan.shouldRunMapReduceFirst()) {
         List<PlanNode> sources = executionPlan.getSources();
         for (PlanNode source : sources) {
            if (source != null) {
               deployOperator(executionPlan, source);
            }
         }
      }
      else
      {
         PlanNode mapreduceNode = executionPlan.getMROperator();
         deployOperator(executionPlan,mapreduceNode);

      }
   }

   private void deployOperator(ExecutionPlanMonitor executionPlan, PlanNode next) {
      Action deployAction = createNewAction(executionPlan.getAction());
      log.error("Deploying operator " + next.getNodeType().toString() + " to micro - cloud" + next
          .getSite());
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
