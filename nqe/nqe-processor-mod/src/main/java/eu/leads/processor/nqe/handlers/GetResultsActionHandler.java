package eu.leads.processor.nqe.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 8/6/14.
 */
public class GetResultsActionHandler implements ActionHandler {
   private final Node com;
   private final LogProxy log;
   private final PersistenceProxy persistence;
   private final String id;

   public GetResultsActionHandler(Node com, LogProxy log, PersistenceProxy persistence, String id) {
      this.com = com;
      this.log = log;
      this.persistence = persistence;
      this.id = id;
   }

   @Override
   public Action process(Action action) {
      Action result = action;
      try {
         String queryId = action.getData().getString("queryId");
         Long min = Long.parseLong(action.getData().getString("min"));
         Long max = Long.parseLong(action.getData().getString("max"));
         JsonObject actionResult = new JsonObject();
         if (min < 0) {
            actionResult.putString("error", "");
            actionResult.putString("message", "negative minimum index parameter given");
         }
         JsonObject queryStatus = persistence.get(StringConstants.QUERIESCACHE, queryId);
         if (!queryStatus.getString("status").equals("COMPLETED")) {
            actionResult.putString("error", "");
            actionResult.putString("message", "query " + queryId + " has not been completed yet");
         } else {
            String cacheName = queryStatus.getString("output");

            JsonArray tuples = null;
            if (max < 0) {
               tuples = persistence.batchGet(cacheName, min);
            } else {
               tuples = persistence.batchGet(cacheName, min, max);
            }
            updateQueryReadStatus(queryId, queryStatus, min, max);
            actionResult.putString("id", queryId);
            actionResult.putString("min", String.valueOf(min));
            actionResult.putString("max", String.valueOf(max));
            actionResult.putString("result", tuples.toString());
            actionResult.putString("size", String.valueOf(tuples.size()));

         }
         result.setResult(actionResult);
      } catch (Exception e) {
         e.printStackTrace();
      }
      result.setStatus(ActionStatus.COMPLETED.toString());
      return result;
   }


   private void updateQueryReadStatus(String queryId, JsonObject queryStatus, Long min, Long max) {
      JsonObject readStatus = queryStatus.getObject("read");
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