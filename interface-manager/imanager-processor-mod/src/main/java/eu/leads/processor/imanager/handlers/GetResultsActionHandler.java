package eu.leads.processor.imanager.handlers;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.AcceptAllFilter;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.infinispan.RangeFilter;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Map;

/**
 * Created by vagvaz on 8/6/14.
 */
public class GetResultsActionHandler implements ActionHandler {
    private final Node com;
    private final LogProxy log;
    private final InfinispanManager persistence;
    private final String id;
    private Cache<String,String> queriesCache;
    public GetResultsActionHandler(Node com, LogProxy log, InfinispanManager persistence,
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
        try {
            String queryId = action.getData().getString("queryId");
            Long min = Long.parseLong(action.getData().getString("min"));
            Long max = Long.parseLong(action.getData().getString("max"));
            JsonObject actionResult = new JsonObject();
            if (min < 0) {
                actionResult.putString("error", "");
                actionResult.putString("message", "negative minimum index parameter given");
            }
            String queryJson = queriesCache.get(queryId);
            if(queryJson == null || queryJson.equals(""))
            {
                actionResult.putString("error", "");
                actionResult
                    .putString("message", "query " + queryId + " has not been completed yet");
            } else {
                JsonObject queryStatus = new JsonObject(queryJson);
                String cacheName = queryStatus.getString("output");
                boolean isSorted = queryStatus.getBoolean("isSorted");
                JsonObject tuples = null;
                if (max < 0) {
                    tuples = batchGet(cacheName,isSorted, min);
                } else {
                    tuples = batchGet(cacheName,isSorted, min, max);
                }
//                updateQueryReadStatus(queryId, queryStatus, min, max);
                actionResult.putString("id", queryId);
                actionResult.putString("min", String.valueOf(min));
                actionResult.putString("max", String.valueOf(max));
                actionResult.putString("result", tuples.getArray("result").toString());
                actionResult.putString("size", String.valueOf(tuples.size()));


            }
            result.setResult(actionResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.setStatus(ActionStatus.COMPLETED.toString());
        return result;
    }

   private JsonObject batchGet(String cacheName, boolean isSorted, Long min) {
      JsonObject result = new JsonObject();
      JsonArray listOfValues = new JsonArray();


      Cache cache = (Cache) persistence.getPersisentCache(cacheName);
        if(isSorted) {
            long cacheSize = cache.size();
            for (long index = 0; index < cacheSize; index++) {
                String value = (String) cache.get(String.valueOf(index));
                listOfValues.add(value);
            }
        }
        else{
            try {
                CloseableIterable<Map.Entry<String, String>> iterable =
                    cache.getAdvancedCache().filterEntries(new AcceptAllFilter());
                for (Map.Entry<String, String> entry : iterable) {
                    listOfValues.add(entry.getValue());
                }
            } catch (Exception e) {

                log.error("Iterating over " + cacheName + " for batch resulted in Exception "
                              + e.getMessage() + "\n from  " + cacheName);
                result.putString("status", "failed");
                result.putArray("result", new JsonArray());
                return result;
            }
      }

      result.putString("status", "ok");
      result.putArray("result", listOfValues);
      return result;
   }

   private JsonObject batchGet(String cacheName, boolean isSorted, Long min, Long max) {
      return batchGet(cacheName,isSorted,min,max);
   }


   private void updateQueryReadStatus(String queryId, JsonObject queryStatus, Long min, Long max) {
        JsonObject readStatus = queryStatus.getObject("read");
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
        queriesCache.put(queryId, queryStatus.toString());
    }
}
