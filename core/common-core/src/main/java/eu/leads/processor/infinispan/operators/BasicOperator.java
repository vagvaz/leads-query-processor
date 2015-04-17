package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.utils.PrintUtilities;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.infinispan.LeadsBaseCallable;
import eu.leads.processor.web.ActionResult;
import eu.leads.processor.web.WebServiceClient;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.infinispan.Cache;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.distexec.DistributedTask;
import org.infinispan.distexec.DistributedTaskBuilder;
import org.vertx.java.core.json.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by tr on 30/8/2014.
 */
public abstract class BasicOperator extends Thread implements Operator{
   protected JsonObject conf;
   protected Action action;
   protected InfinispanManager manager;
   protected Node com;
   protected Cache statisticsCache;
   protected LogProxy log;
   protected JsonObject globalConfig;
   protected Set<String> pendingMMC;
   protected Set<String> pendingRMC;
   protected String currentCluster;
   protected LeadsBaseCallable mapperCallable;
   protected LeadsBaseCallable reducerCallable;
   protected Cache inputCache;
   protected Cache outputCache;
   protected Cache reduceInputCache;
   protected String finalOperatorName, statInputSizeKey, statOutputSizeKey, statExecTimeKey;
   protected boolean isRemote = false;
   protected boolean executeOnlyMap = false;
   protected  boolean executeOnlyReduce = false;
   long startTime;
   protected boolean failed = false;
   private volatile Object mmcMutex = new Object();
   private volatile Object rmcMutex = new Object();
   protected LeadsMessageHandler handler = new CompleteExecutionHandler(com,this);
   private Map<String, String> mcResults;

   protected BasicOperator(Action action) {
      conf = action.getData();
      this.action = action;
   }
   protected BasicOperator(Node com, InfinispanManager manager,LogProxy log,Action action){
      super(com.getId() + "-operator-thread");
      System.err.println(this.getClass().getCanonicalName());
      mcResults = new HashMap<>();
      this.com = com;
      this.manager = manager;
      this.log = log;
      this.action = action;
      this.conf = action.getData().getObject("operator").getObject("configuration");
      isRemote = action.getData().containsField("remote");
      if(isRemote){
         executeOnlyMap = action.getData().containsField("map");
         executeOnlyReduce = action.getData().containsField("reduce");
      }
      else{
         executeOnlyMap = true;
         executeOnlyReduce =true;
      }

      this.globalConfig = action.getGlobalConf();
      this.currentCluster = LQPConfiguration.getInstance().getMicroClusterName();
      this.statisticsCache = (Cache) manager.getPersisentCache(StringConstants.STATISTICS_CACHE);
      this.init_statistics(this.getClass().getCanonicalName());
   }

   public JsonObject getConf() {
      return conf;
   }

   public void setConf(JsonObject conf) {
      this.conf = conf;
   }

   public Action getAction() {
      return action;
   }

   public void setAction(Action action) {
      this.action = action;
   }

   public InfinispanManager getManager() {
      return manager;
   }

   public void setManager(InfinispanManager manager) {
      this.manager = manager;
   }

   public Node getCom() {
      return com;
   }

   public void setCom(Node com) {
      this.com = com;
   }

   public Cache getStatisticsCache() {
      return statisticsCache;
   }

   public void setStatisticsCache(Cache statisticsCache) {
      this.statisticsCache = statisticsCache;
   }

   public LogProxy getLog() {
      return log;
   }

   public void setLog(LogProxy log) {
      this.log = log;
   }

   public JsonObject getGlobalConfig() {
      return globalConfig;
   }

   public void setGlobalConfig(JsonObject globalConfig) {
      this.globalConfig = globalConfig;
   }

   public Set<String> getPendingMMC() {
      return pendingMMC;
   }

   public void setPendingMMC(Set<String> pendingMMC) {
      this.pendingMMC = pendingMMC;
   }

   public Set<String> getPendingRMC() {
      return pendingRMC;
   }

   public void setPendingRMC(Set<String> pendingRMC) {
      this.pendingRMC = pendingRMC;
   }

   public String getCurrentCluster() {
      return currentCluster;
   }

   public void setCurrentCluster(String currentCluster) {
      this.currentCluster = currentCluster;
   }


