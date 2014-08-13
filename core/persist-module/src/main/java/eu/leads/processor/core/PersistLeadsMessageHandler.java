package eu.leads.processor.core;

import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Created by vagvaz on 8/11/14.
 */
public class PersistLeadsMessageHandler implements Handler<Message<JsonObject>>, LeadsMessageHandler {
   PersistenceVerticle owner;
   Node bus;
   LogProxy logUtil;
   public PersistLeadsMessageHandler(PersistenceVerticle o,Node com,LogProxy log){
      owner = o;
      bus = com;
      logUtil = log;
   }


      @Override
      public void handle(JsonObject message) {

         JsonObject reply = myhandle(message);
         bus.sendTo(message.getString("from"),reply);
      }

   public JsonObject myhandle(JsonObject message){
      JsonObject msg = message;
      JsonObject reply = new JsonObject().putString("status","fail");

      String actionType = msg.getString("action");
      if (actionType.equals("get")) {
         reply = owner.getAction(msg);
      } else if (actionType.equals("put")) {
         reply = owner.putAction(msg);
      } else if (actionType.equals("read")) {
         reply = owner.readAction(msg);
      } else if (actionType.equals("store")) {
         reply = owner.storeAction(msg);
      } else if (actionType.equals("batchGet")){
         reply = owner.getBactchAction(msg);
      } else if (actionType.equals("contains")){
         reply = owner.getContainsAction(msg);
      }
      else {
         logUtil.error("Unknown ActionType " + actionType + " Cannot Handle in Persist");

      }
      return reply;
   }
   @Override
   public void handle(Message<JsonObject> msg) {
      System.err.println("reply");
      msg.reply(myhandle(msg.body()));
   }
}
