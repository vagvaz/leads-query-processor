package eu.leads.processor.imanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.utils.storage.LeadsStorage;
import eu.leads.processor.common.utils.storage.LeadsStorageFactory;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionCategory;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.nqe.NQEConstants;
import eu.leads.processor.planner.QueryPlannerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.Properties;
import java.util.UUID;

import static eu.leads.processor.core.ActionStatus.PENDING;

/**
 * Created by vagvaz on 8/4/14.
 */
public class IManagerLogicWorker extends Verticle implements LeadsMessageHandler {

  private final String componentType = "imanager";
  JsonObject config;
  String imanager;
  String planner;
  LogProxy logg;
  Logger log;
  //    PersistenceProxy persistence;
  Node com;
  String id;
  String workQueueAddress;
  ObjectMapper mapper;
  LeadsStorage storage;

  @Override
  public void start() {
    super.start();
    config = container.config();
    imanager = config.getString("imanager");
    planner = config.getString("planner");
    workQueueAddress = config.getString("workqueue");
    id = config.getString("id");
    com = new DefaultNode();
    com.initialize(id, imanager, null, this, null, vertx);
    logg = new LogProxy(config.getString("log"), com);
    log = LoggerFactory.getLogger(IManagerLogicWorker.class);
    //        persistence = new PersistenceProxy(config.getString("persistence"), com, vertx);
    //        persistence.start();
    mapper = new ObjectMapper();
    Properties storageConf = new Properties();
    storageConf.setProperty("prefix", "/tmp/leads/");


    if(config.containsField("global")){
      JsonObject global = config.getObject("global");
      if(global.containsField("hdfs.uri") && global.containsField("hdfs.prefix") && global.containsField("hdfs.user"))
      {
        storageConf.setProperty("hdfs.url", global.getString("hdfs.uri"));
        storageConf.setProperty("fs.defaultFS", global.getString("hdfs.uri"));
        storageConf.setProperty("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        storageConf.setProperty("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        storageConf.setProperty("prefix", global.getString("hdfs.prefix"));
        storageConf.setProperty("hdfs.user", global.getString("hdfs.user"));
        storageConf.setProperty("postfix", "0");
        System.out.println("USING HDFS yeah!");
        log.info("using hdfs: " + global.getString("hdfs.user")+ " @ "+ global.getString("hdfs.uri") + global.getString("hdfs.prefix") );

        storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.HDFS,storageConf);
      }else
      {
        log.info("No defined all hdfs parameters using local storage ");
        storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.LOCAL, storageConf);
      }
    }else
      storage = LeadsStorageFactory.getInitializedStorage(LeadsStorageFactory.LOCAL,storageConf);
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
      switch (ActionStatus.valueOf(action.getStatus())) {
        case PENDING: //probably received an action from an external source
          if (label.equals(IManagerConstants.GET_OBJECT)) {
            action.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, action.asJsonObject());
          } else if (label.equals(IManagerConstants.PUT_OBJECT)) {
            action.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, action.asJsonObject());
          } else if (label.equals(IManagerConstants.GET_QUERY_STATUS)) {
//            log.info("peding get query status");
            action.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, action.asJsonObject());
          } else if (label.equals(IManagerConstants.GET_RESULTS)) {
//            log.info("completed reply get results");
            action.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, action.asJsonObject());
          } else if (label.equals(IManagerConstants.SUBMIT_QUERY)) {
            newAction = createNewAction(action);
            newAction.setCategory(ActionCategory.ACTION.toString());
            newAction.setLabel(IManagerConstants.CREATE_NEW_QUERY);
            newAction.setProcessedBy(id);
            newAction.setData(action.getData());
            newAction.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, newAction.asJsonObject());
          } else if (label.equals(IManagerConstants.SUBMIT_WORKFLOW)) {
            newAction = createNewAction(action);
            newAction.setCategory(ActionCategory.ACTION.toString());
            newAction.setLabel(IManagerConstants.CREATE_NEW_WORKFLOW);
            newAction.setProcessedBy(id);
            newAction.setData(action.getData());
            newAction.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, newAction.asJsonObject());
          } else if (label.equals(IManagerConstants.SUBMIT_PLUGIN)) {
            newAction = createNewAction(action);
            newAction.setCategory(ActionCategory.ACTION.toString());
            newAction.setLabel(IManagerConstants.REGISTER_PLUGIN);
            newAction.setProcessedBy(id);
            newAction.setData(action.getData());
            newAction.getData().putString("replyTo", msg.getString("from"));

            System.out.println("Plugin send " + newAction.asJsonObject().toString());
            com.sendWithEventBus(workQueueAddress, newAction.asJsonObject());
          } else if (label.equals(IManagerConstants.SUBMIT_SPECIAL)) {
            newAction = createNewAction(action);
            newAction.setCategory(ActionCategory.ACTION.toString());
            newAction.setLabel(IManagerConstants.CREATE_NEW_SPECIAL_QUERY);
            newAction.setProcessedBy(id);
            newAction.setData(action.getData());
            newAction.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, newAction.asJsonObject());
          }else if (label.equals(IManagerConstants.DEPLOY_PLUGIN)) {
            action.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, action.asJsonObject());
          }else if (label.equals(IManagerConstants.UNDEPLOY_PLUGIN)) {
            //                        newAction = createNewAction(action);
            //                        newAction.setCategory(ActionCategory.ACTION.toString());
            //                        newAction.setLabel(IManagerConstants.REGISTER_PLUGIN);
            //                        newAction.setProcessedBy(id);
            //                        newAction.setData(action.getData());
            //                        newAction.getData().putString("replyTo", msg.getString("from"));
            //                        System.out.println("Plugin send " + newAction.asJsonObject().toString());
            //                        com.sendWithEventBus(workQueueAddress, newAction.asJsonObject());
            action.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, action.asJsonObject());
          }else if (label.equals(IManagerConstants.PUT_ENC_OBJECT)){
            action.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, action.asJsonObject());
          }else if (label.equals(IManagerConstants.SUBMIT_ENC_QUERY)) {
            newAction = createNewAction(action);
            newAction.setCategory(ActionCategory.ACTION.toString());
            newAction.setLabel(IManagerConstants.CREATE_NEW_ENC_QUERY);
            newAction.setProcessedBy(id);
            newAction.setData(action.getData());
            newAction.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, newAction.asJsonObject());
          }else if(label.equals(IManagerConstants.UPLOAD_DATA)){
            String path = action.getData().getString("path");
            byte[] data = action.getData().getBinary("data");
            storage.writeData(path,data);
            action.getData().putString("replyTo", msg.getString("from"));
            JsonObject result = new JsonObject();
            result.putString("status","SUCCESS");
            result.putString("message","");
            com.sendTo(action.getData().getString("replyTo"), result);
          }
          else if(label.equals(IManagerConstants.EXECUTE_MAPREDUCE)){
            action.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, action.asJsonObject());
          }
          else if (label.equals(IManagerConstants.COMPLETED_MAPREDUCE)){
            action.getData().putString("replyTo", msg.getString("from"));
            com.sendWithEventBus(workQueueAddress, action.asJsonObject());
          }
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
          log.error("Received INPROCESS action that should not" + action.toString());
          break;
        case COMPLETED: // the action either a part of a multistep workflow (INPROCESSING) or it could be processed.
          if (label.equals(IManagerConstants.GET_OBJECT)) {
            com.sendTo(action.getData().getString("replyTo"), action.getResult());
          } else if (label.equals(IManagerConstants.PUT_OBJECT)) {
            if (!action.getResult().containsField("message")) {
              action.getResult().putString("message", "");
            }
            com.sendTo(action.getData().getString("replyTo"), action.getResult());
          } else if (label.equals(IManagerConstants.GET_QUERY_STATUS)) {
//            log.info("completed reply get query status");
            com.sendTo(action.getData().getString("replyTo"), action.getResult());

          } else if (label.equals(IManagerConstants.DEPLOY_PLUGIN)) {
            com.sendTo(action.getData().getString("replyTo"), action.getResult());
            if(action.getResult().getString("status").equals("SUCCESS")) {
              newAction = new Action(action);
              newAction.setLabel(NQEConstants.DEPLOY_PLUGIN);
              newAction.setStatus(ActionStatus.PENDING.toString());
              com.sendTo(StringConstants.NODEEXECUTORQUEUE, newAction.asJsonObject());
            }
          } else if (label.equals(IManagerConstants.UNDEPLOY_PLUGIN)) {
            JsonObject result = action.getResult();
            com.sendTo(action.getData().getString("replyTo"),result.getObject("reply"));
            if(result.getObject("reply").getString("status").equals("SUCCESS")){
              Action undeployAction = createNewAction(action);
              undeployAction.setData(result.getObject("undeployAction"));
              undeployAction.setLabel(NQEConstants.UNDEPLOY_PLUGIN);
              undeployAction.setDestination(undeployAction.getData().getString("owner"));
              newAction = undeployAction;
              com.sendTo(undeployAction.getData().getString("owner"), undeployAction.asJsonObject());
            }
          } else if (label.equals(IManagerConstants.GET_RESULTS)) {
//            log.info("completed reply get query results");
            com.sendTo(action.getData().getString("replyTo"), action.getResult());
          } else if (label.equals(IManagerConstants.CREATE_NEW_QUERY)) {
            JsonObject webServiceReply = action.getResult().getObject("status");
            //Reply to the SUBMIT Query Action to the webservice
            com.sendTo(action.getData().getString("replyTo"), webServiceReply);
            //Create Action for the QueryPlanner to create the plan for the new Query.
            if (!action.getResult().containsField("error")) {
              Action plannerAction = createNewAction(action);
              plannerAction.setCategory(ActionCategory.ACTION.toString());
              plannerAction.setLabel(QueryPlannerConstants.PROCESS_SQL_QUERY);
              plannerAction.setDestination(StringConstants.PLANNERQUEUE);
              plannerAction.setData(action.getResult());
              newAction = plannerAction;
              com.sendTo(plannerAction.getDestination(),
                          plannerAction.asJsonObject());
            }
          } else if (label.equals(IManagerConstants.CREATE_NEW_WORKFLOW)) {
            JsonObject webServiceReply = action.getResult().getObject("status");
            //Reply to the SUBMIT Query Action to the webservice
            com.sendTo(action.getData().getString("replyTo"), webServiceReply);
            //Create Action for the QueryPlanner to create the plan for the new Query.
            if (!action.getResult().containsField("error")) {
              Action plannerAction = createNewAction(action);
              plannerAction.setCategory(ActionCategory.ACTION.toString());
              plannerAction.setLabel(QueryPlannerConstants.PROCESS_WORKFLOW_QUERY);
              plannerAction.setDestination(StringConstants.PLANNERQUEUE);
              plannerAction.setData(action.getResult());
              newAction = plannerAction;
              com.sendTo(plannerAction.getDestination(),
                          plannerAction.asJsonObject());
            }
          } else if (label.equals(IManagerConstants.CREATE_NEW_SPECIAL_QUERY)) {
            JsonObject webServiceReply = action.getResult().getObject("status");
            //Reply to the SUBMIT Query Action to the webservice
            com.sendTo(action.getData().getString("replyTo"), webServiceReply);
            //Create Action for the QueryPlanner to create the plan for the new Query.
            if (!action.getResult().containsField("error")) {
              Action plannerAction = createNewAction(action);
              plannerAction.setCategory(ActionCategory.ACTION.toString());
              plannerAction.setLabel(QueryPlannerConstants.PROCESS_SPECIAL_QUERY);
              plannerAction.setDestination(StringConstants.PLANNERQUEUE);
              plannerAction.setData(action.getResult());
              newAction = plannerAction;
              com.sendTo(plannerAction.getDestination(),
                          plannerAction.asJsonObject());
            }
          } else if (label.equals(IManagerConstants.REGISTER_PLUGIN)) {
            System.out.println(action.toString());

            JsonObject webServiceReply = action.getResult();
            //Reply to the SUBMIT Query Action to the webservice
            com.sendTo(action.getData().getString("replyTo"), webServiceReply);
            //Create Action for the QueryPlanner to create the plan for the new Query.
          }else if (label.equals(IManagerConstants.PUT_ENC_OBJECT)){
            if (!action.getResult().containsField("message")) {
              action.getResult().putString("message", "");
            }
            com.sendTo(action.getData().getString("replyTo"), action.getResult());
          }else if (label.equals(IManagerConstants.CREATE_NEW_ENC_QUERY)){
            JsonObject webServiceReply = action.getResult().getObject("status");
            //Reply to the SUBMIT Query Action to the webservice
            com.sendTo(action.getData().getString("replyTo"), webServiceReply);
            //Create Action for the QueryPlanner to create the plan for the new Query.
            if (!action.getResult().containsField("error")) {
              Action plannerAction = createNewAction(action);
              plannerAction.setCategory(ActionCategory.ACTION.toString());
              plannerAction.setLabel(QueryPlannerConstants.PROCESS_SPECIAL_QUERY);
              plannerAction.setDestination(StringConstants.PLANNERQUEUE);
              plannerAction.setData(action.getResult());
              newAction = plannerAction;
              com.sendTo(plannerAction.getDestination(),
                          plannerAction.asJsonObject());
            }
          }
          else if (label.equals(IManagerConstants.EXECUTE_MAPREDUCE)){
            JsonObject webServiceReply = action.getResult().getObject("status");
            com.sendTo(action.getData().getString("replyTo"),webServiceReply);
            newAction = createNewAction(action);
            newAction.setLabel(NQEConstants.DEPLOY_REMOTE_OPERATOR);
            newAction.setDestination((StringConstants.NODEEXECUTORQUEUE));
            newAction.setData(action.getResult().getObject("result"));
            com.sendTo(newAction.getDestination(),newAction.asJsonObject());

          }
          else if (label.equals(IManagerConstants.COMPLETED_MAPREDUCE)){
            JsonObject webServiceReply = action.getResult().getObject("status");
            com.sendTo(action.getData().getString("replyTo"),webServiceReply);
            newAction = createNewAction(action);
            newAction.setLabel(NQEConstants.OPERATOR_COMPLETE);
            newAction.setDestination(action.getResult().getString("replyGroup"));
            newAction.setData(action.getResult().getObject("result"));
            newAction.setStatus(ActionStatus.COMPLETED.toString());
            System.out.println("Send to " + newAction.getDestination() + " COMPLETED MAPREDUCE");
            com.sendTo(newAction.getDestination(),newAction.asJsonObject());
          }
          else if(label.equals(IManagerConstants.QUIT)){
            System.out.println(" Imanager logic worker recovery ");
            stop();
          }
          else {
            log.error("Unknown COMPLETED OR INPROCESS Action received " + action
                                                                            .toString());
            return;
          }
          if(newAction != null)
          {
            logAction(newAction);
          }
          finalizeAction(action);
      }
    }
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
    result.setStatus(PENDING.toString());
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

  private String generateNewQueryId(String prefix) {

    String candidateId = prefix + "." + UUID.randomUUID();
    //TODO create action
    //        while (persistence.contains(StringConstants.QUERIESCACHE, candidateId)) {
    //            candidateId = prefix + "." + UUID.randomUUID();
    //        }
    return candidateId;
  }

  private void updateQueryReadStatus(String queryId, JsonObject queryStatus, Long min, Long max) {
    JsonObject readStatus = queryStatus.getObject("read");
    //      if(Long.parseLong(readStatus.getString("min")) > min)
    //      {
    //         readStatus.putString("min",Long.toString(min));
    //      }
    //      Long size = Long.parseLong(readStatus.getString("size"));
    //      if( (Long.parseLong(readStatus.getString("max") ) < max) && (max < size) ){
    //         readStatus.putString("max",Long.toString(max));
    //      }else if(max < 0 || max > size){
    //         readStatus.putString("max",Long.toString(max));
    //         if(readStatus.getString("min").equals("0"))
    //            readStatus.putString("readFully","true");
    //      }
    if (readStatus.getLong("min") > min) {
      readStatus.putNumber("min", min);
    }
    Long size = readStatus.getLong("size");
    if ((readStatus.getLong("max") < max) && (max < size)) {
      readStatus.putNumber("max", max);
    } else if (max < 0 || max > size) {
      readStatus.putNumber("max", max);
      if (readStatus.getLong("min") == 0)
        readStatus.putBoolean("readFully", true);
    }
    queryStatus.putObject("read", readStatus);
    //        persistence.put(StringConstants.QUERIESCACHE, queryId, queryStatus);
    //TODO read through action inside  do it excluisvely in processor
  }
}