   public LeadsBaseCallable getMapperCallable() {
      return mapperCallable;
   }
   @Override
   public void setMapperCallable(LeadsBaseCallable mapperCallable) {
      this.mapperCallable = mapperCallable;
   }

   public LeadsBaseCallable getReducerCallable() {
      return reducerCallable;
   }
   @Override
   public void setReducerCallable(LeadsBaseCallable reducerCallable) {
      this.reducerCallable = reducerCallable;
   }

   public String getFinalOperatorName() {
      return finalOperatorName;
   }

   public void setFinalOperatorName(String finalOperatorName) {
      this.finalOperatorName = finalOperatorName;
   }

   public String getStatInputSizeKey() {
      return statInputSizeKey;
   }

   public void setStatInputSizeKey(String statInputSizeKey) {
      this.statInputSizeKey = statInputSizeKey;
   }

   public String getStatOutputSizeKey() {
      return statOutputSizeKey;
   }

   public void setStatOutputSizeKey(String statOutputSizeKey) {
      this.statOutputSizeKey = statOutputSizeKey;
   }

   public String getStatExecTimeKey() {
      return statExecTimeKey;
   }

   public void setStatExecTimeKey(String statExecTimeKey) {
      this.statExecTimeKey = statExecTimeKey;
   }

   public long getStartTime() {
      return startTime;
   }

   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }


   protected void init_statistics(String finalOperatorName ){
      this.finalOperatorName=finalOperatorName;
      this.statInputSizeKey = finalOperatorName+"inputSize";
      this.statOutputSizeKey = finalOperatorName+"outputSize";
      this.statExecTimeKey = finalOperatorName+"timeSize";
   }

   @Override
   public void init(JsonObject config) {
      this.conf = config;
   }

   @Override
   public void execute() {
      startTime = System.currentTimeMillis();
      System.out.println("Execution Start! ");
      start();
   }

   @Override
   public void cleanup() {
      if(!isRemote)
      {
         unsubscribeToMapActions("execution." + com.getId() + "." + action.getId());
      }
      action.setStatus(ActionStatus.COMPLETED.toString());

      if(com != null)
         com.sendTo(action.getData().getString("owner"),action.asJsonObject());
      else
         System.err.println("PROBLEM Uninitialized com");
   }

   @Override
   public void failCleanup() {
      if(!isRemote)
      {
         unsubscribeToMapActions("execution." + com.getId() + "." + action.getId());
      }
      action.setStatus(ActionStatus.FAILED.toString());
      pendingMMC.clear();
      pendingRMC.clear();
      mmcMutex.notifyAll();

//      rmcMutex.notifyAll();
      if(com != null)
         com.sendTo(action.getData().getString("owner"),action.asJsonObject());
      else
         System.err.println("PROBLEM Uninitialized com");
   }

   public void updateStatistics(BasicCache input1,BasicCache input2, BasicCache output)
   {
      long endTime = System.currentTimeMillis();

      long inputSize = 1;
      long outputSize = 1;
      if(input1 != null)
         inputSize += input1.size();
      if(input2 != null)
         inputSize += input2.size();
      if(outputSize != 0){
         outputSize = output.size();
      }
      System.out.println("In#: " + inputSize + " Out#:" + outputSize + " Execution time: " + (endTime - startTime) / 1000.0 + " s");
      updateStatisticsCache(inputSize, outputSize, (endTime - startTime));
   }

   public void updateStatisticsCache(double inputSize, double outputSize, double executionTime){
      updateSpecificStatistic(statInputSizeKey, inputSize);
      updateSpecificStatistic(statOutputSizeKey, outputSize);
      updateSpecificStatistic(statExecTimeKey, executionTime);
   }

   public void updateSpecificStatistic(String StatNameKey, double NewValue){
      DescriptiveStatistics  stats;
      if(!statisticsCache.containsKey(StatNameKey)) {
         stats = new DescriptiveStatistics();
         //stats.setWindowSize(1000);
      }
      else
         stats=(DescriptiveStatistics)statisticsCache.get(StatNameKey);
      stats.addValue(NewValue);
      statisticsCache.put(StatNameKey, stats);
   }

   @Override
   public JsonObject getConfiguration() {
      return conf;
   }

   @Override
   public void setConfiguration(JsonObject config) {
      conf = config;
   }

   @Override
   public String getInput() {
      return action.getData().getObject("operator").getArray("inputs").get(0).toString();
   }

   @Override
   public void setInput(String input) {
      conf.putString("input",input);
   }

   @Override
   public String getOutput() {
      return action.getData().getObject("operator").getString("id");
   }


   @Override
   public void setOutput(String output) {
      conf.putString("output",output);
   }

   @Override
   public void setOperatorParameters(JsonObject parameters) {
      conf = parameters;
   }

   @Override
   public JsonObject getOperatorParameters() {
      return conf;
   }

