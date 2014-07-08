package eu.leads.processor.core.net;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

import java.util.*;

/**
 * Created by vagvaz on 7/8/14.
 */
public class DefaultNode implements Node {
  private EventBus bus;
  JsonObject config;
  private Logger logger;
  private int retries = ComUtils.DEFAULT_RETRIES;
  private long timeout = ComUtils.DEFAULT_TIMEOUT;
  private AckHandler ackHandler;
  private long currentId;
  private Map<Long,JsonObject> pending;
  private Map<Long,AckHandler> pendingHandlers;
  private CommunicationHandler comHandler;
  public DefaultNode() {
    config = new JsonObject();
    pending = new HashMap<Long, JsonObject>();
    pendingHandlers = new HashMap<Long, AckHandler>();
  }

  public String getId(){
   return  config.getString("id");
  }

  public String getGroup(){
    return config.getString("group");
  }
  @Override
  public boolean sendTo(String nodeid, JsonObject message) {
    boolean send = true;
    JsonObject leadsMessage = MessageUtils.createLeadsMessage(message, getId(),ComUtils.P2P, nodeid);
    long messageId = this.getNextMessageId();
    AckHandler ack = new AckHandler(this,logger,messageId);
    pending.put(messageId,leadsMessage);
    pendingHandlers.put(messageId,ack);
    bus.sendWithTimeout(getId(),leadsMessage,timeout,ack);
    return send;
  }

  @Override
  public void sendRequestTo(String id, JsonObject message, LeadsMessageHandler handler) {

  }

  @Override
  public boolean sendToGroup(String groupId, JsonObject message) {
    boolean send = true;
    JsonObject leadsMessage = MessageUtils.createLeadsMessage(message, getId(),ComUtils.GROUP, groupId);
    long messageId = this.getNextMessageId();
    AckHandler ack = new AckHandler(this,logger,messageId);
    pending.put(messageId,leadsMessage);
    pendingHandlers.put(messageId,ack);
    bus.sendWithTimeout(getId(),leadsMessage,timeout,ack);
    return send;
  }

  @Override
  public void sendRequestToGroup(String groupId, JsonObject message, CommunicationHandler handler) {

  }

  @Override
  public void sendToAllGroup(String groupId, JsonObject message) {
    JsonObject leadsMessage = MessageUtils.createLeadsMessage(message, getId(),ComUtils.ALLGROUP, groupId);
    bus.publish(groupId,leadsMessage);
  }

  @Override
  public void subscribe(final String groupId, final LeadsMessageHandler handler) {

    bus.registerHandler(groupId,comHandler, new Handler< AsyncResult<Void>>(){
      @Override
      public void handle(AsyncResult<Void> event) {
        if(event.succeeded()){
          logger.info("subscribing to " + groupId + " succeded");
          comHandler.register(groupId,handler);
        }
        else{
          logger.error("Fail to subscribe to " + groupId);
        }
      }
    });

  }

  @Override
  public void unsubscribe(final String groupId) {
    bus.unregisterHandler(groupId,comHandler,new Handler< AsyncResult<Void>>(){
      @Override
      public void handle(AsyncResult<Void> event) {
        if(event.succeeded()){
          logger.info("unsubscribing to " + groupId + " succeded");
          comHandler.unregister(groupId);
        }
        else{
          logger.error("Fail to subscribe to " + groupId);
        }
      }
    });
  }

  @Override
  public void initialize(JsonObject config,LeadsMessageHandler handler, Vertx vertx) {
    comHandler = new CommunicationHandler(handler);
    bus = vertx.eventBus();
    logger = LoggerFactory.getLogger(this.getClass().getCanonicalName()+"."+this.getId());
    setConfig(config);
    registerToEventBusAddresses(this.config);

  }

  @Override
  public void initialize(String id, String group, List<String> groups,LeadsMessageHandler handler, Vertx vertx) {
    comHandler = new CommunicationHandler(handler);
    bus = vertx.eventBus();
    config.putString("id",id);
    config.putString("group",group);
    JsonArray array = new JsonArray();
    for(String g : groups){
      array.add(g);
    }

    registerToEventBusAddresses(config);
  }

  private void registerToEventBusAddresses(JsonObject config) {
    bus.registerHandler(getId(),comHandler);
    bus.registerHandler(getGroup(), comHandler);
    Iterator<Object> it = this.config.getArray("groups").iterator();
    while(it.hasNext()){
      bus.registerHandler((String) it.next(), comHandler);
    }
  }

  @Override
  public JsonObject getConfig() {
    return config;
  }


  public void setConfig(JsonObject config) {
    this.config = config.copy();
    bus.registerHandler(getId(),comHandler);
    bus.registerHandler(getGroup(), comHandler);
    Iterator<Object> it = this.config.getArray("groups").iterator();
    while(it.hasNext()){
      bus.registerHandler((String) it.next(), comHandler);
    }
  }


  @Override
  public void setEventBus(EventBus bus) {
    this.bus = bus;
  }

  @Override
  public int getRetries() {
    return retries;
  }

  @Override
  public void setRetries(int retries) {
    this.retries = retries;
  }

  @Override
  public long getTimeout() {
    return timeout;
  }

  @Override
  public void setTimeout(long timeout) {
     this.timeout = timeout;
  }

  @Override
  public void retry(Long messageId, AckHandler handler) {
    JsonObject msg = pending.get(messageId);
    if(msg.getString(MessageUtils.TYPE).equals(ComUtils.P2P)){
      bus.sendWithTimeout(msg.getString(MessageUtils.TO),msg,timeout,handler);
    }
    else if (msg.getString(MessageUtils.TYPE).equals(ComUtils.GROUP)){
      bus.sendWithTimeout(msg.getString(MessageUtils.TO),msg,timeout,handler);
    }
  }

  @Override
  public void succeed(Long messageId) {
    JsonObject msg = pending.remove(messageId);
    AckHandler handler = pendingHandlers.remove(messageId);
    handler = null;
    logger.error("Succeded Message: " + msg.toString() );
  }

  @Override
  public void fail(Long messageId) {
    //Remove message andhandler from pending handlers
    JsonObject msg = pending.remove(messageId);
    AckHandler handler = pendingHandlers.remove(messageId);
    handler = null;
    logger.error("Failed Message: " + msg.toString() );

  }

  @Override
  public  long getNextMessageId() {
    return currentId++ % Long.MAX_VALUE;
  }


}
