package eu.leads.processor.nqe.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.QueryContext;
import eu.leads.processor.core.plan.QueryState;
import eu.leads.processor.core.plan.QueryStatus;
import eu.leads.processor.core.plan.RecursiveCallQuery;
import org.vertx.java.core.json.JsonObject;

import java.util.UUID;

/**
 * Created by vagvaz on 8/6/14.
 */
public class CreateSpecialQueryActionHandler implements ActionHandler {
   private final Node com;
   private final LogProxy log;
   private final PersistenceProxy persistence;
   private final String id;

   public CreateSpecialQueryActionHandler(Node com, LogProxy log, PersistenceProxy persistence, String id) {
      this.com = com;
      this.log = log;
      this.persistence = persistence;
      this.id = id;
   }

   @Override
   public Action process(Action action) {
      Action result = action;
      try {
         JsonObject q = action.getData().getObject("query");
         String queryType = action.getData().getString("queryType");
            if (queryType.equals("rec_call")) {
               String url = q.getString("url");
               String user = q.getString("user");
               int depth = Integer.parseInt("depth");
               String uniqueId = generateNewQueryId(user);
               JsonObject actionResult = new JsonObject();
               RecursiveCallQuery query = new RecursiveCallQuery(user, url, depth);
               query.setId(uniqueId);
               QueryStatus status = new QueryStatus(uniqueId, QueryState.PENDING, "");
               query.setQueryStatus(status);
               QueryContext context = new QueryContext(uniqueId);
               query.setContext(context);
               JsonObject queryStatus = status.asJsonObject();

               if (!persistence.put(StringConstants.QUERIESCACHE, uniqueId, query.asJsonObject())) {
                  actionResult.putString("error", "");
                  actionResult.putString("message", "Failed to add wgs query " + " from user " + user + " to the queries cache");
               }
               result.setResult(actionResult);
               result.setStatus(ActionStatus.COMPLETED.toString());
            }

      }
      catch (Exception e){
         e.printStackTrace();
      }
      return result;
   }

   private String generateNewQueryId(String prefix) {
      String candidateId = prefix+"."+ UUID.randomUUID();
      while (persistence.contains(StringConstants.QUERIESCACHE,candidateId)) {
         candidateId = prefix +"."+UUID.randomUUID();
      }
      return candidateId;
   }
}
