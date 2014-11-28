package eu.leads.processor.imanager.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.QueryContext;
import eu.leads.processor.core.plan.QueryState;
import eu.leads.processor.core.plan.QueryStatus;
import eu.leads.processor.core.plan.SQLQuery;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Created by vagvaz on 8/6/14.
 */
public class CreateQueryActionHandler implements ActionHandler {
    private final Node com;
    private final LogProxy log;
    private final InfinispanManager persistence;
    private final String id;
    private Cache<String,String> queriesCache;
    private RandomAccessFile raf;
    public CreateQueryActionHandler(Node com, LogProxy log, InfinispanManager persistence,
                                       String id) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
       queriesCache = (Cache<String, String>) persistence.getPersisentCache(StringConstants.QUERIESCACHE);
       try {
          raf = new RandomAccessFile("/tmp/queryhistory.log","w");
       } catch (FileNotFoundException e) {
          e.printStackTrace();
       }
    }

    @Override
    public Action process(Action action) {
        Action result = action;
       JsonObject actionResult = new JsonObject();
        try {
            JsonObject q = action.getData();
            if (q.containsField("sql")) {//SQL Query
                String user = q.getString("user");
                String sql = q.getString("sql");
                String uniqueId = generateNewQueryId(user);
                if(raf.getChannel().isOpen()){
                   raf.seek(raf.length());
                   raf.writeBytes(sql+"\n");
                   raf.close();
                }
               else{
                   raf = new RandomAccessFile("/tmp/queryhistory.log","w");
                   raf.seek(raf.length());
                   raf.writeBytes(sql+"\n");
                   raf.close();
                }
                SQLQuery query = new SQLQuery(user, sql);
                query.setId(uniqueId);
                QueryStatus status = new QueryStatus(uniqueId, QueryState.PENDING, "");
                query.setQueryStatus(status);
                QueryContext context = new QueryContext(uniqueId);
                query.setContext(context);
                JsonObject queryStatus = status.asJsonObject();
                queriesCache.put(uniqueId, query.asJsonObject().toString());
                actionResult.putObject("status", query.getQueryStatus().asJsonObject());
                actionResult.putObject("query",query.asJsonObject());
                result.setResult(actionResult);
                result.setStatus(ActionStatus.COMPLETED.toString());
            }
        } catch (Exception e) {
           actionResult.putString("error", "");
           actionResult.putString("message",
                                         "Failed to add query " + action.getData().toString()+"\n"
                                                 + " to the queries cache");
            result.setResult(actionResult);
        }
        return result;
    }

   private String generateNewQueryId(String prefix) {
      String candidateId = prefix + "." + UUID.randomUUID();
      int retries = 0;
      boolean checked = false;
      while (!checked) {
         try {
            candidateId = prefix + "." + UUID.randomUUID();
            checked = !queriesCache.containsKey(candidateId);
         }catch(Exception e ){
            log.error("Checking if queries cache contains key " + candidateId);
            retries++;
            if(retries > 10){
               log.error("Could not ensure that the query is unique continue either way");
               return candidateId;
            }
         }
      }

      return candidateId;
   }
}


