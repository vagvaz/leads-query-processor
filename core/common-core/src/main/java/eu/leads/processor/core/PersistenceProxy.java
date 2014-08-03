package eu.leads.processor.core;

import eu.leads.processor.core.net.Node;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 7/16/14.
 */
public class PersistenceProxy {

  private String id;
  private JsonObject putAction;
  private JsonObject getAction;
  private JsonObject readAction;
  private JsonObject storeAction;
  private Node bus;
  private ReplyHandler replyHandler;
  public PersistenceProxy(String id,Node bus) {
    this.id = id;
    this.bus = bus;
    replyHandler = new ReplyHandler();


    putAction = new JsonObject();
    putAction.putString("action","put");
    putAction.putString("cache","");
    putAction.putString("key","");
    getAction = new JsonObject();
    getAction.putString("action","get");
    getAction.putString("cache","");
    getAction.putString("key","");
    readAction = new JsonObject();
    readAction.putString("action","read");
    readAction.putString("key","");
    storeAction = new JsonObject();
    storeAction.putString("action","store");
    storeAction.putString("key","");
  }


  public JsonObject get(String cacheName, String key){
    JsonObject result = null;
    getAction.putString("cache",cacheName);
    getAction.putString("key",key);
    bus.sendRequestTo(id,getAction,replyHandler);
    result = replyHandler.waitForMessage();
    return result;
  }

  public JsonObject read(String key){
    JsonObject result = null;
    storeAction.putString("key",key);
    bus.sendRequestTo(id,readAction,replyHandler);
    result = replyHandler.waitForMessage();
    return result;
  }

  public boolean put(String cacheName,String key, JsonObject value){
    putAction.putString("cache",cacheName);
    putAction.putString("key",key);
    putAction.putString("value",value.toString());
    bus.sendRequestTo(id,putAction,replyHandler);
    return replyHandler.waitForStatus();
  }

  public boolean store(String key, JsonObject value){
    putAction.putString("key",key);
    putAction.putString("value",value.toString());
    bus.sendRequestTo(id,storeAction,replyHandler);
    return replyHandler.waitForStatus();
  }



}