//   @Override
//   public  boolean isSingleStage(){return true;}

   @Override
   public void findPendingMMCFromGlobal(){
      pendingMMC = new HashSet<>();
      if(executeOnlyMap) {
         for (String mc : getMicroCloudsFromOpSched()) {
            pendingMMC.add(mc);
         }
      }
   }
   @Override
   public void findPendingRMCFromGlobal() {
      pendingRMC = new HashSet<>();

      if (isSingleStage())
         return;
      if(executeOnlyReduce) {
         for (String mc : getMicroCloudsFromOpSched()) {
            pendingRMC.add(mc);
         }
      }
   }


   @Override
   public void run(){
      findPendingMMCFromGlobal();
      findPendingRMCFromGlobal();
      createCaches(isRemote, executeOnlyMap, executeOnlyReduce);
      if(executeOnlyMap) {
         setupMapCallable();
         executeMap();
      }
      if(!failed) {
         if (executeOnlyReduce) {
            setupReduceCallable();
            executeReduce();
         }
         if (!failed) {
            cleanup();
         } else {
            failCleanup();
         }
      }
      else {
         failCleanup();
      }

   }
   public void createCache(String microCloud, String cacheName ){
      String uri = getURIForMC(microCloud);
      try {
         WebServiceClient.initialize(uri);
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }
      try {
         WebServiceClient.putObject(cacheName,"",new JsonObject());
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   @Override
   public void signal(){
      synchronized (mmcMutex) {
         mmcMutex.notifyAll();
      }
   }

   @Override
   public void addResult(String mc, String status){
      mcResults.put(mc, status);
      pendingMMC.remove(mc);
   }
   @Override
   public void executeMap(){

      subscribeToMapActions(pendingMMC);
      for(String mc : pendingMMC){
         if(!mc.equals(currentCluster)){
            sendRemoteRequest(mc,true);
         }
      }

      if(pendingMMC.contains(currentCluster)){
         localExecuteMap();
      }

      synchronized (mmcMutex){
         while(pendingMMC.size() > 0)
         {
            System.out.println("Sleeping to executing " + mapperCallable.getClass().toString() + " pending clusters ");
            PrintUtilities.printList(Arrays.asList(pendingMMC));
            try {
               mmcMutex.wait(240000);
            } catch (InterruptedException e) {
               log.error("Interrupted " + e.getMessage());
               break;
            }
         }
      }
      for(Map.Entry<String,String> entry : mcResults.entrySet()){
         System.out.println("Execution on " + entry.getKey() + " was " + entry.getValue());
         log.error("Execution on " + entry.getKey() + " was " + entry.getValue());
         if(entry.getValue().equals("FAIL"))
            failed =true;
      }

   }

   private void sendRemoteRequest(String mc, boolean b) {
      Action newAction = new Action(action);
      JsonObject dataAction = action.asJsonObject().copy();
      JsonObject sched = new JsonObject();
      sched.putArray(mc,globalConfig.getObject("microclouds").getArray(mc));
      dataAction.getObject("operator").putObject("scheduling",sched);
      newAction.setData(dataAction);
      newAction.getData().putString("remote","remote");
      if(b) {
         newAction.getData().putString("map", "map");
      }
      else{
         newAction.getData().putString("reduce","reduce");

      }
      newAction.getData().putString("replyGroup", "execution." + com.getId() + "." + action.getId());
      newAction.getData().putString("coordinator",currentCluster);
      String uri = getURIForMC(mc);
      try {
         ActionResult remoteResult = WebServiceClient.executeMapReduce(newAction.asJsonObject(), uri);
         if(remoteResult.getStatus().equals("FAIL")){
            log.error("Remote invocation for " + mc + " failed ");
            replyForFailExecution(newAction);
         }
      } catch (MalformedURLException e) {
         log.error("Problem initializing web service client");
         newAction.getData().putString("microcloud", mc);
         newAction.getData().putString("STATUS","FAIL");
         replyForFailExecution(newAction);
      } catch (IOException e) {
         log.error("Problem remote callling remote execution web service client");
         newAction.getData().putString("microcloud", mc);
         newAction.getData().putString("STATUS","FAIL");
         replyForFailExecution(newAction);
      }

   }

   public void subscribeToMapActions(Set<String> pendingMMC) {
      synchronized (rmcMutex) {
         com.subscribe("execution." + com.getId() + "." + action.getId(), handler, new Callable() {
            @Override
            public Object call() throws Exception {
               synchronized (rmcMutex) {
                  rmcMutex.notifyAll();
               }
               return null;

            }
         });
         try {
            rmcMutex.wait();
         } catch (InterruptedException e) {
            log.error("Subscription wait interreped " + e.getMessage());
            System.err.println("Subscription wait interreped " + e.getMessage());
         }
      }

   }

   public void unsubscribeToMapActions(String group){
      com.unsubscribe(group);
   }
   @Override
   public void localExecuteMap(){
      if(mapperCallable != null) {
         if (inputCache.size() == 0) {
            replyForSuccessfulExecution(action);
            return;
         }
         DistributedExecutorService des = new DefaultExecutorService(inputCache);

//      ScanCallable callable = new ScanCallable(conf.toString(),getOutput());
         mapperCallable.setEnsembleHost(computeEnsembleHost());
         DistributedTaskBuilder builder = des.createDistributedTaskBuilder(mapperCallable);
         builder.timeout(1, TimeUnit.HOURS);
         DistributedTask task = builder.build();
         List<Future<String>> res = des.submitEverywhere(task);
//      Future<String> res = des.submit(callable);
         List<String> addresses = new ArrayList<String>();
         try {
            if (res != null) {
               for (Future<?> result : res) {
                  System.out.println(result.get());
                  addresses.add((String) result.get());
               }
               System.out.println("map " + mapperCallable.getClass().toString() +
                                          " Execution is done");
               log.info("map " + mapperCallable.getClass().toString() +
                                " Execution is done");
            } else {
               System.out.println("map " + mapperCallable.getClass().toString() +
                                          " Execution not done");
               log.info("map " + mapperCallable.getClass().toString() +
                                " Execution not done");
               failed = true;
               replyForFailExecution(action);
            }
         } catch (InterruptedException e) {
            log.error("Exception in Map Excuettion " + "map " + mapperCallable.getClass().toString() + "\n" +
                              e.getClass().toString());
            log.error(e.getMessage());
            System.err.println("Exception in Map Excuettion " + "map " + mapperCallable.getClass().toString() + "\n" +
                                       e.getClass().toString());
            System.err.println(e.getMessage());
            failed = true;
            replyForFailExecution(action);
         } catch (ExecutionException e) {
            log.error("Exception in Map Excuettion " + "map " + mapperCallable.getClass().toString() + "\n" +
                              e.getClass().toString());
            log.error(e.getMessage());
            System.err.println("Exception in Map Excuettion " + "map " + mapperCallable.getClass().toString() + "\n" +
                                       e.getClass().toString());
            System.err.println(e.getMessage());
            failed = true;
            replyForFailExecution(action);
         }
      }
      replyForSuccessfulExecution(action);
   }

   public String computeEnsembleHost() {
     String result = "";
      JsonObject targetEndpoints = action.getData().getObject("operator").getObject("targetEndpoints");
      List<String> sites = new ArrayList<>();
      for(String targetMC : targetEndpoints.getFieldNames()){
//         JsonObject mc = targetEndpoints.getObject(targetMC);
         sites.add(targetMC);
         //
      }
      Collections.sort(sites);
      for(String site : sites){
         result += site+":11222|";
      }
      result = result.substring(0,result.length()-1);
      return result;
   }

   @Override
   public void localExecuteReduce(){
      if(reducerCallable != null) {
         DistributedExecutorService des = new DefaultExecutorService(reduceInputCache);
         reducerCallable.setEnsembleHost(computeEnsembleHost());
         DistributedTaskBuilder builder = des.createDistributedTaskBuilder(reducerCallable);
         builder.timeout(1, TimeUnit.HOURS);
         DistributedTask task = builder.build();
         List<Future<String>> res = des.submitEverywhere(task);
//      Future<String> res = des.submit(callable);
         List<String> addresses = new ArrayList<String>();
         try {
            if (res != null) {
               for (Future<?> result : res) {
                  System.out.println(result.get());
                  addresses.add((String) result.get());
               }
               System.out.println("reduce " + reducerCallable.getClass().toString() +
                                          " Execution is done");
               log.info("reduce " + reducerCallable.getClass().toString() +
                                " Execution is done");
            } else {
               System.out.println("reduce " + reducerCallable.getClass().toString() +
                                          " Execution not done");
               log.info("reduce " + reducerCallable.getClass().toString() +
                                " Execution not done");
               failed = true;
               replyForFailExecution(action);
            }
         } catch (InterruptedException e) {
            log.error("Exception in reduce Excuettion " + "reduce " + reducerCallable.getClass().toString() + "\n" +
                              e.getClass().toString());
            log.error(e.getMessage());
            System.err.println("Exception in reduce Excuettion " + "reduce " + reducerCallable.getClass().toString() + "\n" +
                                       e.getClass().toString());
            System.err.println(e.getMessage());
            failed = true;
            replyForFailExecution(action);
         } catch (ExecutionException e) {
            log.error("Exception in reduce Excuettion " + "reduce " + reducerCallable.getClass().toString() + "\n" +
                              e.getClass().toString());
            log.error(e.getMessage());
            System.err.println("Exception in reduce Excuettion " + "map " + reducerCallable.getClass().toString() + "\n" +
                                       e.getClass().toString());
            System.err.println(e.getMessage());
            failed = true;
            replyForFailExecution(action);
         }
      }
         replyForSuccessfulExecution(action);
   }

   public void replyForSuccessfulExecution(Action action) {
      action.getData().putString("microcloud",currentCluster);
      action.getData().putString("STATUS","SUCCESS");
      com.sendTo("execution." + com.getId() + "." + action.getId(), action.asJsonObject());
   }

   public void replyForFailExecution(Action action){
      if(!action.getData().containsField("microcloud")) {
         action.getData().putString("microcloud", currentCluster);
      }
      action.getData().putString("STATUS", "FAIL");
      com.sendTo("execution." + com.getId() + "." + action.getId(),action.asJsonObject());
   }
   @Override
   public void executeReduce(){
      pendingMMC = new HashSet<>();
      mcResults = new HashMap<>();
      pendingMMC.addAll(pendingRMC);
//      subscribeToMapActions(pendingMMC);
      for(String mc : pendingMMC){
         if(!mc.equals(currentCluster)){
            sendRemoteRequest(mc,false);
         }
      }

      if(pendingMMC.contains(currentCluster)){
         localExecuteReduce();
      }

      synchronized (mmcMutex){
         while(pendingMMC.size() > 0)
         {
            System.out.println("Sleeping to executing " + reducerCallable.getClass().toString() + " pending clusters ");
            PrintUtilities.printList(Arrays.asList(pendingMMC));
            try {
               mmcMutex.wait(240000);
            } catch (InterruptedException e) {
               log.error("REduce Interrupted " + e.getMessage());
               break;
            }
         }
      }
      for(Map.Entry<String,String> entry : mcResults.entrySet()){
         System.out.println("Reduce Execution on " + entry.getKey() + " was " + entry.getValue());
         log.error("Reduce Execution on " + entry.getKey() + " was " + entry.getValue());
         if(entry.getValue().equals("FAIL"))
            failed =true;
      }
   }
   public  String getURIForMC(String microCloud) {
      String uri = globalConfig.getObject("microclouds").getArray(microCloud).get(0);
      return uri;
   }

   public Set<String> getMicroCloudsFromOpSched(){
      Set<String> result = new HashSet<>();
      JsonObject operator = action.getData().getObject("operator");
      JsonObject scheduling = operator.getObject("scheduling");
      for (String mc : scheduling.getFieldNames()){
         result.add(mc);
      }

      return result;
   }

   public Set<String> getTargetMC(){
      Set<String> result = new HashSet<>();
      return result;
   }
}
