package eu.leads.processor.imanager.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.QueryState;
import eu.leads.processor.core.plan.QueryStatus;
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
    JsonObject actionResult = new JsonObject();
    public GetQueryStatusActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                          String id) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
       queriesCache = (Cache) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
      actionResult = new QueryStatus("",QueryState.PENDING,"INITIAL").asJsonObject();
    }

    @Override
    public Action process(Action action) {
//      log.info("process get query status");
        Action result = action;

       try {
            String queryId = action.getData().getString("queryId");
//            JsonObject actionResult = persistence.get(StringConstants.QUERIESCACHE, queryId);
//         log.info("read query"); SELECT sourceIP FROM Rankings AS R JOIN  uservisits UV  ON R.pageURL = UV.desturl WHERE pagerank < 10  LIMIT 1;
//         log.info("read query"); SELECT paeran     FROM Rankings  WHERE pagerank < 10  LIMIT 1;
            String queryJson = queriesCache.get(queryId);

            if(queryJson != null) {
              JsonObject query = new JsonObject(queryJson);

              result.setResult(query.getObject("status"));
            }
            else{
              result.setResult( new QueryStatus(queryId, QueryState.PENDING,"NON-EXISTENT").asJsonObject());
            }
           }catch(Exception e){
         log.info("exception in read");
              e.printStackTrace();
              actionResult.putString("error", e.getMessage());
              result.setResult(actionResult);
            }
        result.setStatus(ActionStatus.COMPLETED.toString());
//      log.info("preturn query status");
        return result;
    }
}
