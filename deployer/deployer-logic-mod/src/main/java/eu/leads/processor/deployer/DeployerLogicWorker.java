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
import eu.leads.processor.imanager.IManagerConstants;
import eu.leads.processor.infinispan.operators.DistCMSketch;
import eu.leads.processor.math.FilterOperatorNode;
import eu.leads.processor.math.FilterOperatorTree;
import eu.leads.processor.math.MathUtils;
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
         if(action.getLabel().equals(IManagerConstants.QUIT)) {
            System.out.println(" Quit Dep ");
            persistence.stopManager();
            log.error("Stopped Manager Exiting");
            try {
               Thread.sleep(10);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            System.exit(0);
         }


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
                     if(next != null && next.getNodeType().equals(LeadsNodeType.JOIN)){
                        if(node.getConfiguration().containsField("buildBloomFilter")){
                           List<String> inputs = next.getInputs();
                           String otherScanNode = "";
                           for(String input : inputs){
                              if(!input.equals(node.getNodeId())){
                                 otherScanNode = input;
                              }
                           }
                           List<PlanNode> sources = plan.getSources();
                           for(PlanNode s : sources){ // hijcak deployment and run 2nd run
                              if(s.getNodeId().equals(otherScanNode)){
                                 next = s;
                                 deployOperator(plan,next);
                              }
                           }
                        }
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
      log.error(
          "Deploying operator " + mrOperator.getNodeType().toString() + " to micro - cloud" + mrOperator.getSite());
      deployAction.getData().putString("monitor", monitorAddress);
      deployAction.getData().putObject("operator",mrOperator.asJsonObject());
      deployAction.getData().putString("operatorType", mrOperator.getNodeType().toString());
      deployAction.getData().putString("queryId", operator.getString("queryId"));
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
      if (!executionPlan.shouldRunMapReduceFirst()) {
         List<PlanNode> sources = executionPlan.getSources();
         if (sources.size() == 1) {
            for (PlanNode source : sources) {
               if (source != null) {
                  deployOperator(executionPlan, source);
               }
            }
         } else if (sources.size() == 2 && (sources.get(0).getNodeType().toString().equals(LeadsNodeType.SCAN.toString())) && (sources.get(1).getNodeType().toString().equals(LeadsNodeType.SCAN.toString()))) {
            PlanNode node1 = sources.get(0);
            PlanNode node2 = sources.get(1);
            JsonObject bloomFilter = new JsonObject();
            bloomFilter.putString("bloomCache", node1.getOutput() + ".bloom");

            if (executionPlan.getNextOperator(node1).getNodeType().toString().equals(LeadsNodeType.JOIN.toString())) {
               if (hasPredicate(node1) && hasPredicate(node2)) {
                  if(hasIndexedPredicate(node1) && hasIndexedPredicate(node2) || !(hasIndexedPredicate(node1) && hasIndexedPredicate(node2)) ){
                     Long node1Size = getSize(node1);
                     Long node2Size = getSize(node2);
                     if(node1Size < node2Size){
                        //Choose node2
                        bloomFilter.putNumber("bloomSize",getSize(node2));
                        node2.getConfiguration().getObject("next").getObject("configuration").putObject("buildBloom",
                            bloomFilter);
                        node2.getConfiguration().putString("buildBloomFilter", "buildBloomFilter");
                        executionPlan.updateNode(node2);
                        deployOperator(executionPlan, node2);
                     }
                     else{
                        //choose node1
                        bloomFilter.putNumber("bloomSize",getSize(node1));
                        node1.getConfiguration().getObject("next").getObject("configuration").putObject("buildBloom",
                            bloomFilter);
                        node1.getConfiguration().putString("buildBloomFilter", "buildBloomFilter");
                        executionPlan.updateNode(node1);
                        deployOperator(executionPlan, node1);
                     }
                  }
                  else if(hasIndexedPredicate(node1)){
                     //choose node1
                     bloomFilter.putNumber("bloomSize",getSize(node1));
                     node1.getConfiguration().getObject("next").getObject("configuration").putObject("buildBloom",
                         bloomFilter);
                     node1.getConfiguration().putString("buildBloomFilter", "buildBloomFilter");
                     executionPlan.updateNode(node1);
                     deployOperator(executionPlan,node1);
                  }
                  else if(hasIndexedPredicate(node2)) {
                     //choose node2
                     bloomFilter.putNumber("bloomSize", getSize(node2));
                     node2.getConfiguration().getObject("next").getObject("configuration")
                         .putObject("buildBloom", bloomFilter);
                     node2.getConfiguration().putString("buildBloomFilter", "buildBloomFilter");
                     executionPlan.updateNode(node2);
                     deployOperator(executionPlan, node2);
                  }
               } else if (hasPredicate(node1)) {
                  //choose node 1
                  bloomFilter.putNumber("bloomSize",getSize(node1));
                  node1.getConfiguration().getObject("next").getObject("configuration").putObject("buildBloom",
                      bloomFilter);
                  node1.getConfiguration().putString("buildBloomFilter", "buildBloomFilter");
                  executionPlan.updateNode(node1);
                  deployOperator(executionPlan,node1);
               }else if (hasPredicate(node2)) {
                  //choose node2
                  bloomFilter.putNumber("bloomSize",getSize(node2));
                  node2.getConfiguration().getObject("next").getObject("configuration").putObject("buildBloom",
                      bloomFilter);
                  node2.getConfiguration().putString("buildBloomFilter","buildBloomFilter");
                  executionPlan.updateNode(node2);
                  deployOperator(executionPlan,node2);
               } else {
                  for (PlanNode source : sources) {
                     if (source != null) {
                        deployOperator(executionPlan, source);
                     }
                  }

               }
            }
         } else {
            PlanNode mapreduceNode = executionPlan.getMROperator();
            deployOperator(executionPlan, mapreduceNode);

         }
      }
   }

   private Long getSize(PlanNode node) {
      String tableName = node.getConfiguration().getObject("body").getObject("tableDesc").getString("tableName");
      if(LQPConfiguration.getInstance().getConfiguration().containsKey(tableName+".size")){
         return LQPConfiguration.getInstance().getConfiguration().getLong(tableName+".size",5000000L);
      }
      else{
         return 5000000L;
      }
   }

   private boolean hasIndexedPredicate(PlanNode node) {
      return checkIndex_usage(node);
   }

   private boolean checkIndex_usage(PlanNode node) {
      JsonObject conf = node.getConfiguration();
      if (conf.getObject("body").containsField("qual")) {
         System.out.println("Scan Check if fields are indexed.");
         JsonObject inputSchema;
         inputSchema = conf.getObject("body").getObject("inputSchema");
         JsonArray fields = inputSchema.getArray("fields");
         System.out.println("Check if fields: " + fields.toArray().toString() + " are indexed.");

         Iterator<Object> iterator = fields.iterator();
         String columnName = null;
         HashMap indexCaches = new HashMap<>();
         HashMap sketches = new HashMap<>();
         while (iterator.hasNext()) {
            JsonObject tmp = (JsonObject) iterator.next();
            columnName = tmp.getString("name");
            //System.out.print("Check if exists: " +  columnName + " ");
            if (persistence.getCacheManager().cacheExists(columnName)) {
               indexCaches.put(columnName, (Cache) persistence.getIndexedPersistentCache(columnName));
               System.out.print(columnName + " exists! ");
            }

            if (persistence.getCacheManager().cacheExists(columnName + ".sketch")) {
               sketches.put(columnName, new DistCMSketch((Cache) persistence.getPersisentCache(columnName + ".sketch"), true));
               System.out.println(" exists!");
            }
         }

         if (indexCaches.size() == 0) {
            System.out.println("Nothing Indexed");
            return false;
         } else {
            System.out.print("At least some fields are Indexed: ");
            for (Object s : indexCaches.keySet())
               System.out.println((String) s);
         }
         long start = System.currentTimeMillis();

         FilterOperatorTree tree = new FilterOperatorTree(conf.getObject("body").getObject("qual"));
         Object selectvt = getSelectivity(sketches, tree.getRoot());
         System.out.println("  selectvt CMS " + selectvt + "  computation time: " + (System.currentTimeMillis() - start) / 1000.0);
         long inputSize;
         if (selectvt != null) {
            start = System.currentTimeMillis();
            System.out.println("Get size of table " + columnName.substring(0, columnName.lastIndexOf(".")));
            Cache<String, Long> sizeC = (Cache) persistence.getPersisentCache("TablesSize");
            if (sizeC.containsKey(columnName.substring(0, columnName.lastIndexOf("."))))
               inputSize = sizeC.get(columnName.substring(0, columnName.lastIndexOf(".")));
            else {
               System.out.print("Size not found, Slow Get size() ");
               inputSize = getSize(node);
               System.out.println("... Caching size value.");
               sizeC.put(columnName.substring(0, columnName.lastIndexOf(".")),inputSize);
            }
            System.out.println(" Found size: " + inputSize);

            double selectivity = (double) selectvt / (double) inputSize;
            System.out.println("Scan  Selectivity: " + selectivity);
            System.out.println("  Selectivity, inputSize " + inputSize + "  computation time: " + (System.currentTimeMillis() - start) / 1000.0);

            if (selectivity < 0.5) {
               System.out.println("Scan Use indexes!! ");
               return indexCaches.size() > 0;
            }
         } else
            System.out.println("No Selectivity!!");

      } else
         System.out.println("No Qual!!");

      System.out.println("Don't Use indexes!! ");
      return false;
   }

   Object getSelectivity(HashMap<String, DistCMSketch> sketchCaches, FilterOperatorNode root) {
      if (root == null)
         return null;
      Object left = getSelectivity(sketchCaches, root.getLeft());
      Object right = getSelectivity(sketchCaches, root.getRight());

      switch (root.getType()) {
         case AND:
            if (left != null && right != null)
               return Math.min((double) left, (double) right);
            break;
         case OR:
            if (left != null && right != null)
               return (double) left + (double) right;
            break;
         default:
            System.out.println("SubQual " + root.getType());
            return getSubSelectivity(sketchCaches, root);
      }
      return (left != null) ? left : right;
   }


   Object getSubSelectivity(HashMap<String, DistCMSketch> sketchCaches, FilterOperatorNode root) {
      Object result = null;
      double dleft = -1000;
      double dright = -1000;
      String sleft = null;
      String sright = null;
      if (root == null)
         return null;
      Object oleft = getSubSelectivity(sketchCaches, root.getLeft());
      Object oright = getSubSelectivity(sketchCaches, root.getRight());

      if (oleft instanceof Double)
         dleft = (double) oleft;
      if (oright instanceof Double)
         dright = (double) oright;
      if (oleft instanceof String)
         sleft = (String) oleft;
      if (oright instanceof String)
         sright = (String) oright;

      switch (root.getType()) {
         case EQUAL:
            if (sleft != null && oright != null) {
               String collumnName = sleft;
               return sketchCaches.get(collumnName).get(oright);
            }
            break;
         case FIELD:
            String collumnName = root.getValueAsJson().getObject("body").getObject("column").getString("name");
            //String type = root.getValueAsJson().getObject("body").getObject("column").getObject("dataType").getString("type");


            if (sketchCaches.containsKey(collumnName)) {
               //if (type.equals("TEXT"))
               return collumnName;

            }
            return null;
         //break;

         case CONST:
            JsonObject datum = root.getValueAsJson().getObject("body").getObject("datum");
            String type = datum.getObject("body").getString("type");
            Number ret=0;// = MathUtils.getTextFrom(root.getValueAsJson());
            //System.out.println("Operator Found datum: " + datum.toString());

            try {
               if (type.equals("TEXT"))
                  return  MathUtils.getTextFrom(root.getValueAsJson());
               else {
                  Number a = datum.getObject("body").getNumber("val");
                  if (a != null)
                     return a;
               }
            } catch (Exception e) {
               System.err.print("Error " + ret + " to type " + type +"" + e.getMessage());
            }
            return null;
         case LTH:
            return 0.4;

         ////        if(left !=null && oright !=null)
         ////          return left.and().having("attributeValue").lt(oright);//,right.getValueAsJson());
         //        return null;
         //        break;
         case LEQ:
            return 0.4;
         //        if(left !=null && oright !=null)
         //          return left.and().having("attributeValue").lte(oright);//,right.getValueAsJson());
         //        break;
         case GTH:
            return 0.4;
         //        if(left !=null && oright !=null)
         //          return left.and().having("attributeValue").gt(oright);//,right.getValueAsJson());
         //        break;
         case GEQ:
            return 0.4;
         //        if(left !=null && oright !=null)
         //          return left.and().having("attributeValue").gte(oright);//,right.getValueAsJson());
         //        break;
         //
         //      case LIKE:
         //        if(left !=null && oright !=null) {
         //          return left.and().having("attributeValue").like((String) oright);//,right.getValueAsJson());
         //        }break;
         //
         //
         //      case ROW_CONSTANT:
         //        //TODO
         //        break;
         default:
            return 0.01;
      }
      return null;
   }

   private boolean hasPredicate(PlanNode node) {
      return node.getConfiguration().getObject("body").containsField("qual");
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
