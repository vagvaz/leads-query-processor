package eu.leads.processor.imanager.handlers;

import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 8/6/14.
 */
public class PutObjectActionHandler implements ActionHandler {

   Node com;
   LogProxy log;
   PersistenceProxy persistence;
   String id;

   public PutObjectActionHandler(Node com, LogProxy log, PersistenceProxy persistence, String id) {
      this.com = com;
      this.log = log;
      this.persistence = persistence;
      this.id = id;
   }

   @Override
   public Action process(Action action) {
      Action result = action;

      try{
            String cacheName = action.getData().getString("table");
            String key = action.getData().getString("key");
            JsonObject value = action.getData().getObject("value");

            boolean done = persistence.put(cacheName,key, value);
            JsonObject actionResult = new JsonObject();
            if (done) {
               actionResult.putString("status", "SUCCESS");
            } else {
               actionResult.putString("status", "FAIL");
               actionResult.putString("error", "");
               actionResult.putString("message", "Could not store object " + value.toString() + " to " + cacheName + " with key " + key);
            }
            result.setResult(actionResult);
      }catch(Exception e){
         e.printStackTrace();
      }
      result.setStatus(ActionStatus.COMPLETED.toString());
      return result;
   }
}
