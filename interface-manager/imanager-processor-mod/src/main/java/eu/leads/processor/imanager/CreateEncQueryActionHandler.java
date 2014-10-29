package eu.leads.processor.imanager;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.*;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.util.UUID;

/**
 * Created by vagvaz on 10/29/14.
 */
public class CreateEncQueryActionHandler implements ActionHandler {

      private final Node com;
      private final LogProxy log;
      private final InfinispanManager persistence;
      private final String id;
      private Cache<String,String> queriesCache;
      public CreateEncQueryActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                             String id) {
         this.com = com;
         this.log = log;
         this.persistence = persistence;
         this.id = id;
         queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);

      }

      @Override
      public Action process(Action action) {
         Action result = action;
         JsonObject actionResult = new JsonObject();
         try {
            JsonObject q = action.getData().getObject("query");
            String queryType = action.getData().getString("type");
            String token = q.getString("token");
            String cacheName = q.getString("cache");
            String user = q.getString("user");
            String uniqueId = generateNewQueryId(user);
               PPPQCallQuery query = new PPPQCallQuery(user, cacheName,token);
               query.setId(uniqueId);
               QueryStatus status = new QueryStatus(uniqueId, QueryState.PENDING, "");
               query.setQueryStatus(status);
               QueryContext context = new QueryContext(uniqueId);
               query.setContext(context);
               JsonObject queryStatus = status.asJsonObject();
               query.asJsonObject().putString("queryType",queryType);
               queriesCache.put(uniqueId, query.asJsonObject().toString());
               actionResult.putObject("query",query.asJsonObject());
               JsonObject webServiceReply = new JsonObject();
               webServiceReply.putString("id",query.getId());
               webServiceReply.putString("output",query.getId());
               actionResult.putObject("status",webServiceReply);
               result.setResult(actionResult);
               result.setStatus(ActionStatus.COMPLETED.toString());
         } catch (Exception e) {
            actionResult.putString("error", "");
            actionResult.putString("message",
                                          "Failed to add wgs query " + " from user " + action.getData().toString());
            result.setResult(actionResult);

         }
         return result;
      }

      private String generateNewQueryId(String prefix) {
         String candidateId = prefix + "." + UUID.randomUUID();
         while (queriesCache.containsKey(candidateId)) {
            candidateId = prefix + "." + UUID.randomUUID();
         }
         return candidateId;
      }
}
