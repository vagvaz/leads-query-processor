package eu.leads.processor.imanager.handlers;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.encrypt.Etuple;
import eu.leads.processor.encrypt.Record;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Iterator;

/**
 * Created by vagvaz on 10/29/14.
 */
public class PutEncActionHandler implements ActionHandler {
   Node com;
   LogProxy log;
   InfinispanManager persistence;
   String id;

   public PutEncActionHandler(Node com, LogProxy log, InfinispanManager persistence, String id) {
      this.com = com;
      this.log = log;
      this.persistence = persistence;
      this.id = id;
   }

   @Override
   public Action process(Action action) {
      Action result = action;
      JsonObject actionResult = new JsonObject();
      try {
         String cacheName = action.getData().getString("cache");
         String key = action.getData().getString("key");
         if(action.getData().getBoolean("isData")){
            Etuple etuple = new Etuple();
            etuple.fromJson(action.getData().getObject("value"));
            Cache<String, Etuple> cache = (Cache<String, Etuple>) persistence.getPersisentCache(cacheName);
            log.info("putting encrypted data " + key  +"  " +etuple.toJson());
            cache.put(key, etuple);
         }
         else{
            JsonArray array = action.getData().getArray("value");
            Record[] actualValue = new Record[array.size()];
            Iterator<Object> iterator = array.iterator();
            int count = 0;
            while (iterator.hasNext()){
               JsonObject recordJson = (JsonObject)iterator.next();
               Record record = new Record();
               record.fromJson(recordJson);
               actualValue[count] = record;
               count++;
            }
            Cache<Integer,Record[]> cache = (Cache<Integer,Record[]>)persistence.getPersisentCache(cacheName);
            cache.put(Integer.parseInt(key),actualValue);
            log.info("putting encrypted index " + key  +"  \n");
            log.info("record 0 " + actualValue[0]);
         }

         actionResult.putString("status", "SUCCESS");
      } catch (Exception e) {
         actionResult.putString("status", "FAIL");
         actionResult.putString("error", "");
         actionResult.putString("message",
                                       "Could not store object " + action.getData().toString());
         System.err.println(e.getMessage());
      }
      result.setResult(actionResult);
      result.setStatus(ActionStatus.COMPLETED.toString());
      return result;
   }
}
