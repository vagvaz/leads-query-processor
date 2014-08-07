package eu.leads.processor.core;

import com.google.common.base.Strings;
import eu.leads.processor.common.infinispan.CacheManagerFactory;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.comp.DefaultFailHandler;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.Node;
import org.infinispan.Cache;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.Map;

/**
 * Created by vagvaz on 7/13/14.
 * The Persistence Verticle is responsible for interacting with the technologies like Infinispan, Hazelcast, DBs for
 * persisting data and reading/writing data from those technologies. The Persistence Verticle must always be deployed as
 * worker verticle as most of the functionalities can be bloking (remote calls).
 */
public class PersistenceVerticle extends Verticle {
   private InfinispanManager manager;
   private Map<String, Cache> caches;
   private JsonObject okResult = new JsonObject().putString("status", "ok");
   private JsonObject failResult = new JsonObject().putString("status", "fail");
   private LeadsMessageHandler dispatcher;
   private LogProxy logUtil;
   private Cache<String, String> componentState;
   private String id;
   private Node bus;
   private JsonObject config;
   private String componentId;

   @Override
   public void start() {
      try {
         super.start();
         //Initialize vertx structures
         config = container.config();
         id = config.getString("id");
//         componentId = config.getString("componentType");
         bus = new DefaultNode();
         logUtil = new LogProxy(config.getString("log"), bus);

         LQPConfiguration.initialize();//TODO

         //  this.manager = CacheManagerFactory.createCacheManager();
         //      this.componentState = (Cache<String, String>) this.manager.getPersisentCache(componentId);

         dispatcher = new LeadsMessageHandler() {

            @Override
            public void handle(JsonObject message) {
               JsonObject msg = message;
               JsonObject reply = null;
               String actionType = msg.getString("action");
               if (actionType.equals("get")) {
                  reply = getAction(msg);
               } else if (actionType.equals("put")) {
                  reply = putAction(msg);
               } else if (actionType.equals("read")) {
                  reply = readAction(msg);
               } else if (actionType.equals("store")) {
                  reply = storeAction(msg);
               } else {
                  logUtil.error("Unknown ActionType " + actionType + " Cannot Handle in Persist");

               }
               bus.sendTo(message.getString("from"), reply);
            }
         };

         bus.initialize(id, id, null, dispatcher, null, this.getVertx());
      }catch(Exception e ){
         e.printStackTrace();
      }
   }

   private JsonObject storeAction(JsonObject msg) {

      try {
         componentState.put(msg.getString("key"), msg.getString("value"));
      } catch (Exception e) {
         return failResult;
      }
      return okResult;
   }

   private JsonObject readAction(JsonObject msg) {
      JsonObject result;
      String jsonValue = componentState.get(msg.getString("key"));
      result = new JsonObject(jsonValue);
      return result;
   }

   private JsonObject putAction(JsonObject msg) {
      String cacheName = msg.getString("cache");
      Cache<String, String> cache = caches.get(cacheName);
      if (cache == null) {
         cache = (Cache<String, String>) manager.getPersisentCache(cacheName);
         caches.put(cacheName, cache);
      }
      try {
         cache.put(msg.getString("key"), msg.getString("value"));
      } catch (Exception e) {
         return failResult;
      }
      return okResult;
   }

   private JsonObject getAction(JsonObject msg) {
      JsonObject result = new JsonObject();

      String cacheName = msg.getString("cache");
      Cache<String, String> cache = caches.get(cacheName);
      if (cache == null) {
         cache = (Cache<String, String>) manager.getPersisentCache(cacheName);
         caches.put(cacheName, cache);
      }
      String jsonValue = cache.get(msg.getString("key"));
      if (!Strings.isNullOrEmpty(jsonValue)) {
         result.putString("status","ok");
         result.putString("result",jsonValue);
      }
      else{
         result.putString("status","fail");
         result.putString("message","Key " + msg.getString("key") + " does not exist in cache " + cacheName);
         result.putString("result","{}");
      }
      return result;
   }
}
