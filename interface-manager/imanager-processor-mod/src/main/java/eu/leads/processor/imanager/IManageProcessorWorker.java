package eu.leads.processor.imanager;

import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.ActionStatus;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.imanager.handlers.*;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vagvaz on 8/6/14.
 */
public class IManageProcessorWorker extends Verticle implements Handler<Message<JsonObject>> {
   Node com;
   String id;
   String gr;
   String workqueue;
   String logic;
   JsonObject config;
   EventBus bus;
   LeadsMessageHandler leadsHandler;
   LogProxy log;
   PersistenceProxy persistence;
   Map<String,ActionHandler> handlers;

   @Override
   public void start() {
      super.start();
      leadsHandler = new LeadsMessageHandler() {
         @Override
         public void handle(JsonObject event) {
            if (event.getString("type").equals("unregister")) {
               JsonObject msg = new JsonObject();
               msg.putString("processor", id + ".process");
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
      com.initialize(id, gr, null, leadsHandler, leadsHandler, vertx);
      bus.registerHandler(id + ".process", this);
      persistence = new PersistenceProxy(config.getString("persistence"),com,vertx);
      persistence.start();
      JsonObject msg = new JsonObject();
      msg.putString("processor", id + ".process");
      handlers = new HashMap<String,ActionHandler>();
      handlers.put(IManagerConstants.GET_OBJECT,new GetObjectActionHandler(com,log,persistence,id));
      handlers.put(IManagerConstants.PUT_OBJECT,new PutObjectActionHandler(com,log,persistence,id));
      handlers.put(IManagerConstants.GET_QUERY_STATUS,new GetQueryStatusActionHandler(com,log,persistence,id));
      handlers.put(IManagerConstants.GET_RESULTS,new GetResultsActionHandler(com,log,persistence,id));
      handlers.put(IManagerConstants.CREATE_NEW_QUERY,new CreateQueryActionHandler(com,log,persistence,id));
      handlers.put(IManagerConstants.CREATE_NEW_SPECIAL_QUERY,new CreateSpecialQueryActionHandler(com,log,persistence,id));
      log = new LogProxy(config.getString("log"),com);

      bus.send(workqueue + ".register", msg, new Handler<Message<JsonObject>>() {
         @Override
         public void handle(Message<JsonObject> event) {
            log.info("Registration " + event.toString());
         }
      });
   }

   @Override
   public void handle(Message<JsonObject> message) {
      try {
         JsonObject body = message.body();
         if (body.containsField("type")) {
            if (body.getString("type").equals("action")) {
               Action action = new Action(body);
               ActionHandler ac = handlers.get(action.getLabel());
               Action result = ac.process(action);
               result.setStatus(ActionStatus.COMPLETED.toString());
               com.sendTo(logic,result.asJsonObject());
               message.reply();
            }
         } else {
            log.error(id + " received message from eventbus that does not contain type field  \n" + message.toString());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
