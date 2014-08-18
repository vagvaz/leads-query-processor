package eu.leads.processor.imanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionCategory;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.planner.QueryPlannerConstants;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;
import java.util.UUID;

import static eu.leads.processor.core.ActionStatus.PENDING;

/**
 * Created by vagvaz on 8/4/14.
 */
public class IManagerLogicWorker extends Verticle implements LeadsMessageHandler {

   JsonObject config;
   String imanager;
   String planner;
   LogProxy log;
   PersistenceProxy persistence;
   Node com;
   String id;
   String workQueueAddress;
   ObjectMapper mapper;
   private final String componentType = "imanager";

   @Override
   public void start() {
      super.start();
      config = container.config();
      imanager = config.getString("imanager");
      planner = config.getString("planner");
      workQueueAddress = config.getString("workqueue");
      id = config.getString("id");
      com = new DefaultNode();
      com.initialize(id,imanager,null,this,null,vertx);
      log = new LogProxy(config.getString("log"),com);
      persistence = new PersistenceProxy(config.getString("persist"),com);
      mapper = new ObjectMapper();

   }

   @Override
   public void stop() {
      super.stop();
      com.unsubscribe(id);
      com.unsubscribe(imanager);
   }

   @Override
   public void handle(JsonObject msg) {
      String type = msg.getString("type");
      String from = msg.getString(MessageUtils.FROM);
      String to = msg.getString(MessageUtils.TO);


////      if (type.equals("getObject")) {
////         try {
////            GetObjectQuery query = mapper.readValue(msg.getString("query"), GetObjectQuery.class);
////            JsonObject result = persistence.get(query.getTable(), query.getKey());
////            if (result.getString("status").equals("ok")) {
////               com.sendTo(from, result.getObject("result"));
////            } else {
////               result.putString("error", "");
////               com.sendTo(from, result);
////            }
////         } catch (IOException e) {
////            e.printStackTrace();
////         }
////
////      } else if (type.equals("putObject")) {
////         try {
////            PutObjectQuery query = mapper.readValue(msg.getString("query"), PutObjectQuery.class);
////            JsonObject value = new JsonObject(query.getObject());
////            boolean done = persistence.put(query.getTable(), query.getKey(), value);
////            JsonObject result = new JsonObject();
////            if (done) {
////               result.putString("status", "SUCCESS");
////            } else {
////               result.putString("status", "FAIL");
////               result.putString("error", "");
////               result.putString("message", "Could not store object " + value.toString() + " to " + query.getTable() + " with key " + query.getKey());
////            }
////            com.sendTo(from, result);
////         } catch (IOException e) {
////            e.printStackTrace();
////         }
////
////
////      } else if (type.equals("getQueryStatus")) {
////         String queryId = msg.getString("queryId");
////         JsonObject result = persistence.get(StringConstants.QUERIESCACHE, queryId);
////         if (result.getString("status").equals("ok")) {
////            com.sendTo(from, result.getObject("result"));
////         } else {
////            result.putString("error", "");
////            com.sendTo(from, result);
////         }
////
////      } else if (type.equals("getResults")) {
////         String queryId = msg.getString("queryId");
////         Long min = Long.parseLong(msg.getString("min"));
////         Long max = Long.parseLong(msg.getString("max"));
////         JsonObject result = new JsonObject();
////         if (min < 0) {
////            result.putString("error", "");
////            result.putString("message", "negative minimum parameter given");
////         }
////         JsonObject queryStatus = persistence.get(StringConstants.QUERIESCACHE, queryId);
////         if (!queryStatus.getString("status").equals("COMPLETED")) {
////            result.putString("error", "");
////            result.putString("message", "query " + queryId + " has not been completed yet");
////         } else {
////            String cacheName = queryStatus.getString("output");
////
////            JsonArray tuples = null;
////            if (max < 0) {
////               tuples = persistence.batchGet(cacheName, min);
////            } else {
////               tuples = persistence.batchGet(cacheName, min, max);
////            }
////            updateQueryReadStatus(queryId, queryStatus, min, max);
////            result.putString("id", queryId);
////            result.putString("min", String.valueOf(min));
////            result.putString("max", String.valueOf(max));
////            result.putString("result", tuples.toString());
////            result.putString("size", String.valueOf(tuples.size()));
////         }
////
////      } else if (type.equals("submitQuery")) {
////         JsonObject q = msg.getObject("query");
////         if (q.containsField("sql")) {//SQL Query
////            String user = q.getString("user");
////            String sql = q.getString("sql");
////            String uniqueId = generateNewQueryId(user);
////            JsonObject result = new JsonObject();
////            SQLQuery query = new SQLQuery(user, sql);
////            query.setId(uniqueId);
////            QueryStatus status = new QueryStatus(uniqueId, QueryState.PENDING, "");
////            query.setQueryStatus(status);
////            QueryContext context = new QueryContext(uniqueId);
////            query.setContext(context);
////            JsonObject queryStatus = status.asJsonObject();
////            if (persistence.put(StringConstants.QUERIESCACHE, uniqueId, query.asJsonObject())) {
////               com.sendTo(from, queryStatus);
////               com.sendTo(planner, query.asJsonObject());
////            } else {
////               result.putString("error", "");
////               result.putString("message", "Failed to add query " + sql + " from user " + user + " to the queries cache");
////               com.sendTo(from, result);
////            }
////
////
////         } else { //Workflow Query
////
////         }
////
////      } else if (type.equals("submitSpecialQuery")) {
////         JsonObject q = msg.getObject("query");
////         if (q.containsField("sql")) {//SQL Query
////
////            String queryType = q.getString("queryType");
////            if (queryType.equals("rec_call")) {
////               String url = q.getString("url");
////               String user = q.getString("user");
////               int depth = Integer.parseInt("depth");
////               String uniqueId = generateNewQueryId(user);
////               JsonObject result = new JsonObject();
////               RecursiveCallQuery query = new RecursiveCallQuery(user, url, depth);
////               query.setId(uniqueId);
////               QueryStatus status = new QueryStatus(uniqueId, QueryState.PENDING, "");
////               query.setQueryStatus(status);
////               QueryContext context = new QueryContext(uniqueId);
////               query.setContext(context);
////               JsonObject queryStatus = status.asJsonObject();
////
////               if (persistence.put(StringConstants.QUERIESCACHE, uniqueId, query.asJsonObject())) {
////                  com.sendTo(from, queryStatus);
////                  com.sendTo(planner, query.asJsonObject());
////               } else {
////                  result.putString("error", "");
////                  result.putString("message", "Failed to add wgs query " + " from user " + user + " to the queries cache");
////                  com.sendTo(from, result);
////               }
////            }
//
//
//         } else {
//            log.error("Received Unknown message action type \n" + msg.toString());
//         }
//      }
      if(type.equals("action")) {
         Action action = new Action(msg);
         String label = action.getLabel();
         Action newAction = null;
         action.setProcessedBy(id);
//         action.setStatus(ActionStatus.INPROCESS.toString());

         switch (ActionStatus.valueOf(action.getStatus())) {
            case PENDING: //probably received an action from an external source
               if (label.equals(IManagerConstants.GET_OBJECT)) {
                  action.getData().putString("replyTo",msg.getString("from"));
                  com.sendWithEventBus(workQueueAddress, action.asJsonObject());
               } else if (label.equals(IManagerConstants.PUT_OBJECT)) {
                  action.getData().putString("replyTo",msg.getString("from"));
                  com.sendWithEventBus(workQueueAddress, action.asJsonObject());
               } else if (label.equals(IManagerConstants.GET_QUERY_STATUS)) {
                  action.getData().putString("replyTo",msg.getString("from"));
                  com.sendWithEventBus(workQueueAddress, action.asJsonObject());
               } else if (label.equals(IManagerConstants.GET_RESULTS)) {
                  action.getData().putString("replyTo",msg.getString("from"));
                  com.sendWithEventBus(workQueueAddress, action.asJsonObject());
               } else if (label.equals(IManagerConstants.SUBMIT_QUERY)) {
                  Action newACtion = createNewAction(action);
                  newAction.setCategory(ActionCategory.ACTION.toString());
                  newAction.setLabel(IManagerConstants.CREATE_NEW_QUERY);
                  newAction.setProcessedBy(id);
                  newAction.setData(action.getData());
                  newAction.getData().putString("replyTo",msg.getString("from"));
                  com.sendWithEventBus(workQueueAddress, newAction.asJsonObject());
               } else if (label.equals(IManagerConstants.SUBMIT_SPECIAL)) {
                  Action newACtion = createNewAction(action);
                  newAction.setCategory(ActionCategory.ACTION.toString());
                  newAction.setLabel(IManagerConstants.CREATE_NEW_SPECIAL_QUERY);
                  newAction.setProcessedBy(id);
                  newAction.setData(action.getData());
                  newAction.getData().putString("replyTo",msg.getString("from"));
                  com.sendWithEventBus(workQueueAddress, newAction.asJsonObject());
               } else {
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
            case COMPLETED: // the action either a part of a multistep workflow (INPROCESSING) or it could be processed.
               if (label.equals(IManagerConstants.GET_OBJECT)) {
                  com.sendTo(action.getData().getString("replyTo"),action.getResult());
               } else if (label.equals(IManagerConstants.PUT_OBJECT)) {
                  com.sendTo(action.getData().getString("replyTo"),action.getResult());
               } else if (label.equals(IManagerConstants.GET_QUERY_STATUS)) {
                  com.sendTo(action.getData().getString("replyTo"), action.getResult());
               } else if (label.equals(IManagerConstants.GET_RESULTS)) {
                  com.sendTo(action.getData().getString("replyTo"), action.getResult());
               } else if (label.equals(IManagerConstants.CREATE_NEW_QUERY)) {
                  JsonObject webServiceReply =  action.getResult().getObject("status");
                  //Reply to the SUBMIT Query Action to the webservice
                  com.sendTo(action.getData().getString("replyTo"),webServiceReply);
                  //Create Action for the QueryPlanner to create the plan for the new Query.
                  if(!action.getResult().containsField("error")) {
                     Action plannerAction = createNewAction(action);
                     plannerAction.setCategory(ActionCategory.ACTION.toString());
                     plannerAction.setLabel(QueryPlannerConstants.PROCESS_QUERY);
                     plannerAction.setDestination(StringConstants.PLANNERQUEUE);
                     com.sendTo(plannerAction.getDestination(), plannerAction.asJsonObject());
                  }
               } else if (label.equals(IManagerConstants.CREATE_NEW_SPECIAL_QUERY)) {
                  JsonObject webServiceReply =  action.getResult().getObject("status");
                  //Reply to the SUBMIT Query Action to the webservice
                  com.sendTo(action.getData().getString("replyTo"),webServiceReply);
                  //Create Action for the QueryPlanner to create the plan for the new Query.
                  if(!action.getResult().containsField("error")) {
                     Action plannerAction = createNewAction(action);
                     plannerAction.setCategory(ActionCategory.ACTION.toString());
                     plannerAction.setLabel(QueryPlannerConstants.PROCESS_SPECIAL_QUERY);
                     plannerAction.setDestination(StringConstants.PLANNERQUEUE);
                     com.sendTo(plannerAction.getDestination(), plannerAction.asJsonObject());
                  }
               } else {
                  log.error("Unknown COMPLETED OR INPROCESS Action received " + action.toString());
                  return;
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
      result.setData(null);
      result.setResult(null);
      result.setLabel("");
      result.setCategory("");
      return result;
   }

   private String generateNewQueryId(String prefix) {

      String candidateId = prefix+"."+UUID.randomUUID();
      while (persistence.contains(StringConstants.QUERIESCACHE,candidateId)) {
         candidateId = prefix +"."+UUID.randomUUID();
      }
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
      if(readStatus.getLong("min") > min)
      {
         readStatus.putNumber("min", min);
      }
      Long size = readStatus.getLong("size");
      if( (readStatus.getLong("max")  < max) && (max < size) ){
         readStatus.putNumber("max", max);
      }else if(max < 0 || max > size){
         readStatus.putNumber("max", max);
         if(readStatus.getLong("min") == 0 )
            readStatus.putBoolean("readFully", true);
      }
      queryStatus.putObject("read",readStatus);
      persistence.put(StringConstants.QUERIESCACHE,queryId,queryStatus);
   }
}
