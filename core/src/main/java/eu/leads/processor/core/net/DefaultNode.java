package eu.leads.processor.core.net;

import eu.leads.processor.core.comp.LeadsMessageHandler;
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
  private JsonObject config;
  private Logger logger;
  private int retries = ComUtils.DEFAULT_RETRIES;
  private long timeout = ComUtils.DEFAULT_TIMEOUT;
  private long currentId;
  private Map<Long,JsonObject> pending;
  private Map<Long,AckHandler> pendingHandlers;
  private CommunicationHandler comHandler;



  /**
   * Getter for property 'comHandler'.
   *
   * @return Value for property 'comHandler'.
   */
  public CommunicationHandler getComHandler() {
    return comHandler;
  }

  /**
   * Setter for property 'comHandler'.
   *
   * @param comHandler Value to set for property 'comHandler'.
   */
  public void setComHandler(CommunicationHandler comHandler) {
    this.comHandler = comHandler;
  }

  /**
   * Getter for property 'failHandler'.
   *
   * @return Value for property 'failHandler'.
   */
  public LeadsMessageHandler getFailHandler() {
    return failHandler;
  }

  /**
   * Setter for property 'failHandler'.
   *
   * @param failHandler Value to set for property 'failHandler'.
   */
  public void setFailHandler(LeadsMessageHandler failHandler) {
    this.failHandler = failHandler;
  }

  private LeadsMessageHandler failHandler;

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
  public void sendTo(String nodeid, JsonObject message) {
    JsonObject leadsMessage = MessageUtils.createLeadsMessage(message, getId(),ComUtils.P2P, nodeid);
    sendMessageToDestination(nodeid,leadsMessage,null);
  }

  @Override
  public void sendRequestTo(String nodeid, JsonObject message, LeadsMessageHandler handler) {
    JsonObject leadsMessage = MessageUtils.createLeadsMessage(message, getId(),ComUtils.GROUP, nodeid);
    sendMessageToDestination(nodeid,leadsMessage,handler);
  }

  @Override
  public void sendToGroup(String groupId, JsonObject message) {
    JsonObject leadsMessage = MessageUtils.createLeadsMessage(message, getId(),ComUtils.GROUP, groupId);
    sendMessageToDestination(groupId,leadsMessage,null);
  }

  private void sendMessageToDestination(String destination, JsonObject leadsMessage,LeadsMessageHandler handler) {
    long messageId = this.getNextMessageId();
    AckHandler ack = new AckHandler(this,logger,messageId,handler);
    pending.put(messageId,leadsMessage);
    pendingHandlers.put(messageId,ack);
    bus.sendWithTimeout(destination,leadsMessage,timeout,ack);
  }


  @Override
  public void sendRequestToGroup(String groupId, JsonObject message, LeadsMessageHandler handler) {
    JsonObject leadsMessage = MessageUtils.createLeadsMessage(message, getId(),ComUtils.GROUP, groupId);
    sendMessageToDestination(groupId,leadsMessage,handler);
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
          config.getArray("groups").add(groupId);
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
          //TODO remove from config the groupId.config.getArray("groups").(groupId);
          comHandler.unregister(groupId);
        }
        else{
          logger.error("Fail to subscribe to " + groupId);
        }
      }
    });
  }

  @Override
  public void initialize(JsonObject config,LeadsMessageHandler defaultHandler, LeadsMessageHandler failHandler, Vertx vertx) {
    comHandler = new CommunicationHandler(defaultHandler);
    this.failHandler = failHandler;
    bus = vertx.eventBus();
    logger = LoggerFactory.getLogger(this.getClass().getCanonicalName() + "." + this.getId());
    this.config = config.copy();
    registerToEventBusAddresses(this.config);

  }

  @Override
  public void initialize(String id, String group, Set<String> groups,LeadsMessageHandler defaultHandler, LeadsMessageHandler failHandler, Vertx vertx) {
    JsonObject conf = new JsonObject();
    conf.putString("id",id);
    conf.putString("group",group);
    JsonArray array = new JsonArray();
    for(String g : groups){
      array.add(g);
    }
    conf.putArray("groups",array);
    initialize(conf, defaultHandler,failHandler, vertx);
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
      //resend message through event bus to the nodeid
      bus.sendWithTimeout(msg.getString(MessageUtils.TO),msg,timeout,handler);
    }
    else if (msg.getString(MessageUtils.TYPE).equals(ComUtils.GROUP)){
      //resend message through event bus to the groupId, it is essentially the same as
      //sending to one node since the event bus address is just an id.
      bus.sendWithTimeout(msg.getString(MessageUtils.TO),msg,timeout,handler);
    }
  }

  @Override
  public void succeed(Long messageId) {
    //If succeded remove message and ackHandler
    JsonObject msg = pending.remove(messageId);
    AckHandler handler = pendingHandlers.remove(messageId);
    handler = null;
    logger.info("Succeded Message: " + msg.toString());
  }

  @Override
  public void fail(Long messageId) {
    //Remove message andhandler from pending handlers
    JsonObject msg = pending.remove(messageId);
    AckHandler handler = pendingHandlers.remove(messageId);
    handler = null;
    logger.error("Failed Message: " + msg.toString() );
    if(failHandler != null){
      failHandler.handle(msg);
    }


  }

  @Override
  public  long getNextMessageId() {
    currentId += (currentId+1) % Long.MAX_VALUE;
    return currentId;
  }


}
