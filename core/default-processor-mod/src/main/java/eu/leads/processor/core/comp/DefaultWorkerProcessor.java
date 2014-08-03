package eu.leads.processor.core.comp;

import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.Node;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * Created by vagvaz on 7/31/14.
 */
public class DefaultWorkerProcessor extends Verticle implements Handler<Message>{

   Node com;
   String id;
   String gr;
   String workqueue;
   String logic;
   JsonObject config;
   EventBus bus;
   LeadsMessageHandler leadsHandler;
   @Override
   public void start() {
      super.start();
      leadsHandler = new LeadsMessageHandler() {
         @Override
         public void handle(JsonObject event) {
            if(event.getString("type").equals("unregister")){
               JsonObject msg = new JsonObject();
               msg.putString("processor",id+".process");
               com.sendWithEventBus(workqueue + ".unregister", msg);
               stop();
            }
         }
      };
      bus = vertx.eventBus();
      config = container.config();
      id = config.getString("id");
      gr = config.getString("group");
      logic = config.getString("logic");
      workqueue = config.getString("workqueue");
      com = new DefaultNode();
      com.initialize(id,gr,null,leadsHandler,leadsHandler,vertx);
      bus.registerHandler(id+".process",this);
      JsonObject msg = new JsonObject();
      msg.putString("processor",id+".process");
      bus.send(workqueue + ".register", msg, new Handler<Message<JsonObject>>() {
         @Override
         public void handle(Message<JsonObject> event) {
            System.out.println("Registration " + event.toString());
         }
      });
   }



   @Override
   public void handle(Message message) {
      JsonObject msg = (JsonObject) message.body();
      if(msg.getString("type").equals("pingpong")){
         long l = Long.parseLong(msg.getString("count"));
         l++;
         msg.putString("count",Long.toString(l));
         container.logger().info("Received pingpong " + l);
         try { //Simulate heavy work.
            Thread.sleep(500);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      com.sendTo(msg.getString("replyTo"),msg);
      message.reply(new JsonObject());
   }
}
