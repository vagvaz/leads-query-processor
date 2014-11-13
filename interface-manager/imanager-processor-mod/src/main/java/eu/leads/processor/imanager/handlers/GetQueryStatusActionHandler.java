package eu.leads.processor.imanager.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 8/6/14.
 */
public class GetQueryStatusActionHandler implements ActionHandler {
    Node com;
    LogProxy log;
    InfinispanManager persistence;
    String id;
    Cache <String,String> queriesCache;
    public GetQueryStatusActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                          String id) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
       queriesCache = (Cache) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
    }

    @Override
    public Action process(Action action) {
      log.info("process get query resutls");
        Action result = action;
       JsonObject actionResult = new JsonObject();
       try {
            String queryId = action.getData().getString("queryId");
//            JsonObject actionResult = persistence.get(StringConstants.QUERIESCACHE, queryId);
         log.info("read query");
            String queryJson = queriesCache.get(queryId);

            JsonObject query = new JsonObject(queryJson);
            result.setResult(query.getObject("status"));

           }catch(Exception e){
         log.info("exception in read");
              actionResult.putString("error", "");
              result.setResult(actionResult);
            }
        result.setStatus(ActionStatus.COMPLETED.toString());
      log.info("preturn query resutls");
        return result;
    }
}
