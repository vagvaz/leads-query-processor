package eu.leads.processor.core.net;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.List;

/**
 * Created by vagvaz on 7/8/14.
 */
public interface Node {
  public boolean sendTo(String nodeid, JsonObject message);

  public void sendRequestTo(String nodeid, JsonObject message, LeadsMessageHandler handler);

  public boolean sendToGroup(String groupId, JsonObject message);

  public void sendRequestToGroup(String groupId, JsonObject message, CommunicationHandler handler);

  public void sendToAllGroup(String groupId, JsonObject message);

  public void subscribe(String groupId, LeadsMessageHandler handler);

  public void unsubscribe(String groupId);

  public void initialize(JsonObject config, LeadsMessageHandler handler, Vertx vertx);
  public void initialize(String id, String group, List<String> groups,LeadsMessageHandler handler,Vertx vertx);

  public JsonObject getConfig();
  public void setEventBus(EventBus bus);
  public int getRetries();
  public void setRetries(int retries);
  public long getTimeout();
  public void setTimeout(long timeout);
  public void retry(Long messageId,AckHandler handler);
  public void fail(Long messageId);
  public void succeed(Long messageId);
  public long getNextMessageId();

}
