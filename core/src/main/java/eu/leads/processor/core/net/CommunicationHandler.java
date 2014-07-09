package eu.leads.processor.core.net;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vagvaz on 7/8/14.
 */
public class CommunicationHandler implements Handler<Message> {
  private Map<String,LeadsMessageHandler> handlers;
  public CommunicationHandler(LeadsMessageHandler defaultHandler) {
    handlers = new HashMap<String, LeadsMessageHandler>();
    handlers.put("default",defaultHandler);
  }

  @Override
  public void handle(Message message) {

    JsonObject incoming = (JsonObject) message.body();
    String from = incoming.getString(MessageUtils.FROM);
    String to = incoming.getString(MessageUtils.TO);
    String type = incoming.getString(MessageUtils.TYPE);
    JsonObject object = new JsonObject();
    message.reply(MessageUtils.createLeadsMessage(object,from,to));
    LeadsMessageHandler handler = handlers.get(to);
    if(handler == null)
      handlers.get("default");
    handler.handle((JsonObject)message.body());
  }

  public void register(String groupId, LeadsMessageHandler handler) {
    LeadsMessageHandler oldHandler = handlers.remove(groupId);
    if(oldHandler != null)
      oldHandler =null;
    handlers.put(groupId,handler);
  }

  public void unregister(String groupId) {
    handlers.remove(groupId);
  }

  public LeadsMessageHandler getDefaultHandler() {
    return getHandler("default") ;
  }
  public LeadsMessageHandler getHandler(String groupId){
    LeadsMessageHandler result = handlers.get(groupId);
    if(result == null)
      result = getDefaultHandler();
    return result;
  }
}
