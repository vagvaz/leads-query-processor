package eu.leads.processor.core;

import com.google.common.base.Strings;
import eu.leads.processor.common.infinispan.CacheManagerFactory;
import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.common.infinispan.RangeFilter;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.Node;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vagvaz on 7/13/14.
 * The Persistence Verticle is responsible for interacting with the technologies like Infinispan, Hazelcast, DBs for
 * persisting data and reading/writing data from those technologies. The Persistence Verticle must always be deployed as
 * worker verticle as most of the functionalities can be bloking (remote calls).
 */
public class PersistenceVerticle extends Verticle {
    public PersistLeadsMessageHandler dispatcher;
    private InfinispanManager manager;
    private Map<String, Cache> caches;
    private JsonObject okResult = new JsonObject().putString("status", "ok");
    private JsonObject failResult = new JsonObject().putString("status", "fail");
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
            caches = new HashMap<String, Cache>();
            config = container.config();
            id = config.getString("id");
            componentId = config.getString("componentId");

            bus = new DefaultNode();
            logUtil = new LogProxy(config.getString("log"), bus);

            LQPConfiguration.initialize();//TODO
            dispatcher = new PersistLeadsMessageHandler(this, bus, logUtil);
            bus.initialize(id, id, null, dispatcher, dispatcher, this.getVertx());
            //         vertx.eventBus().registerHandler(id,dispatcher);
            this.manager = CacheManagerFactory.createCacheManager();
            this.componentState =
                (Cache<String, String>) this.manager.getPersisentCache(componentId);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonObject storeAction(JsonObject msg) {

        try {
            componentState.put(msg.getString("key"), msg.getString("value"));
        } catch (Exception e) {
            return failResult;
        }
        return okResult;
    }

    public JsonObject readAction(JsonObject msg) {
        JsonObject result = new JsonObject();
        String jsonValue = componentState.get(msg.getString("key"));
        if (!Strings.isNullOrEmpty(jsonValue)) {
            result.putString("status", "ok");
            result.putString("result", jsonValue);
        } else {
            result.putString("status", "fail");
            result.putString("message", "Key " + msg.getString("key")
                                            + " does not exist in component cache ");
            result.putString("result", "{}");
        }
        return result;
    }

    public JsonObject putAction(JsonObject msg) {
        String cacheName = msg.getString("cache");
        Cache<String, String> cache = getCache(cacheName);
        try {
            cache.put(msg.getString("key"), msg.getString("value"));
        } catch (Exception e) {
            return failResult;
        }
        return okResult;
    }

    public JsonObject getAction(JsonObject msg) {
        JsonObject result = new JsonObject();

        String cacheName = msg.getString("cache");
        Cache cache = getCache(cacheName);
        //      if (cache == null) {
        //         cache = (Cache<String, String>) manager.getPersisentCache(cacheName);
        //         caches.put(cacheName, cache);
        //      }
        String jsonValue = (String) cache.get(msg.getString("key"));
        if (!Strings.isNullOrEmpty(jsonValue)) {
            result.putString("status", "ok");
            result.putString("result", jsonValue);
        } else {
            result.putString("status", "fail");
            result.putString("message", "Key " + msg.getString("key") + " does not exist in cache "
                                            + cacheName);
            result.putString("result", "{}");
        }
        return result;
    }

    public JsonObject getBactchAction(JsonObject msg) {
        JsonObject result = new JsonObject();
        JsonArray listOfValues = new JsonArray();
        String cacheName = msg.getString("cache");
        long min = (long) msg.getNumber("min");
        long max = (long) msg.getNumber("max");
        Cache cache = getCache(cacheName);
        try {
            CloseableIterable<Map.Entry<String, String>> iterable =
                cache.getAdvancedCache().filterEntries(new RangeFilter(min, max));
            for (Map.Entry<String, String> entry : iterable) {
                listOfValues.add(entry.getValue());
            }
        } catch (Exception e) {

            logUtil.error("Iterating over " + cacheName + " for batch resulted in Exception "
                              + e.getMessage() + "\n from msg " + msg.toString());
            result.putString("status", "failed");
            result.putArray("result", new JsonArray());
            return result;
        }

        result.putString("status", "ok");
        result.putArray("result", listOfValues);
        return result;
    }

    private Cache getCache(String cacheName) {
        Cache result = caches.get(cacheName);
        if (result == null) {
            result = (Cache<String, String>) manager.getPersisentCache(cacheName);
            caches.put(cacheName, result);
        }
        return result;
    }

    public JsonObject getContainsAction(JsonObject msg) {

        JsonObject result = new JsonObject();
        String cacheName = msg.getString("cache");
        String key = msg.getString("key");
        boolean isContained = false;
        try {
            Cache cache = getCache(cacheName);
            isContained = cache.containsKey(key);
        } catch (Exception e) {
            logUtil.error("Checking for contains in" + cacheName + " for" + key
                              + "  resulted in Exception " + e.getMessage() + "\n from msg "
                              + msg.toString());
            result.putString("status", "failed");
            result.putBoolean("result", false);
            return result;
        }
        result.putString("status", "ok");
        result.putBoolean("result", isContained);
        return result;
    }
}
