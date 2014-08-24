package eu.leads.processor.imanager.handlers;

import eu.leads.processor.common.StringConstants;
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
public class GetQueryStatusActionHandler implements ActionHandler {
   Node com;
   LogProxy log;
   PersistenceProxy persistence;
   String id;

   public GetQueryStatusActionHandler(Node com, LogProxy log, PersistenceProxy persistence, String id) {
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
         JsonObject actionResult = persistence.get(StringConstants.QUERIESCACHE, queryId);
         if (actionResult.getString("status").equals("ok")) {
           result.setResult(actionResult.getObject("result").getObject("status"));
         } else {
            actionResult.putString("error", "");
            result.setResult(actionResult);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      result.setStatus(ActionStatus.COMPLETED.toString());
      return result;
   }
}
